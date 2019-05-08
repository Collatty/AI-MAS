package Components;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

import Components.State.Goal;
import Components.State.State;
import Components.State.Tile;

public class BlackBoard extends SubmissionPublisher<MessageToAgent>{

    private static BlackBoard blackBoard = new BlackBoard();
    private List<Task> tasksReadyForSubmit = new ArrayList<>();
    private List<Task> unsolvedTasks = new ArrayList<>();
    private Map<Long, List<HeuristicProposal>> heuristicProposalMap = new HashMap<>();
    private Map<Integer, ArrayList<Action>> heuristicsActionsMap = new HashMap<>();
    private Map<Color, Integer> colorAgentAmountMap = new HashMap<>();
    private Map<Long, Task> taskMap = new HashMap<>();
    private List<List<Action>> acceptedPlans = new ArrayList<>(10);
    private ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();

    private BlackBoard() {
    }

    //Testing purposes
    public static void setNewBlackboard() {
        blackBoard = new BlackBoard();
    }


    public void setTasks(List<Task> tasks) {
        this.unsolvedTasks = tasks;
        this.taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
    }

    private void populateAcceptedPlans(){
        for (int i = 0; i<State.getState().getAgents().size(); i++) {
            acceptedPlans.add(new ArrayList<>());
        }
    }

    public void run() {
        this.start(State.getState().getAgents());
	    Message nextMessage;
        System.err.println("Blackboard running");



        while (!State.isSolved()) {
            while ((nextMessage = messagesToBlackboard.poll()) != null) {
            String messageType = nextMessage.getClass().getSimpleName();
                if (messageType.equals(HeuristicProposal.class.getSimpleName())) {
                    HeuristicProposal hp = (HeuristicProposal) nextMessage;
                    System.err
                        .println("Agent " + hp.getA().getAgentNumber() + " proposes " + hp.getH() + " actions for " +
                                "task " + hp.getTaskID());
                    List<HeuristicProposal> hpArray = new ArrayList<>();
                    if (heuristicProposalMap.containsKey(hp.getTaskID())) {
                        hpArray = heuristicProposalMap.get(hp.getTaskID());
                        }
                    hpArray.add(hp);
                    heuristicProposalMap.put(hp.getTaskID(), hpArray);

                    // Check if all agents of a given color have send a heuristic for this task
                    if (colorAgentAmountMap.get(hp.getA().getColor()) == hpArray.size()) {
                        delegateTask(hpArray);
                        if (!tasksReadyForSubmit.isEmpty()) {
                            submitHeuristicTask(tasksReadyForSubmit.remove(0));
                        } else {
                            System.err.print("All tasks are delegated, time for planning!");
                        }
                    }
                } else if (messageType.equals(PlanProposal.class.getSimpleName())) {
                    PlanProposal pp = (PlanProposal) nextMessage;
                    System.err.println(pp.toString());
                    Agent agent = agentInTheWay(pp.getActions());
                    if(conflict(pp.getActions())){
                        pp.getA().replan();
                    } else if (agent != null) {
                        System.err.println("Agent" + agent.toString() + " is in the way!");
                        Task task = new Task.MoveTask(agent.getColor(), new ArrayList<>(), pp.getActions());
                        this.taskMap.get(pp.getTaskID()).getDependencies().add(task.getId());
                        this.taskMap.put(task.getId(), task);
                        this.submit(new MessageToAgent(false, agent.getColor(), agent.getAgentNumber(),
                                MessageType.PLAN, task, null));
                    } else {
                        if (acceptedPlans.get(pp.getA().getAgentNumber()).size() == 0) {
                            acceptedPlans.remove(pp.getA().getAgentNumber());
                            acceptedPlans.add(pp.getA().getAgentNumber(), pp.getActions());
                            this.taskMap.get(pp.getTaskID()).setSolved(true);
                            this.unsolvedTasks.remove(this.taskMap.get(pp.getTaskID()));
                        } else {
                            acceptedPlans.get(pp.getA().getAgentNumber()).addAll(pp.getActions());
                            this.taskMap.get(pp.getTaskID()).setSolved(true);
                            this.unsolvedTasks.remove(this.taskMap.get(pp.getTaskID()));
                        }
                        for (Task task : this.unsolvedTasks) {
                            if (task.getDependencies().size() == 0) {
                                if (task instanceof Task.MoveTask) {
                                    this.submit(new MessageToAgent(false, task.getColor(), null,
                                            MessageType.PLAN, task, null));
                                } else {
                                    this.submitHeuristicTask(task);
                                }
                            }
                            boolean solvedDeps = true;
                            for (Long ID : task.getDependencies()) {
                                if(!this.taskMap.get(ID).isSolved()) {
                                    solvedDeps = false;
                                    break;
                                }
                            }
                            if (solvedDeps) {
                                if (task instanceof Task.MoveTask) {
                                    this.submit(new MessageToAgent(false, task.getColor(), null,
                                            MessageType.PLAN, task, null));
                                } else {
                                    this.submitHeuristicTask(task);
                                }
                            }
                        }
                    }
                }
                checkCompleted();
            }
        }
        appendNoOpAction();
        System.err.println("Blackboard finished");
    }

