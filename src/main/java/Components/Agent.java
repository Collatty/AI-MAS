package Components;

import java.util.ArrayList;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import AI.Heuristic;
import Components.State.State;
import Components.State.Goal;


public class Agent implements Subscriber<MessageToAgent> {


    private final int agentNumber;
    private final Color color;
    private int col;
    private int row;
    private Subscription blackboardChannel;
    private BlackBoard blackBoard;

    private boolean workingOnPlan;

    public Agent(int agentNumber, Color color, int row, int col, BlackBoard blackBoard) {
	this.agentNumber = agentNumber;
	this.color = color;
	this.row = row;
	this.col = col;
	this.blackBoard = blackBoard;
	this.workingOnPlan = false;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
	this.blackboardChannel = subscription;
	this.blackboardChannel.request(1);
    }

    @Override
    public void onNext(MessageToAgent message) {
	System.err.println("Agent " + agentNumber + " received a message " + message.getToColor());
	if (message.getToAll() != null && message.getToAll() || message.getToColor() != null && message.getToColor() == color
		|| message.getToAgent() != null && message.getToAgent() == agentNumber) {
	    if (message.getMessageType() == MessageType.HEURISTIC) {
		System.err.println("Agent " + agentNumber + " creates h for task " + message.getTask().getId());
		proposeHeuristic(calculateHeuristic(message.getTask()), message.getTask());
	    } else if (message.getMessageType() == MessageType.PLAN) {
		// TODO: Make awesome plan
		System.err.println("Agent " + agentNumber + " is planning for task " + message.getTask().getId());
		createPlan(message.getTask());
		// System.err.println("Agent " + agentNumber + " makes plan for task " +
		// message.task.id);
	    }
	}
	this.blackboardChannel.request(1);
    }

    private void createPlan(Task task) {
	workingOnPlan = true;
	long startIndex = 0;
	for (Long dependencyId : task.getDependencies()) {
	    PlanProposal acceptedPlan = blackBoard.getAcceptedPlan(dependencyId);
	    if (acceptedPlan != null) {
		startIndex = Math.max(startIndex, acceptedPlan.getEndIndex() + 1);
	    }
	}

	ArrayList<Action> actions = new ArrayList<>();

	// TODO: implement properly
	if (task.getId() == 1) {
	    Action a = new Action(); //TODO
	    boolean add = actions.add(a);
	} else { // when task.getId() is 2
	    Action a = new Action();//TODO
	    Action a2 = new Action();//TODO
	    actions.add(a);
	    actions.add(a2);
	}

	long endIndex = startIndex + actions.size() - 1;
	PlanProposal pp = new PlanProposal(actions, this, task.getId(), startIndex, endIndex);
	blackBoard.getMessagesToBlackboard().add(pp);
	workingOnPlan = false;
    }

    private void proposeHeuristic(int h, Task t) {
	blackBoard.getMessagesToBlackboard().add(new HeuristicProposal(h, this, t.getId()));
    }

    private int calculateHeuristic(Task task) {
		return getHeuristic(State.getState(), task.getGoal());
    }

    public int getHeuristic(State state, Goal goal){
        return Heuristic.h(state, this, goal);
    }

    @Override
    public void onError(Throwable e) {
	System.err.println("Some error happened in agent " + agentNumber + " subscription:");
	e.printStackTrace();
    }

    @Override
    public void onComplete() {
	// System.out.println("All Processing Done");
    }

    @Override
    public String toString() {
        return Integer.toString(getAgentNumber());
    }

    //GETTERS
    public int getAgentNumber() {
	return agentNumber;
    }

    public Color getColor() {
	return color;
    }

    public int getRow() {
	return row;
    }

    public int getCol() {
	return col;
    }

    public boolean getWorking() {
	return workingOnPlan;
    }
    // END GETTERS

    // SETTERS
    public void setRow(int row) {
	this.row = row;
    }

    public void setCol(int col) {
	this.col = col;
    }

    public void setWorking(boolean working) {
	this.workingOnPlan = working;
    }
    // END SETTERS

}
