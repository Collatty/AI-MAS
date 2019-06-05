package components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

import components.state.Box;
import components.state.Goal;
import components.state.State;
import components.state.Tile;

public class BlackBoard extends SubmissionPublisher<MessageToAgent> {
    private static BlackBoard blackBoard = new BlackBoard();
    private List<Task> unsolvedTasks = new ArrayList<>();
    private List<Long> submittedTasks = new ArrayList<>();
    private Map<Long, List<HeuristicProposal>> heuristicProposalMap = new HashMap<>();
    private Map<Long, Task> taskMap = new HashMap<>();
    private Map<Integer, Integer> heuristicPenaltyMap = new HashMap<>();
    private List<State> states = new ArrayList<>();
    private List<List<Action>> acceptedPlans = new ArrayList<>();
    private ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();

    public List<String> getOutputStrings() {
	return outputStrings;
    }

    private List<String> outputStrings = new ArrayList<>();

    private BlackBoard() {
    }

    public static void setNewBlackboard() {
	blackBoard = new BlackBoard();
    }

    public void setTasks(List<Task> tasks) {
	this.unsolvedTasks = tasks;
	this.taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
    }

    private void populateAcceptedPlans() {
	for (int i = 0; i < State.getInitialAgents().size(); i++) {
	    acceptedPlans.add(new CopyOnWriteArrayList<>());
	}
    }

    public void run() {
	this.start(State.getInitialAgents());
	Message nextMessage;

	while (!State.isSolved()) {
	    while ((nextMessage = messagesToBlackboard.poll()) != null && !State.isSolved()) {
		String messageType = nextMessage.getClass().getSimpleName();
		if (messageType.equals(HeuristicProposal.class.getSimpleName())) {
		    HeuristicProposal hp = (HeuristicProposal) nextMessage;
		    List<HeuristicProposal> hpArray = new ArrayList<>();
		    if (heuristicProposalMap.containsKey(hp.getTaskID())) {
			hpArray = heuristicProposalMap.get(hp.getTaskID());
		    }
		    HeuristicProposal oldHeuristicProposal = null;
		    for (HeuristicProposal heuristicProposal : hpArray) {
			if (heuristicProposal.getA() == hp.getA()) {
			    oldHeuristicProposal = heuristicProposal;
			}
		    }
		    hpArray.remove(oldHeuristicProposal);
		    hpArray.add(hp);
		    heuristicProposalMap.put(hp.getTaskID(), hpArray);

		    // Check if all the relevant agents have send a heuristic for this task
		    if (taskMap.get(hp.getTaskID()).getBox().getReachableAgents().size() == hpArray.size()) {
			delegateTask(hpArray);
		    }
		} else if (messageType.equals(PlanProposal.class.getSimpleName())) {
		    handlePlanPropsal((PlanProposal) nextMessage);

		} else if (messageType.equals(AbortTaskMessage.class.getSimpleName())) {
		    AbortTaskMessage abort = (AbortTaskMessage) nextMessage;
		    taskMap.get(abort.getTask().getId()).setSolved(true);
		    unsolvedTasks.remove(taskMap.get(abort.getTask().getId()));
		    submitTasks();
		}
		checkCompleted();
	    }
	}

	appendNoOpAction();

	stringBuilder();
	for (String string : this.outputStrings) {
	    System.out.println(string);
	}
    }

    private void handlePlanPropsal(PlanProposal pp) {
	Tile tile = states.get(states.size() - 1).getCurrentTiles().get(pp.getAgentStartRow())
		.get(pp.getAgentStartCol());
	Agent agentInTile = !tile.hasAgent() ? null : (Agent) (tile.getTileOccupant());
	if (agentInTile == null || agentInTile.getAgentNumber() != pp.getAgent().getAgentNumber()) {
	    // Agent has moved since it started planning
	    submittedTasks.remove(pp.getTask().getId());
	    submitTasks();
	    return;
	}

	Agent agent = agentInTheWay(pp.getActions());
	Box box = boxInTheWay(pp.getActions(), pp.getTask().getBox());
	List<State> newStates = newStatesGeneratorAndConflictChecker(pp.getActions(),
		this.acceptedPlans.get(pp.getAgent().getAgentNumber()).size());
	if (newStates == null) { // CONFLICT
	    if (agent != null && !agent.equals(pp.getAgent())) {
		Task task = new Task.MoveAgentTask(agent.getColor(), new ArrayList<>(), agent.getAgentNumber(),
			pp.getActions());
		this.taskMap.get(pp.getTask().getId()).getDependencies().add(task.getId());
		this.taskMap.put(task.getId(), task);
		submittedTasks.remove(pp.getTask().getId());
		this.submit(new MessageToAgent(false, null, agent.getAgentNumber(), MessageType.PLAN, task));
	    } else if (box != null && pp.getTask().getBox() != null && !pp.getTask().getBox().equals(box)) {
		Task task = new Task.MoveBoxTask(box.getColor(), new ArrayList<>(), pp.getActions(), box);
		this.taskMap.get(pp.getTask().getId()).getDependencies().add(task.getId());
		this.taskMap.put(task.getId(), task);
		submittedTasks.remove(pp.getTask().getId());
		for (Agent a : box.getReachableAgents()) {
		    submit(new MessageToAgent(false, null, a.getAgentNumber(), MessageType.PLAN, task));
		}
	    } else {
		pp.getAgent().replan();
	    }
	} else {
	    acceptedPlans.get(pp.getAgent().getAgentNumber()).addAll(pp.getActions());
	    updateStates(newStates, acceptedPlans.get(pp.getAgent().getAgentNumber()).size() - pp.getActions().size());
	    this.taskMap.get(pp.getTask().getId()).setSolved(true);
	    this.unsolvedTasks.remove(this.taskMap.get(pp.getTask().getId()));
	    pp.getAgent().executePlan(pp);
	    submitTasks();
	}
    }

