package components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import AI.Heuristic;
import AI.HeuristicAndBlock;
import AI.Plan;
import components.state.Block;
import components.state.Goal;
import components.state.State;
import components.state.Tile;

public class Agent implements Subscriber<MessageToAgent>, Runnable {
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
	if (message.getToAll() != null && message.getToAll()
		|| message.getToColor() != null && message.getToColor() == this.color
		|| message.getToAgent() != null && message.getToAgent() == agentNumber) {
	    if (message.getMessageType() == MessageType.HEURISTIC) {
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
	    Plan.MoveBoxPlan moveBoxPlan = new Plan.MoveBoxPlan(this.getRow(), this.getCol(), task.getBlock().getRow(),
		    task.getBlock().getCol(), task.getGoal().getRow(), task.getGoal().getCol(), this.color);
	    this.plan = new PlanProposal(moveBoxPlan.getPlan(), this, task);
	    BlackBoard.getBlackBoard().getMessagesToBlackboard().add(plan);
	} else if (task instanceof Task.MoveAgentTask) {
	    Collection<Tile> occupiedTiles = getOccupiedTiles(((Task.MoveAgentTask) task).getOccupiedTiles());
	    Tile freeTile = searchForFreeTile(State.getInitialState().get(this.row).get(this.col), occupiedTiles);
	    if (null == freeTile) {
		workingOnPlan = false;
		BlackBoard.getBlackBoard().getMessagesToBlackboard().add(new AbortTaskMessage(task, this));
		return;
	    }
	    Plan.MovePlan movePlan = new Plan.MovePlan(this.getRow(), this.getCol(), freeTile.getRow(),
		    freeTile.getCol(), this.color);
	    this.plan = new PlanProposal(movePlan.getPlan(), this, task);
	    BlackBoard.getBlackBoard().getMessagesToBlackboard().add(plan);

	} else if (task instanceof Task.MoveBlockTask) {
	    Collection<Tile> occupiedTiles = getOccupiedTiles(((Task.MoveBlockTask) task).getOccupiedTiles());
	    boolean solved = false;
	    Plan.MoveBoxPlan moveBoxPlan = null;
	    List<Tile> virtualWalls = new ArrayList<>();
	    while (!solved) {
		Tile freeTile = searchForFreeTile(
			State.getInitialState().get(task.getBlock().getRow()).get(task.getBlock().getCol()),
			occupiedTiles);
		if (null == freeTile) {
		    workingOnPlan = false;
		    BlackBoard.getBlackBoard().getMessagesToBlackboard().add(new AbortTaskMessage(task, this));
		    return;
		}
		moveBoxPlan = new Plan.MoveBoxPlan(this.getRow(), this.getCol(), task.getBlock().getRow(),
			task.getBlock().getCol(), freeTile.getRow(), freeTile.getCol(), this.color);

		Tile agentEndTile = moveBoxPlan.getPlan().get(moveBoxPlan.getPlan().size() - 1).getEndAgent();
		if (!occupiedTiles.contains(agentEndTile)) {
		    solved = true;
		} else {
		    // Agent is now in the way, try to find a tile it can go to
		    virtualWalls.add(freeTile);
		    Tile freeAgentTile = searchForFreeTileSimple(
			    State.getInitialState().get(agentEndTile.getRow()).get(agentEndTile.getCol()),
			    occupiedTiles, virtualWalls);
		    if (null == freeAgentTile) {
			// Could not find a tile for agent. Find a new tile for block
			occupiedTiles.add(freeTile);
			virtualWalls.remove(freeTile);
		    } else {
			Plan.MovePlan movePlan = new Plan.MovePlan(agentEndTile.getRow(), agentEndTile.getCol(),
				freeAgentTile.getRow(), freeAgentTile.getCol(), this.color);
			moveBoxPlan.getPlan().addAll(movePlan.getPlan());
			solved = true;
		    }
		}
	    }
	    this.plan = new PlanProposal(moveBoxPlan.getPlan(), this, task);
	    BlackBoard.getBlackBoard().getMessagesToBlackboard().add(plan);
	}
	workingOnPlan = false;

    }

    public void replan() {
	this.plan.getActions().add(0, new Action(this.plan.getActions().get(0).getStartAgent()));
	BlackBoard.getBlackBoard().getMessagesToBlackboard()
		.add(new PlanProposal(this.plan.getActions(), this, this.task));
    }

    private static Collection<Tile> getOccupiedTiles(List<Action> actions) {
	Collection<Tile> occupiedTiles = new HashSet<>();
	for (Action action : actions) {
	    occupiedTiles.add(action.getStartBox());
	    occupiedTiles.add(action.getEndBox());
	    occupiedTiles.add(action.getStartAgent());
	    occupiedTiles.add(action.getEndAgent());
	}
	return occupiedTiles;
    }

    private static Tile searchForFreeTileSimple(Tile startTile, Collection<Tile> occupiedTiles,
	    List<Tile> virtualWalls) {
	// Try first to find a tile without having to move stuff
	Collection<Tile> exploredTiles = new HashSet<>();
	Stack<Tile> frontier = new Stack<>();
	frontier.push(startTile);
	while (!frontier.isEmpty()) {
	    Tile exploringTile = frontier.pop();
	    if (!occupiedTiles.contains(exploringTile) && exploringTile.isFree() && !exploringTile.isWall()
		    && !exploringTile.isCompletedGoal() && !virtualWalls.contains(exploringTile)) {
		return exploringTile;
	    }

	    for (Tile neighbor : exploringTile.getNeighbors()) {
		if (!exploredTiles.contains(neighbor) && !frontier.contains(neighbor) && !neighbor.isWall()
			&& neighbor.isFree() && !virtualWalls.contains(exploringTile)) {
		    frontier.push(neighbor);
		}
	    }
	    exploredTiles.add(exploringTile);
	}
	return null;
    }

