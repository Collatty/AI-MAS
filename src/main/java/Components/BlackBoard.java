package Components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

import Components.State.State;

public class BlackBoard implements Runnable {
    private Thread t;
    private List<Task> tasksReadyForSubmit = new ArrayList<>();
    private List<Task> tasksNotSubmitted = new ArrayList<>();
    private HashMap<Long, ArrayList<HeuristicProposal>> heuristicProposalMap = new HashMap<>();
    private HashMap<Integer, ArrayList<Action>> acceptedActionsMap = new HashMap<>();
    private HashMap<Integer, ArrayList<Action>> heuristicsActionsMap = new HashMap<>();
    private HashMap<Color, Integer> colorAgentAmountMap = new HashMap<>();
    private SubmissionPublisher<MessageToAgent> publisher = new SubmissionPublisher<>();
    private HashMap<Long, PlanProposal> delegatedTasksMap = new HashMap<>();
    private Map<Long, Task> taskMap = new HashMap<>();
    private ArrayList<State> stateSequence = new ArrayList<>();
    private HashMap<Long, PlanProposal> acceptedPlansMap = new HashMap<>();
    private State currentState;

    ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();

    public List<Task> getTasks() {
        return tasks;
    }

    private List<Task> tasks;
    ArrayList<Agent> agents;
    long taskCounter; // TODO: If more tasks than long can hold, problems can occur. Fix that


    // TODO: Enable state in constructor
//    public BlackBoard(List<Task> tasks, State initState) {
    public BlackBoard(List<Task> tasks) {
	this.tasksNotSubmitted = tasks;
	this.taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
//	currentState = initState; // TODO: Enable
//	stateSequence.add(initState); // TODO: Enable
    }