    // Delegate task to agents
    private void delegateTask(List<HeuristicProposal> hpArray) {
	HeuristicProposal hpChosen = null;

	// Find best heuristic proposed
	for (HeuristicProposal hp : hpArray) {
	    int currentPenalty = heuristicPenaltyMap.get(hp.getA().getAgentNumber());
	    if (hpChosen == null || (currentPenalty + hp.getH().getHeuristic() < hpChosen.getH().getHeuristic())) {
		hpChosen = hp;
	    }
	}

	heuristicPenaltyMap.put(hpChosen.getA().getAgentNumber(),
		hpChosen.getH().getHeuristic() + heuristicPenaltyMap.get(hpChosen.getA().getAgentNumber()));
	this.submit(new MessageToAgent(false, null, hpChosen.getA().getAgentNumber(), MessageType.PLAN,
		this.taskMap.get(hpChosen.getTaskID())));
    }

    private void submitTasks() {
	for (Task task : this.unsolvedTasks) {
	    if (submittedTasks.contains(task.getId())) {
		continue;
	    }
	    if (task.getDependencies().size() == 0) {
		if (task instanceof Task.MoveAgentTask) {
		    this.submit(new MessageToAgent(false, null, ((Task.MoveAgentTask) task).getAgentNumber(),
			    MessageType.PLAN, task));
		    submittedTasks.add(task.getId());
		} else {
		    this.submitHeuristicTask(task);
		    submittedTasks.add(task.getId());
		}
	    } else {
		boolean solvedDeps = true;
		for (Long ID : task.getDependencies()) {
		    if (!this.taskMap.get(ID).isSolved()) {
			solvedDeps = false;
			break;
		    }
		}
		if (solvedDeps) {
		    if (task instanceof Task.MoveAgentTask) {
			this.submit(new MessageToAgent(false, null, ((Task.MoveAgentTask) task).getAgentNumber(),
				MessageType.PLAN, task));
			submittedTasks.add(task.getId());
		    } else {
			this.submitHeuristicTask(task);
			submittedTasks.add(task.getId());
		    }
		}
	    }
	}
    }

    private void submitHeuristicTask(Task task) {
	for (Agent agent : task.getBox().getReachableAgents()) {
	    MessageToAgent messageToAgent = new MessageToAgent(null, null, agent.getAgentNumber(),
		    MessageType.HEURISTIC, task);
	    this.submit(messageToAgent);
	}
    }

    private void start(List<Agent> agents) {
	populateAcceptedPlans();
	this.states.add(State.getState());
	// Setup agents
	for (Agent a : agents) {
	    heuristicPenaltyMap.put(a.getAgentNumber(), 0);
	    this.subscribe(a);
	}
	submitTasks();
    }

    private Agent agentInTheWay(List<Action> actions) {
	Map<Tile, Agent> occupiedTiles = new HashMap<>();
	for (Agent agent : State.getInitialAgents()) {
	    List<Action> acceptedPlan = acceptedPlans.get(agent.getAgentNumber());
	    if (acceptedPlan.size() == 0) {
		occupiedTiles.put(State.getInitialState().get(agent.getRow()).get(agent.getCol()), agent);
	    } else {
		occupiedTiles.put(
			State.getInitialState().get(acceptedPlan.get(acceptedPlan.size() - 1).getEndAgent().getRow())
				.get(acceptedPlan.get(acceptedPlan.size() - 1).getEndAgent().getCol()),
			agent);
	    }
	}

	for (Action action : actions) {
	    if (occupiedTiles.keySet().contains(action.getEndBox())) {
		return occupiedTiles.get(action.getEndBox());
	    } else if (occupiedTiles.keySet().contains(action.getEndAgent())) {
		return occupiedTiles.get(action.getEndAgent());
	    }
	}
	return null;
    }

