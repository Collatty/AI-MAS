package Components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SubmissionPublisher;

import Components.State.State;

public class BlackBoard implements Runnable {
    private Thread t;
    HashMap<Long, Task> todoMap = new HashMap<>();
    HashMap<Long, ArrayList<HeuristicProposal>> heuristicProposalMap = new HashMap<>();
    HashMap<Integer, ArrayList<Action>> acceptedActionsMap = new HashMap<>();
    HashMap<Color, Integer> colorAgentAmountMap = new HashMap<>();
    ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();
    SubmissionPublisher<MessageToAgent> publisher = new SubmissionPublisher<>();
    List<Task> tasks;
    ArrayList<Agent> agents;
    long taskCounter; // TODO: If more tasks than long can hold, problems can occur. Fix that
    ArrayList<State> stateSequence = new ArrayList<>();
    private HashMap<Long, PlanProposal> acceptedPlansMap = new HashMap<>();

    // TODO: add something like State initState to constructor
    public BlackBoard(List<Task> tasks) {
	taskCounter = 0;
	this.tasks = tasks;
	// stateSequence.add(initState);
    }

    @Override
    public void run() {
	Message nextMessage;

	while (true) {
	    // TODO: Consider smarter solution than "sleep"
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

		    // TODO: subtract the number of unavailable agents
		    if (colorAgentAmountMap.get(hp.a.getColor()) == hpArray.size()) {
			delegateTask(hpArray);
		    }
		} else if (messageType == PlanProposal.class.getSimpleName()) {
		    PlanProposal pp = (PlanProposal) nextMessage;

		    // TODO check for conflict before adding to plan map

		    // If there is no conflict, input the new actions in the plan map
		    if (!conflict(pp.a.getAgentNumber(), pp.actions)) {

			int numberOfNoOps = pp.startIndex - acceptedActionsMap.get(pp.a.getAgentNumber()).size();

			System.err.println(pp.toString());
			acceptedActionsMap.put(pp.a.getAgentNumber(), pp.actions);
		    }
		}
	    }
	    try {
		Thread.sleep(100);
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
    }

    // Assign task to not-working agent with best heuristic
    private void delegateTask(ArrayList<HeuristicProposal> hpArray) {
	Collections.sort(hpArray, (hp1, hp2) -> hp1.h - hp2.h);
	for (HeuristicProposal hp : hpArray) {
	    // hp.print();
	    if (!hp.a.getWorking()) {
		MessageToAgent messageToAgent = new MessageToAgent(null, null, hp.a.getAgentNumber(), MessageType.PLAN,
			todoMap.get(hp.taskID));
		todoMap.remove(hp.taskID);
		publisher.submit(messageToAgent);
		break;
	    }
	}
    }

    public void setAgents(ArrayList<Agent> agents) {
	this.agents = agents;
	calculateColorAgentAmountMap(agents);

	for (Agent a : agents) {
	    publisher.subscribe(a);
	    ArrayList<Action> actions = new ArrayList<>();
	    acceptedActionsMap.put(a.getAgentNumber(), actions);
	}

	for (Task t : tasks) {
	    if (t.getDependencies().isEmpty()) {
		t.setId(taskCounter);
		taskCounter++;
		todoMap.put(t.getId(), t);
		// System.err.println("Blackboard submits task with id " + t.id);
		MessageToAgent messageToAgent = new MessageToAgent(null, t.getColor(), null, MessageType.HEURISTIC, t);
		publisher.submit(messageToAgent);
		// tasks.remove(t); TODO: This doesn't work. Find another way.
	    }
	}
    }

    // TODO: Make nicer
    public void calculateColorAgentAmountMap(ArrayList<Agent> agents) {
	for (Agent a : agents) {
	    if (!colorAgentAmountMap.containsKey(a.getColor())) {
		colorAgentAmountMap.put(a.getColor(), 1);
	    } else {
		colorAgentAmountMap.put(a.getColor(), colorAgentAmountMap.get(a.getColor()) + 1);
	    }
	}
    }

    //
    public boolean conflict(int agentNumber, ArrayList<Action> actions) {
	// TODO: implement

	return false;
    }

    public void start() {
	if (t == null) {
	    t = new Thread(this);
	    t.start();
	}
    }

    public PlanProposal getAcceptedPlan(long taskID) {
	return acceptedPlansMap.get(taskID);
    }
}
