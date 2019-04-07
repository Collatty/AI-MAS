package Components;

import Components.State.*;

import java.util.*;


/*
* The purpose of this class is to check if there needs to be a strict order in which the goals of the level needs to
* be solved in order for the level to actually be completed.
* */



public abstract class SubGoalPlanner {

    private static List<List<Tile>> copyOfInitialState = State.copyState(State.getInitialState());
    private static List<Goal> goals = State.getGoals();
    private static List<List<Goal>> orderOfGoals = new ArrayList<>();



    public static void postToBlackBoard() {
        //TODO
    }

    public static void serializeGoals(oldState initialOldState){
        //TODO: This is where we sort out which order our goals have to be completed in

    }

    private static boolean searchForBlock(Goal goal) {
        Collection<Tile> exploredTiles = new HashSet<>();
        Stack<Tile> frontier = new Stack<>(); //TODO: Look into whether this is best data structure. Think it's
        // indifferent, but have not slept for a while now.
            Tile goalTile = copyOfInitialState.get(goal.getRow()).get(goal.getColumn());
            frontier.push(goalTile);
            while (!frontier.isEmpty()) {
                Tile expolringTile = frontier.pop();
                if (expolringTile.hasBlock() && ((Block) expolringTile.getTileOccupant()).getType() == goal.getType()) {
                    goalTile.setTileOccupant(new Wall(goalTile.getRow(), goalTile.getColumn()));
                    expolringTile.setTileOccupant(null); //BLOCK IS NOW "USED"
                    return true;
                }
                for (Tile neighbor : expolringTile.getNeighbors()) {
                    if (exploredTiles.contains(neighbor)) {
                        continue;
                    }
                    if (neighbor.isWall()) {
                        continue;
                    }
                    frontier.push(neighbor);
                }
                exploredTiles.add(expolringTile);

            }
        return false; // NO BLOCK WAS FOUND FOR THE GOAL AND THE LEVEL NEEDS TO BE SOLVED IN ANOTHER ORDER OR IS NOT
        // SOLVABLE AT ALL
    }


    //TODO: figure out the serialize method
    public static boolean serialize() {

        for (Goal goal : goals) {
            if (!searchForBlock(goal)) {
                SubGoalPlanner.reset();
                Goal iffyGoal = goals.get(goals.indexOf(goal));
                goals.remove(iffyGoal);
                goals.add(iffyGoal);
                return SubGoalPlanner.serialize();
            }
        }
        return true;
    }

    private static void reset() {
        copyOfInitialState=State.copyState(State.getInitialState());
    }
}