    private Box boxInTheWay(List<Action> actions, Box boxUsed) {
	Map<Tile, Box> occupiedTiles = new HashMap<>();
	for (Box box : State.getBoxes()) {
	    if (!box.equals(boxUsed)) {
		occupiedTiles.put(State.getInitialState().get(box.getRow()).get(box.getCol()), box);
	    }
	}
	for (Action action : actions) {
	    if (occupiedTiles.keySet().contains(action.getEndBox())) {
		return occupiedTiles.get(action.getEndBox());
	    } else if (occupiedTiles.keySet().contains(action.getEndAgent())) {
		return occupiedTiles.get(action.getEndAgent());
	    }
	}
	return null;
    }

    private void appendNoOpAction() {
	int maxSize = 0;
	for (List<Action> acceptedPlan : this.acceptedPlans) {
	    if (acceptedPlan.size() > maxSize) {
		maxSize = acceptedPlan.size();
	    }
	}

	for (List<Action> acceptedPlan : this.acceptedPlans) {
	    if (acceptedPlan.size() < maxSize) {
		List<Action> noOps = new ArrayList<>();
		for (int i = 0; i < maxSize - acceptedPlan.size(); i++) {
		    if (acceptedPlan.isEmpty()) {
			noOps.add(new Action(State.getInitialState()
				.get(State.getInitialAgents().get(this.acceptedPlans.indexOf(acceptedPlan)).getRow())
				.get(State.getInitialAgents().get(this.acceptedPlans.indexOf(acceptedPlan)).getCol())));
		    } else {
			noOps.add(new Action(acceptedPlan.get(acceptedPlan.size() - 1).getEndAgent()));
		    }
		}
		acceptedPlan.addAll(noOps);
	    }
	}
    }

    public ConcurrentLinkedQueue<Message> getMessagesToBlackboard() {
	return messagesToBlackboard;
    }

    public static BlackBoard getBlackBoard() {
	return blackBoard;
    }

    public String toString() {
	return "Blackboard";
    }

    private void checkCompleted() {
	for (Goal goal : State.getGoals()) {
	    if (!goal.isCompleted()) {
		return;
	    }
	}
	State.setSolved(true);
    }

    private void updateStates(List<State> newStates, int indexFrom) {
	this.states = this.states.subList(0, indexFrom);
	this.states.addAll(newStates);
    }

    // Generates new states by applying actions. Returns null if there is a conflict
    private List<State> newStatesGeneratorAndConflictChecker(List<Action> actions, int indexFrom) {
	int curStateIndex = 0;
	List<State> newStates = new ArrayList<>();
	State nextState = new State(this.states.get(indexFrom));
	newStates.add(new State(nextState));

	for (Action action : actions) {
	    nextState.setUsedInLastActionTilesFree();
	    if (!nextState.isLegalMove(action)) {
		return null;
	    }

	    nextState.makeMove(action, true);
	    curStateIndex = indexFrom + actions.indexOf(action);
	    for (List<Action> agentPlan : acceptedPlans) {
		if (agentPlan.size() > curStateIndex) {
		    if (!nextState.isLegalMove(agentPlan.get(curStateIndex))) {
			return null;
		    }
		    nextState.makeMove(agentPlan.get(curStateIndex), true);
		}
	    }
	    newStates.add(new State(nextState));
	}

	for (int i = curStateIndex + 1; i < this.states.size(); i++) {
	    nextState.setUsedInLastActionTilesFree();
	    for (List<Action> agentPlan : acceptedPlans) {
		if (agentPlan.size() > i) {
		    if (!nextState.isLegalMove(agentPlan.get(i))) {
			return null;
		    }
		    nextState.makeMove(agentPlan.get(i), true);
		}
	    }
	    newStates.add(new State(nextState));
	}

	return newStates;
    }

    public void stringBuilder() {
	for (int i = 0; i < acceptedPlans.get(0).size(); i++) {
	    StringBuilder stringBuilder = new StringBuilder();
	    for (int j = 0; j < State.getInitialAgents().size(); j++) {
		stringBuilder.append(acceptedPlans.get(j).get(i));
		stringBuilder.append(";");
	    }
	    this.outputStrings.add(stringBuilder.substring(0, stringBuilder.toString().length() - 1));
	}
    }
}