    // Delegate task to agents
    private void delegateTask(List<HeuristicProposal> hpArray) {
        HeuristicProposal hpChosen = null;
        Long minEndIndex = null;
        Long endIndex = null;
        long startIndex = 0;
        long maxDependencyEndIndex = 0;
        long agentPenalty;


        // Find best heuristic proposed
       for (HeuristicProposal hp : hpArray) {
           /*agentPenalty = heuristicsActionsMap.get(hp.getA().getAgentNumber()).size();
           startIndex = Math.max(agentPenalty, maxDependencyEndIndex);
           endIndex = startIndex + hp.getH().heuristic;

           if (minEndIndex == null || minEndIndex > endIndex) {
               minEndIndex = endIndex;*/
          hpChosen = hp;
        }

        System.err.print("Blackboard has chosen a heuristic proposal: ");
        this.submit(new MessageToAgent(false, hpChosen.getA().getColor(), hpChosen.getA().getAgentNumber(),
                MessageType.PLAN, this.taskMap.get(hpChosen.getTaskID()), hpChosen.getH().block));
    }

    private void submitHeuristicTask(Task task) {
        System.err.println("Blackboard submits task with id " + task.getId() + " and color " + task.getColor());
        MessageToAgent messageToAgent = new MessageToAgent(null, task.getColor(), null, MessageType.HEURISTIC, task,
                null);
        this.submit(messageToAgent);
    }

    private boolean conflict(List<Action> actions) {
        int counter = 0;
	    for (Action action : actions) {
	        Collection<Tile> suggestedTiles = new HashSet<>();
	        suggestedTiles.add(action.getStartAgent());
	        suggestedTiles.add(action.getEndAgent());
	        suggestedTiles.add(action.getStartBox());
	        suggestedTiles.add(action.getEndBox());
	        for (List<Action> acceptedPlan : acceptedPlans) {
	            if (acceptedPlan != null && acceptedPlan.size() > 0 && counter < acceptedPlan.size()) {
                    Collection<Tile> usedTiles = new HashSet<>();
                    usedTiles.add(acceptedPlan.get(counter).getEndBox());
                    usedTiles.add(acceptedPlan.get(counter).getStartBox());
                    usedTiles.add(acceptedPlan.get(counter).getStartAgent());
                    usedTiles.add(acceptedPlan.get(counter).getEndAgent());
                    for (Tile tile : suggestedTiles) {
                        if (tile != null && usedTiles.contains(tile)) {
                            return true;
                        }
                    }
                }

            }
	    counter++;
        }
	return false;
    }

    private void start(List<Agent> agents) {
        // Setup agents
        for (Agent a : agents) {
            Thread thread = new Thread(a);
            thread.start();
            if (!colorAgentAmountMap.containsKey(a.getColor())) {
                colorAgentAmountMap.put(a.getColor(), 1);
            } else {
                colorAgentAmountMap.put(a.getColor(), colorAgentAmountMap.get(a.getColor()) + 1);
            }
            this.subscribe(a);
        }
        // Find tasks with no dependencies
        Iterator<Task> taskItr = unsolvedTasks.iterator();
        Collection<Task> submittedTasks = new ArrayList<>();
        while (taskItr.hasNext()) {
            Task task = taskItr.next();
            if (task.getDependencies().isEmpty()) {
                tasksReadyForSubmit.add(task);
                submittedTasks.add(task);
            }
	    }

        // Submit first task
        submitHeuristicTask(tasksReadyForSubmit.remove(0));
        populateAcceptedPlans();
    }

    private Agent agentInTheWay(List<Action> actions) {
        Map<Tile, Agent> occupiedTiles = new HashMap();
        for (List<Action> acceptedPLan : acceptedPlans) {
            Agent agent = State.getAgentByNumber(acceptedPlans.indexOf(acceptedPLan));
            if (acceptedPLan.size() == 0) {
                occupiedTiles.put(State.getInitialState().get(agent.getRow()).get(agent.getCol()), agent);
            } else {
                occupiedTiles
                        .put(State.getInitialState()
                        .get(acceptedPLan.get(acceptedPLan.size()-1).getEndAgent().getRow())
                        .get(acceptedPLan.get(acceptedPLan.size()-1).getEndAgent().getCol()), agent);
            }
        }
        for (Action action : actions) {
            if (occupiedTiles.entrySet().contains(action.getEndBox())){
                return occupiedTiles.get(action.getEndBox());
            } else if (occupiedTiles.entrySet().contains(action.getEndAgent())) {
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
                    if(acceptedPlan.isEmpty()) {
                        noOps.add(new Action(
                                State.getInitialState().get(
                                State.getState().getAgents().get(this.acceptedPlans.indexOf(acceptedPlan)).getRow())
                                .get(State.getState().getAgents().get(this.acceptedPlans.indexOf(acceptedPlan)).getCol())));
                    } else {
                        noOps.add(new Action(acceptedPlan.get(acceptedPlan.size() - 1).getEndAgent()));
                    }
                }
                acceptedPlan.addAll(noOps);
            }


        }
    }

    public List<Task> getUnsolvedTasks() {
        return unsolvedTasks;
    }
//

    public ConcurrentLinkedQueue<Message> getMessagesToBlackboard() {
        return messagesToBlackboard;
    }

    public static BlackBoard getBlackBoard() {
        return blackBoard;
    }

    public String toString() {
        return "Blackboard";
    }

    public void checkCompleted() {
        for (Goal goal : State.getGoals()) {
            if (!goal.isCompleted()) {
                return;
            }
        }
        State.setSolved(true);
    }

    public List<List<Action>> getAcceptedPlans() {
        return acceptedPlans;
    }
}
