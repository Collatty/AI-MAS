package components;

import java.util.*;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import AI.Heuristic;
import AI.HeuristicAndBlock;
import AI.Plan;
import components.state.Block;
import components.state.State;
import components.state.Goal;
import components.state.Tile;


public class Agent implements Subscriber<MessageToAgent>, Runnable{


    private final int agentNumber;
    private final Color color;
    private int col;
    private int row;
	private Subscription blackboardChannel;

    private PlanProposal plan;
    private Task task;
    private boolean workingOnPlan;


    public Agent(int agentNumber, Color color, int row, int col) {
	this.agentNumber = agentNumber;
	this.color = color;
	this.row = row;
	this.col = col;
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
				this.createPlan(message.getTask());
			}
		}
		this.blackboardChannel.request(1);
    }

    private void createPlan(Task task) {
    	this.task = task;
		workingOnPlan = true;
		if (!(task instanceof Task.MoveBlockTask) && (!(task instanceof Task.MoveAgentTask))) {
			Plan.MoveBoxPlan moveBoxPlan = new Plan.MoveBoxPlan(this.getRow(), this.getCol(),
					task.getBlock().getRow(), task.getBlock().getCol(), task.getGoal().getRow(),
					task.getGoal().getCol(), this.color);
			this.plan = new PlanProposal(moveBoxPlan.getPlan(), this, task);
			BlackBoard.getBlackBoard().getMessagesToBlackboard().add(plan);
			System.err.println(this.toString() + "has submitted a plan");
		}
		else if (task instanceof Task.MoveAgentTask){
			Tile freeTile = searchForFreeTile(State.getInitialState().get(this.row).get(this.col),
					((Task.MoveAgentTask) task).getOccupiedTiles());
			Plan.MovePlan movePlan = new Plan.MovePlan(this.getRow(), this.getCol(), freeTile.getRow(),
					freeTile.getCol(), this.color);
			this.plan = new PlanProposal(movePlan.getPlan(), this, task);
			BlackBoard.getBlackBoard().getMessagesToBlackboard().add(plan);

			System.err.println(this.toString() + "has submitted a moveplan");
		} else if (task instanceof Task.MoveBlockTask) {
			Tile freeTile =
					searchForFreeTile(State.getInitialState()
							.get(task.getBlock().getRow())
							.get(task.getBlock().getCol()),
							((Task.MoveBlockTask) task).getOccupiedTiles());
			Plan.MoveBoxPlan moveBoxPlan = new Plan.MoveBoxPlan(this.getRow(), this.getCol(),
					task.getBlock().getRow(), task.getBlock().getCol(),
					freeTile.getRow(), freeTile.getCol(), this.color);
			this.plan = new PlanProposal(moveBoxPlan.getPlan(), this, task);
			BlackBoard.getBlackBoard().getMessagesToBlackboard().add(plan);
			System.err.println(this.toString() + "has submitted a moveBoxPlan");
		}
		workingOnPlan = false;

	}

    public void replan() {
    	this.plan.getActions().add(0, new Action(this.plan.getActions().get(0).getStartAgent()));
    	BlackBoard.getBlackBoard().getMessagesToBlackboard().add(new PlanProposal(this.plan.getActions(), this, this.task));
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
					if (!neighbor.isWall() && !neighbor.isGoal() && !occupiedTiles.contains(neighbor) && neighbor.isFree()) {
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
				if(!neighbor.isFree()) {
					continue;
				}
				if (neighbor.isCompletedGoal()){
					continue;
				}
				frontier.push(neighbor);
			}
			exploredTiles.add(exploringTile);
		}
		return null;
	}


	private void proposeHeuristic(HeuristicAndBlock h, Task t) {
		BlackBoard.getBlackBoard().getMessagesToBlackboard().add(new HeuristicProposal(h, this, t.getId()));
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
        return "Agent " + Integer.toString(getAgentNumber());
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
			Block movedBlock = null;
			Agent movedAgent = null;
			if(action.getStartAgent() != null ){
				movedAgent =
						(Agent) State.getInitialState().get(action.getStartAgent().getRow()).get(action.getStartAgent().getCol()).getTileOccupant();
				State.getInitialState().get(action.getStartAgent().getRow()).get(action.getStartAgent().getCol()).removeTileOccupant();

			}
			if (action.getStartBox() != null ) {
				movedBlock =
						(Block) State.getInitialState().get(action.getStartBox().getRow()).get(action.getStartBox().getCol()).getTileOccupant();
				State.getInitialState().get(action.getStartBox().getRow()).get(action.getStartBox().getCol()).removeTileOccupant();

			}
			if (action.getEndAgent()!= null) {
				this.row = action.getEndAgent().getRow();
				this.col = action.getEndAgent().getCol();
				State.getInitialState().get(this.row).get(this.col).setTileOccupant(movedAgent);
				movedAgent.setRow(this.row);
				movedAgent.setCol(this.col);

			}
			if (action.getEndBox()!= null) {
				this.plan.getTask().getBlock().setRow(action.getEndBox().getRow());
				this.plan.getTask().getBlock().setCol(action.getEndBox().getCol());
				State.getInitialState().get(action.getEndBox().getRow()).get(action.getEndBox().getCol()).setTileOccupant(movedBlock);

			}
			if (action.getStartBox()!= null && action.getStartBox().isGoal()) {
				action.getStartBox().getGoal().setCompleted(false);
			}
			if (action.getEndBox()!= null && action.getEndBox().isGoal()) {
				action.getEndBox().getGoal().setCompleted(true);
			}
		}
	}
	// END SETTERS

}
