package Components;

import java.util.ArrayList;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import AI.Heuristic;
import AI.HeuristicAndBlock;
import AI.Plan;
import Components.State.Block;
import Components.State.State;
import Components.State.Goal;


public class Agent implements Subscriber<MessageToAgent>, Runnable{


    private final int agentNumber;
    private final Color color;
    private int col;
    private int row;
	private Subscription blackboardChannel;
    private BlackBoard blackBoard;

    private boolean workingOnPlan;

    public Agent(int agentNumber, Color color, int row, int col) {
	this.agentNumber = agentNumber;
	this.color = color;
	this.row = row;
	this.col = col;
	this.blackBoard = BlackBoard.getBlackBoard();
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
			this.createPlan(message.getTask(), message.getBlock());
		System.err.println("Agent " + agentNumber + " is planning for task " + message.getTask().getId());
		// System.err.println("Agent " + agentNumber + " makes plan for task " +
		// message.task.id);
	    }
	}
	this.blackboardChannel.request(1);
    }

    private void createPlan(Task task, Block block) {
		workingOnPlan = true;
		if (block != null) {
			Plan.MoveBoxPlan moveBoxPlan = new Plan.MoveBoxPlan(this.getRow(), this.getCol(),
					block.getRow(), block.getCol(), task.getGoal().getRow(),
					task.getGoal().getCol());
			this.blackBoard.getMessagesToBlackboard().add(new PlanProposal(moveBoxPlan.getPlan(), this, task.getId(),
					-1, -1));
			for (Action a: moveBoxPlan.getPlan()) {
				System.err.println(a.toString());
			}
		}
		else {
			Plan.MovePlan movePlan; //TODO
		}
		workingOnPlan = false;
    }

    private void proposeHeuristic(HeuristicAndBlock h, Task t) {
		blackBoard.getMessagesToBlackboard().add(new HeuristicProposal(h, this, t.getId()));
    }

    private HeuristicAndBlock calculateHeuristic(Task task) {
		return getHeuristic(State.getState(), task.getGoal());
    }

    public HeuristicAndBlock getHeuristic(State state, Goal goal){
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

	public Subscription getBlackboardChannel() {
		return blackboardChannel;
	}

	public BlackBoard getBlackBoard() {
		return blackBoard;
	}

	public boolean isWorkingOnPlan() {
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

	@Override
	public void run() {
		System.err.println("Agent " + this.toString() + "'s thread is up and running");
	}
	// END SETTERS

}