    @Override
    public void run() {
	Message nextMessage;

	while (true) {
	    while ((nextMessage = messagesToBlackboard.poll()) != null) {
		String messageType = nextMessage.getClass().getSimpleName();
		// Heuristic proposal received
		if (messageType == HeuristicProposal.class.getSimpleName()) {
		    HeuristicProposal hp = (HeuristicProposal) nextMessage;
		    System.err
			    .println("Agent " + hp.a.getAgentNumber() + " propose " + hp.h + " for task " + hp.taskID);
		    ArrayList<HeuristicProposal> hpArray = new ArrayList<>();
		    if (heuristicProposalMap.containsKey(hp.taskID)) {
			hpArray = heuristicProposalMap.get(hp.taskID);
		    }
		    hpArray.add(hp);
		    heuristicProposalMap.put(hp.taskID, hpArray);

		    // Check if all agents of a given color have send a heuristic for this task
		    if (colorAgentAmountMap.get(hp.a.getColor()) == hpArray.size()) {
			delegateTask(hpArray);
			if (!tasksReadyForSubmit.isEmpty()) {
			    submitTask(tasksReadyForSubmit.remove(0));
			} else {
			    System.err.print("All plans are delegated, time for planning!");
			    // TODO: All tasks are delegated, make the agents start planning
			}
		    }
		} else if (messageType == PlanProposal.class.getSimpleName()) {
		    PlanProposal pp = (PlanProposal) nextMessage;

		    // TODO check for conflict before adding to plan map

		    // If there is no conflict, input the new actions in the plan map
		    if (!conflict(pp.a.getAgentNumber(), pp.actions)) {
			// TODO: Update stateSequence

			System.err.println(pp.toString());
			// Add NoOps if necessary
			int numberOfNoOps = (int) (pp.startIndex
				- acceptedActionsMap.get(pp.a.getAgentNumber()).size());
			if (numberOfNoOps >= 0) {
			    Action[] noOpArr = new Action[numberOfNoOps];
			    Arrays.fill(noOpArr, new Action()); //TODO
			    acceptedActionsMap.get(pp.a.getAgentNumber()).addAll(Arrays.asList(noOpArr));

			    // Add actions to accepted actions
			    acceptedActionsMap.get(pp.a.getAgentNumber()).addAll(pp.actions);

			    // Add plan to accepted plans
			    acceptedPlansMap.put(pp.taskID, pp);

			    System.err.print("Current actions planned: ");
			    System.err.println(acceptedActionsMap.toString());
			} else {
			    // TODO: Agent has actions where this plan starts. Deny plan
			}
		    }
		}
	    }

	    try {
		// TODO: Consider smarter solution than "sleep"
		Thread.sleep(100);
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
    }

    // Delegate task to agents
    private void delegateTask(ArrayList<HeuristicProposal> hpArray) {
	HeuristicProposal hpChosen = null;
	Long minEndIndex = null;
	Long endIndex = null;
	long startIndex = 0;
	long maxDependencyEndIndex = 0;
	long agentPenalty;

	// Find max endIndex of dependencies
	for (Long depId : taskMap.get(hpArray.get(0).taskID).getDependencies()) {
	    maxDependencyEndIndex = Math.max(maxDependencyEndIndex, delegatedTasksMap.get(depId).endIndex);
	}

	// Find best heuristic proposed
	for (HeuristicProposal hp : hpArray) {
	    agentPenalty = heuristicsActionsMap.get(hp.a.getAgentNumber()).size();
	    startIndex = Math.max(agentPenalty, maxDependencyEndIndex);
	    endIndex = startIndex + hp.h;

	    if (minEndIndex == null || minEndIndex > endIndex) {
		minEndIndex = endIndex;
		hpChosen = hp;
	    }
	}

	System.err.print("Blackboard has chosen a heuristic proposal: ");
	hpChosen.print();

	// TODO: Update the currentState:
	// - Moving agent to new position near goal solved.
	// - Remove box used by agent.
	// - Change goal solved to wall.

	// Add NoOps to plan if necessary
	int numberOfNoOps = (int) (startIndex - heuristicsActionsMap.get(hpChosen.a.getAgentNumber()).size());
	if (numberOfNoOps > 0) {
	    Action[] noOpArr = new Action[numberOfNoOps];
	    Arrays.fill(noOpArr, new Action()); //TODO
	    heuristicsActionsMap.get(hpChosen.a.getAgentNumber()).addAll(Arrays.asList(noOpArr));
	}

	// Add NoOps instead of real actions
	Action[] unknArr = new Action[hpChosen.h];
	Arrays.fill(unknArr, new Action()); //TODO
	ArrayList<Action> unknArrList = new ArrayList<>(Arrays.asList(unknArr));
	heuristicsActionsMap.get(hpChosen.a.getAgentNumber()).addAll(unknArrList);

	System.err.println("Actions planed with heuristics: ");
	for (Integer agentId : heuristicsActionsMap.keySet()) {
	    System.err.println("Agent " + agentId + ": " + heuristicsActionsMap.get(agentId).toString());
	}

	// Save plan
	delegatedTasksMap.put(hpChosen.taskID,
		new PlanProposal(unknArrList, hpChosen.a, hpChosen.taskID, startIndex, endIndex));

	// Find tasks with solved dependencies
	Iterator<Task> taskItr = tasksNotSubmitted.iterator();
	while (taskItr.hasNext()) {
	    Task task = taskItr.next();
	    System.err.println("Task " + task.getId() + " dependencies: " + task.getDependencies().toString());
	    if (delegatedTasksMap.keySet().containsAll(task.getDependencies())) {
		System.err.println("All dependencies solved for task " + task.getId());
		tasksReadyForSubmit.add(task);
		taskItr.remove();
	    }
	}
    }

    private void submitTask(Task task) {
	task.setPrestate(currentState);
	System.err.println("Blackboard submits task with id " + task.getId() + " and color " + task.getColor());
	MessageToAgent messageToAgent = new MessageToAgent(null, task.getColor(), null, MessageType.HEURISTIC, task);
	publisher.submit(messageToAgent);
    }

    public boolean conflict(int agentNumber, ArrayList<Action> actions) {
	// TODO: implement

	return false;
    }

    public void start(ArrayList<Agent> agents) {
	if (t == null) {
	    t = new Thread(this);
	    t.start();
	}

	// Setup agents
	for (Agent a : agents) {
	    if (!colorAgentAmountMap.containsKey(a.getColor())) {
		colorAgentAmountMap.put(a.getColor(), 1);
	    } else {
		colorAgentAmountMap.put(a.getColor(), colorAgentAmountMap.get(a.getColor()) + 1);
	    }

	    publisher.subscribe(a);
	    ArrayList<Action> actions = new ArrayList<>();
	    heuristicsActionsMap.put(a.getAgentNumber(), actions);
	    acceptedActionsMap.put(a.getAgentNumber(), actions);
	}

	// Find tasks with no dependencies
	Iterator<Task> taskItr = tasksNotSubmitted.iterator();
	while (taskItr.hasNext()) {
	    Task task = taskItr.next();
	    System.err.println("Task " + task.getId() + " has color " + task.getColor());
	    if (task.getDependencies().isEmpty()) {
		System.err.println("Task " + task.getId() + " has no dependencies");
		tasksReadyForSubmit.add(task);
		taskItr.remove();
	    }
	}

	// Submit first task
	submitTask(tasksReadyForSubmit.remove(0));
    }

    public PlanProposal getAcceptedPlan(long taskID) {
	return acceptedPlansMap.get(taskID);
    }
}
