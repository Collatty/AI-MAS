package AI;

import static components.TaskType.GOAL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import components.Agent;
import components.Task;
import components.state.Box;
import components.state.Goal;
import components.state.State;
import components.state.Tile;

/*
 * The purpose of this class is to check if there needs to be a strict order in which the goals of the level needs to
 * be solved in order for the level to actually be completed.
 * */

public abstract class SubGoalPlanner {

    public static List<Task> postToBlackBoard() {
	return convertToTask();
    }

    private static boolean searchForBox(Goal goal) {
	boolean hasBox = false;
	boolean hasAgent = false;
	Collection<Tile> exploredTiles = new HashSet<>();
	Stack<Tile> frontier = new Stack<>();
	Tile goalTile = State.getInitialState().get(goal.getRow()).get(goal.getCol());
	System.err.println("37, current state: " + State.getInitialState().toString());
	frontier.push(goalTile);
	while (!frontier.isEmpty()) {
	    Tile exploringTile = frontier.pop();
	    if (exploringTile.hasBox() && ((Box) exploringTile.getTileOccupant()).getType() == goal.getType()) {
		hasBox = true;
	    }
	    if (exploringTile.getTileOccupant() instanceof Agent
		    && ((Agent) exploringTile.getTileOccupant()).getColor().equals(goal.getColor())) {
		hasAgent = true;
		System.err.println("Found a correct type agent for " + goal.toString() + " on tile ("
			+ exploringTile.getCol() + "," + exploringTile.getRow() + ")");
	    }
	    for (Tile neighbor : exploringTile.getNeighbors()) {
		if (exploredTiles.contains(neighbor)) {
		    continue;
		}
		if (neighbor.isWall()) {
		    continue;
		}
		if (neighbor.isGoal()) {
		    continue;
		}
		frontier.push(neighbor);
	    }
	    exploredTiles.add(exploringTile);
	}
	return hasBox && hasAgent;
    }

    private static void searchForGoal(Goal goal) {
	for (Box box : goal.getReachableBoxes()) {
	    AStarSearch search = new AStarSearch(State.getInitialState().size(), State.getMaxCol(),
		    new Node(goal.getRow(), goal.getCol()), new Node(box.getRow(), box.getCol()), 1);
	    List<Node> path = search.findPath();
	    if (path.get(path.size() - 1).getRow() == box.getRow()
		    && path.get(path.size() - 1).getCol() == box.getCol()) {
		for (Node node : path.subList(1, path.size() - 1)) {
		    if (State.getInitialState().get(node.getRow()).get(node.getCol()).isGoal()) {
			State.getInitialState().get(node.getRow()).get(node.getCol()).getGoal().getPreconditions()
				.add(goal);
			System.err.println("77: Adding " + goal.toString() + " as a precondition for "
				+ State.getInitialState().get(node.getRow()).get(node.getCol()).getGoal().toString());
		    }
		}
	    }
	    for (Agent agent : box.getReachableAgents()) {
		AStarSearch searchForAgent = new AStarSearch(State.getInitialState().size(), State.getMaxCol(),
			new Node(box.getRow(), box.getCol()), new Node(agent.getRow(), agent.getCol()), 1);
		List<Node> pathToAgent = searchForAgent.findPath();
		if (pathToAgent.get(pathToAgent.size() - 1).getRow() == agent.getRow()
			&& pathToAgent.get(pathToAgent.size() - 1).getCol() == agent.getCol()) {
		    for (Node node : pathToAgent.subList(1, pathToAgent.size() - 1)) {
			if (State.getInitialState().get(node.getRow()).get(node.getCol()).isGoal() && !(State
				.getInitialState().get(node.getRow()).get(node.getCol()).getRow() == goal.getRow()
				&& State.getInitialState().get(node.getRow()).get(node.getCol()).getCol() == goal
					.getCol()
				&& State.getInitialState().get(node.getRow()).get(node.getCol()).getGoal()
					.getType() == goal.getType())) {
			    State.getInitialState().get(node.getRow()).get(node.getCol()).getGoal().getPreconditions()
				    .add(goal);
			    System.err.println("92: Adding " + goal.toString() + " as a precondition for " + State
				    .getInitialState().get(node.getRow()).get(node.getCol()).getGoal().toString());
			}
		    }
		}
	    }
	}
    }

    public static void serialize() {
	for (Goal goal : State.getGoals()) {
	    if (!searchForBox(goal)) {
		searchForGoal(goal);
	    }
	}
    }

    private static List<Task> convertToTask() {
	List<Task> tasks = new ArrayList<>();
	HashMap<Goal, Task> mapping = new HashMap<>();
	for (Goal goal : State.getGoals()) {
	    if (!goal.isCompleted()) {
		Box box = goal.getReachableBoxes().get(0);
		Task task = new Task(goal.getColor(), null, goal, box);
		task.setTaskType(GOAL);
		tasks.add(task);
		mapping.put(goal, task);
	    }
	}
	for (Goal goal : State.getGoals()) {
	    if (!goal.isCompleted()) {
		List<Task> depTasks = new ArrayList<>();
		for (Goal goal2 : goal.getPreconditions()) {
		    depTasks.add(mapping.get(goal2));
		}
		List<Long> taskIds = depTasks.stream().map(Task::getId).collect(Collectors.toList());
		mapping.get(goal).setDependencies(taskIds);
	    }
	}
	return tasks;
    }
}
