package Components;

import Components.State.Board;
import Components.State.Goal;
import Components.State.State;
import Components.State.Tile;

import java.util.ArrayList;
import java.util.List;


/*
* The purpose of this class is to check if there needs to be a strict order in which the goals of the level needs to
* be solved in order for the level to actually be completed.
*
*
*
* */
public abstract class SubGoalPlanner {

    private List<List<Tile>> initialState = System.arraycopy();
    private List<Goal> goals = Board.getGoals();



    public void postToBlackBoard() {
        //TODO
    }

    public void serializeGoals(State initialState){
        //TODO: This is where we sort out which order our goals have to be completed in

    }
}
