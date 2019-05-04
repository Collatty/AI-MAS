package Components;

import Components.State.*;

import java.util.*;


/*
* The purpose of this class is to check if there needs to be a strict order in which the goals of the level needs to
* be solved in order for the level to actually be completed.
* */



public abstract class SubGoalPlanner {

    private static List<List<Tile>> copyOfInitialState = State.copyState(State.getInitialState());

    private static List<Goal> goals = State.copyGoals(State.getGoals());

    public static List<Task> postToBlackBoard() {
        return convertToTask();
    }


    private static boolean searchForBlock(Goal goal) {
        Collection<Tile> exploredTiles = new HashSet<>();
        Stack<Tile> frontier = new Stack<>(); //TODO: Look into whether this is best data structure. Think it's
        // indifferent, but have not slept for a while now.
            Tile goalTile = copyOfInitialState.get(goal.getRow()).get(goal.getCol());
            frontier.push(goalTile);
            Goal neighborGoal = null;
            while (!frontier.isEmpty()) {
                Tile exploringTile = frontier.pop();
                if (exploringTile.hasBlock() && ((Block) exploringTile.getTileOccupant()).getType() == goal.getType()) {
                    //goalTile.setTileOccupant(new Wall(goalTile.getRow(), goalTile.getCol()));
                    //exploringTile.setTileOccupant(null); //BLOCK IS NOW "USED"
                    return true;
                }
                for (Tile neighbor : exploringTile.getNeighbors()) {
                    if (exploredTiles.contains(neighbor)) {
                        continue;
                    }
                    if (neighbor.isWall()) {
                        continue;
                    }
                    frontier.push(neighbor);
                }
                exploredTiles.add(exploringTile);

            }
        return false; // NO BLOCK WAS FOUND FOR THE GOAL AND THE LEVEL NEEDS TO BE SOLVED IN ANOTHER ORDER OR IS NOT
        // SOLVABLE AT ALL
    }

    private static List<Goal> searchForGoal(Goal goal) {
        Collection<Tile> exploredTiles = new HashSet<>();
        Stack<Tile> frontier = new Stack<>();
        Tile goalTile = copyOfInitialState.get(goal.getRow()).get(goal.getCol());
        frontier.push(goalTile);
        List<Goal> neighborGoals = null;
        while (!frontier.isEmpty()) {
            Tile exploringTile = frontier.pop();
            for (Tile neighbor : exploringTile.getNeighbors()) {
                if (exploredTiles.contains(neighbor)) {
                    continue;
                }
                if (neighbor.isGoal()) {
                    neighborGoals.add( copyOfInitialState.get(neighbor.getRow()).get(neighbor.getCol()).getGoal());
                }
                if (neighbor.isWall()) {
                    continue;
                }

                frontier.push(neighbor);
            }
            exploredTiles.add(exploringTile);

        }
        return neighborGoals;

    }


    //Think this should work reasonably well
    public static void serialize() {
        List<List<Tile>> copyState = State.copyState(copyOfInitialState);

        for (Goal goal : goals) {
            System.err.println(goal.toString());
            copyState.get(goal.getRow()).get(goal.getCol()).setWall(true);
        }

        for (Goal goal : goals ) {
            if (searchForBlock(goal)) {
                System.err.println("Found a block for goal: " + goal);
                //goal.setPrecondition(null);
            } else { // if the previous statement isn't true we either have a precondition, or the level is insolvable.
                List<Goal> preconditions = searchForGoal(goal);
                if (preconditions!=null) {
                    for (Goal goal2 : preconditions)
                    goal.setPrecondition(goal2);
                } else {
                    System.err.println("Level is not fucking solvable");
                }

            }
        }

        for (Goal goal : goals) {
            goal.accumulatePreconditions();
        }

    }

    private static void reset() {
        copyOfInitialState=State.copyState(State.getInitialState());
    }

    public static List<Task> convertToTask() {
        List<Task> tasks = new ArrayList<>();
        HashMap<Goal, Task> mapping = new HashMap<>();
        for (Goal goal : goals) {
            Task task = new Task(goal.getRow(), goal.getCol(), goal.getColor(), null);
            tasks.add(task);
            mapping.put(goal, task);
        }
        for (Goal goal : goals) {
            List<Task> depTasks = new ArrayList<>();
            for (Goal goal2 : goal.getPreconditions()) {
                depTasks.add(mapping.get(goal2));
            }
            mapping.get(goal).setDependencies(depTasks);

        }
        return tasks;
    }
}
