package AI;

import components.Agent;
import components.state.*;
import components.Task;

import java.util.*;
import java.util.stream.Collectors;

import static components.TaskType.GOAL;


/*
* The purpose of this class is to check if there needs to be a strict order in which the goals of the level needs to
* be solved in order for the level to actually be completed.
* */


public abstract class SubGoalPlanner {

    public static List<Task> postToBlackBoard() {
        return convertToTask();
    }

    private static boolean searchForBlock(Goal goal) {
        boolean hasBlock = false;
        boolean hasAgent = false;
        Collection<Tile> exploredTiles = new HashSet<>();
        Stack<Tile> frontier = new Stack<>();
        Tile goalTile = State.getInitialState().get(goal.getRow()).get(goal.getCol());
        frontier.push(goalTile);
        while (!frontier.isEmpty()) {
            Tile exploringTile = frontier.pop();
            if (exploringTile.hasBlock() && ((Block) exploringTile.getTileOccupant()).getType() == goal.getType()) {
                hasBlock = true;
            }
            if (exploringTile.getTileOccupant() instanceof Agent && ((Agent) exploringTile.getTileOccupant()).getColor().equals(goal.getColor())) {
                hasAgent = true;
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
        return hasBlock && hasAgent;
    }

    private static void searchForGoal(Goal goal) {
        for (Block block : State.getBlocks()) {
            if (goal.getType() == (block.getType())) {
                AStarSearch search = new AStarSearch(State.getInitialState().size(), State.getMaxCol(),
                        new Node(goal.getRow(), goal.getCol()), new Node(block.getRow(), block.getCol()), 1);
                List<Node> path = search.findPath();
                if (path.get(path.size()-1).getRow() == block.getRow() && path.get(path.size()-1).getCol() == block.getCol()){
                    for (Node node : path.subList(1,path.size()-1)) {
                        if (State.getInitialState().get(node.getRow()).get(node.getCol()).isGoal()){
                            State.getInitialState().get(node.getRow()).get(node.getCol()).getGoal().getPreconditions().add(goal);
                        }
                    }
                }
                for (Agent agent : State.getInitialAgents()) {
                    if(agent.getColor().equals(block.getColor())) {
                        AStarSearch searchForAgent= new AStarSearch(State.getInitialState().size(), State.getMaxCol(),
                                new Node(block.getRow(), block.getCol()),new Node( agent.getRow(), agent.getCol()),1);
                        List<Node> pathToAgent = searchForAgent.findPath();
                        if (pathToAgent.get(pathToAgent.size()-1).getRow() == agent.getRow() && pathToAgent.get(pathToAgent.size()-1).getCol() == agent.getCol()){
                            for (Node node : pathToAgent.subList(1,pathToAgent.size()-1)) {
                                if (State.getInitialState().get(node.getRow()).get(node.getCol()).isGoal()){
                                    State.getInitialState().get(node.getRow()).get(node.getCol()).getGoal().getPreconditions().add(goal);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void serialize() {
        for (Goal goal : State.getGoals()) {
            if (!searchForBlock(goal)) {
                searchForGoal(goal);
            }
        }

    }

    private static List<Task> convertToTask() {
        List<Task> tasks = new ArrayList<>();
        HashMap<Goal, Task> mapping = new HashMap<>();
        for (Goal goal : State.getGoals()) {
            System.err.println("101: " + goal.toString());
            if(!goal.isCompleted()) {
                System.err.println("103: " + goal.toString() + " is not completed");
                Block block = null;
                for (Block block1 : State.getBlocks()) {
                    if (block1.getType() == goal.getType()){
                        block = block1;
                    }
                }
                Task task = new Task(goal.getColor(), null, goal, block);
                task.setTaskType(GOAL);
                tasks.add(task);
                mapping.put(goal, task);
            }
        }
        for (Goal goal : State.getGoals()) {
            if(!goal.isCompleted()) {
                System.err.println("116: " + goal.toString());
                List<Task> depTasks = new ArrayList<>();
                for (Goal goal2 : goal.getPreconditions()) {
                    System.err.println("119: " + goal2.toString());
                    System.err.println("120: " + mapping.toString());
                    depTasks.add(mapping.get(goal2));
                }
                System.err.println("123, depTasks: " + depTasks.toString());
                List<Long> taskIds = depTasks.stream()
                        .map(Task::getId)
                        .collect(Collectors.toList());
                System.err.println("127" + taskIds.toString());
                mapping.get(goal).setDependencies(taskIds);
            }
        }
        return tasks;
    }
}
