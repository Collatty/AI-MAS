package Components;

import java.util.*;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import AI.Heuristic;
import AI.HeuristicAndBlock;
import AI.Plan;
import Components.State.Block;
import Components.State.State;
import Components.State.Goal;
import Components.State.Tile;


public class Agent implements Subscriber<MessageToAgent>, Runnable{


    private final int agentNumber;
    private final Color color;
    private int col;
    private int row;
	private Subscription blackboardChannel;
    private BlackBoard blackBoard;
    private PlanProposal plan;
    private Task task;
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
		if (message.getToAll() != null && message.getToAll() || message.getToColor() != null && message.getToColor() == this.color
			|| message.getToAgent() != null && message.getToAgent() == agentNumber) {
			if (message.getMessageType() == MessageType.HEURISTIC) {
				System.err.println("Agent " + agentNumber + " creates h for task " + message.getTask().getId());
				proposeHeuristic(calculateHeuristic(message.getTask()), message.getTask());
			} else if (message.getMessageType() == MessageType.PLAN) {
				this.createPlan(message.getTask(), message.getBlock());
			}
		}
		this.blackboardChannel.request(1);
    }

    private void createPlan(Task task, Block block) {
    	this.task = task;
		workingOnPlan = true;
		if (block != null) {
			Plan.MoveBoxPlan moveBoxPlan = new Plan.MoveBoxPlan(this.getRow(), this.getCol(),
					block.getRow(), block.getCol(), task.getGoal().getRow(),
					task.getGoal().getCol(), this.color);
			this.plan = new PlanProposal(moveBoxPlan.getPlan(), this, task.getId(),
					-1, -1, block);
			this.blackBoard.getMessagesToBlackboard().add(plan);
			System.err.println(this.toString() + "has submitted a plan");
		}
		else if (task instanceof Task.MoveAgentTask){
			Tile freeTile = searchForFreeTile(State.getInitialState().get(this.row).get(this.col),
					((Task.MoveAgentTask) task).getOccupiedTiles());
			Plan.MovePlan movePlan = new Plan.MovePlan(this.getRow(), this.getCol(), freeTile.getRow(),
					freeTile.getCol(), this.color);
			this.plan = new PlanProposal(movePlan.getPlan(), this, task.getId(), -1,
					-1, null);
			this.blackBoard.getMessagesToBlackboard().add(plan);

			System.err.println(this.toString() + "has submitted a moveplan");
		} else if (task instanceof Task.MoveBlockTask) {
			Tile freeTile =
					searchForFreeTile(State.getInitialState()
							.get(((Task.MoveBlockTask) task).getBlock().getRow())
							.get(((Task.MoveBlockTask) task).getBlock().getCol()),
							((Task.MoveBlockTask) task).getOccupiedTiles());
			Plan.MoveBoxPlan moveBoxPlan = new Plan.MoveBoxPlan(this.getRow(), this.getCol(),
					((Task.MoveBlockTask) task).getBlock().getRow(), ((Task.MoveBlockTask) task).getBlock().getCol(),
					freeTile.getRow(), freeTile.getCol(), this.color);
			this.plan = new PlanProposal(moveBoxPlan.getPlan(), this, task.getId(),
					-1, -1, ((Task.MoveBlockTask) task).getBlock());
			this.blackBoard.getMessagesToBlackboard().add(plan);
			System.err.println(this.toString() + "has submitted a moveBoxPlan");
		}
		workingOnPlan = false;

	}

    public void replan() {
    	this.plan.getActions().add(0, new Action(this.plan.getActions().get(0).getStartAgent()));
    	this.blackBoard.getMessagesToBlackboard().add(new PlanProposal(this.plan.getActions(), this,
				this.task.getId(), -1, -1, this.plan.getBlock()));
	}

	private static Tile searchForFreeTile(Tile startTile, List<Action> actions) {
    	Collection<Tile> occupiedTiles = new HashSet<>();
    	for (Action action: actions) {
    		occupiedTiles.add(action.getStartBox());
    		occupiedTiles.add(action.getEndBox());
    		occupiedTiles.add(action.getStartAgent());
    		occupiedTiles.add(action.getEndAgent());
		}
		Collection<Tile> exploredTiles = new HashSet<>();
		Stack<Tile> frontier = new Stack<>();
		frontier.push(startTile);
		while (!frontier.isEmpty()) {
			Tile exploringTile = frontier.pop();
			if(!occupiedTiles.contains(exploringTile)) {
				for (Tile neighbor : exploringTile.getNeighbors()) {
					if (!neighbor.isWall() && !neighbor.isGoal() && !occupiedTiles.contains(neighbor)) {
						return neighbor;
					}
				}

			}
			for (Tile neighbor : exploringTile.getNeighbors()) {
				if (exploredTiles.contains(neighbor)) {
					continue;
				}
				if (neighbor.isWall()) {
					continue;
				}
				if (neighbor.isGoal()) { //TODO this might be very buggy
					continue;
				}
				frontier.push(neighbor);
			}
			exploredTiles.add(exploringTile);
		}
		return null;
	}


	private void proposeHeuristic(HeuristicAndBlock h, Task t) {
		blackBoard.getMessagesToBlackboard().add(new HeuristicProposal(h, this, t.getId()));
    }

    private HeuristicAndBlock calculateHeuristic(Task task) {
		return getHeuristic(State.getState(), task.getGoal()); //TODO this is going to be buggy! Should not make new state
		// here
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
	}

	public void executePlan() {
		for (Action action : this.plan.getActions()) {
			if (action.getEndAgent()!= null) {
				this.row = action.getEndAgent().getRow();
				this.col = action.getEndAgent().getCol();
			}
			if (action.getEndBox()!= null) {
				this.plan.getBlock().setRow(action.getEndBox().getRow());
				this.plan.getBlock().setCol(action.getEndBox().getCol());

			}
			if (action.getStartBox()!= null && action.getStartBox().isGoal()) {
				action.getStartBox().getGoal().setCompleted(false);
			}
			if (action.getStartBox()!= null && action.getEndBox().isGoal()) {
				action.getEndBox().getGoal().setCompleted(true);
			}

		}
	}
	// END SETTERS

}