    private static Tile searchForFreeTile(Tile startTile, Collection<Tile> occupiedTiles) {
	// Try first to find a tile without having to move stuff
	Collection<Tile> exploredTiles = new HashSet<>();
	Stack<Tile> frontier = new Stack<>();
	frontier.push(startTile);
	while (!frontier.isEmpty()) {
	    Tile exploringTile = frontier.pop();
	    if (!occupiedTiles.contains(exploringTile) && exploringTile.isFree() && !exploringTile.isWall()
		    && !exploringTile.isCompletedGoal()) {
		return exploringTile;
	    }

	    for (Tile neighbor : exploringTile.getNeighbors()) {
		if (!exploredTiles.contains(neighbor) && !frontier.contains(neighbor) && !neighbor.isWall()
			&& neighbor.isFree()) {
		    frontier.push(neighbor);
		}
	    }
	    exploredTiles.add(exploringTile);
	}

	// Try to find a tile, with stuff in the way
	exploredTiles = new HashSet<>();
	frontier = new Stack<>();
	frontier.push(startTile);
	while (!frontier.isEmpty()) {
	    Tile exploringTile = frontier.pop();
	    if (!occupiedTiles.contains(exploringTile) && exploringTile.isFree() && !exploringTile.isWall()
		    && !exploringTile.isCompletedGoal()) {
		return exploringTile;
	    }

	    for (Tile neighbor : exploringTile.getNeighbors()) {
		if (!exploredTiles.contains(neighbor) && !frontier.contains(neighbor) && !neighbor.isWall()) {
		    frontier.push(neighbor);
		}
	    }
	    exploredTiles.add(exploringTile);
	}

	// Try to find a tile, where the end tile hosts something else
	exploredTiles = new HashSet<>();
	frontier = new Stack<>();
	frontier.push(startTile);
	while (!frontier.isEmpty()) {
	    Tile exploringTile = frontier.pop();
	    if (!occupiedTiles.contains(exploringTile) && !exploringTile.isWall() && !exploringTile.isCompletedGoal()) {
		return exploringTile;
	    }

	    for (Tile neighbor : exploringTile.getNeighbors()) {
		if (!exploredTiles.contains(neighbor) && !frontier.contains(neighbor) && !neighbor.isWall()
			&& neighbor.isFree()) {
		    frontier.push(neighbor);
		}
	    }
	    exploredTiles.add(exploringTile);
	}

	// Try to find a tile, where the end tile hosts something else, and something is
	// in the way
	exploredTiles = new HashSet<>();
	frontier = new Stack<>();
	frontier.push(startTile);
	while (!frontier.isEmpty()) {
	    Tile exploringTile = frontier.pop();
	    if (!occupiedTiles.contains(exploringTile) && !exploringTile.isWall() && !exploringTile.isCompletedGoal()) {
		return exploringTile;
	    }

	    for (Tile neighbor : exploringTile.getNeighbors()) {
		if (!exploredTiles.contains(neighbor) && !frontier.contains(neighbor) && !neighbor.isWall()) {
		    frontier.push(neighbor);
		}
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

    public HeuristicAndBlock getHeuristic(State state, Goal goal) {
	return Heuristic.h(state, this, goal);
    }

    @Override
    public void onError(Throwable e) {
	e.printStackTrace();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public String toString() {
	return Integer.toString(getAgentNumber());
    }

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

    public void executePlan(PlanProposal pp) {
	for (Action action : pp.getActions()) {
	    Block movedBlock = null;
	    Agent movedAgent = null;
	    if (action.getStartAgent() != null) {
		movedAgent = (Agent) State.getInitialState().get(action.getStartAgent().getRow())
			.get(action.getStartAgent().getCol()).getTileOccupant();
		State.getInitialState().get(action.getStartAgent().getRow()).get(action.getStartAgent().getCol())
			.removeTileOccupant();

	    }
	    if (action.getStartBox() != null) {
		movedBlock = (Block) State.getInitialState().get(action.getStartBox().getRow())
			.get(action.getStartBox().getCol()).getTileOccupant();
		State.getInitialState().get(action.getStartBox().getRow()).get(action.getStartBox().getCol())
			.removeTileOccupant();

	    }
	    if (action.getEndAgent() != null) {
		this.row = action.getEndAgent().getRow();
		this.col = action.getEndAgent().getCol();
		State.getInitialState().get(this.row).get(this.col).setTileOccupant(movedAgent);
		movedAgent.setRow(this.row);
		movedAgent.setCol(this.col);

	    }
	    if (action.getEndBox() != null) {
		pp.getTask().getBlock().setRow(action.getEndBox().getRow());
		pp.getTask().getBlock().setCol(action.getEndBox().getCol());
		State.getInitialState().get(action.getEndBox().getRow()).get(action.getEndBox().getCol())
			.setTileOccupant(movedBlock);

	    }
	    if (action.getStartBox() != null && action.getStartBox().isGoal()) {
		action.getStartBox().getGoal().setCompleted(false);
	    }
	    if (action.getEndBox() != null && action.getEndBox().isGoal()) {
		action.getEndBox().getGoal().setCompleted(true);
	    }
	}
    }
}
