package Components;

import Components.State.*;

import java.util.List;


/*
* The purpose of this class is to check if there needs to be a strict order in which the goals of the level needs to
* be solved in order for the level to actually be completed.
*
*
*
* */
public abstract class SubGoalPlanner {

    private List<List<Tile>> copyOfInitialState = State.copyState(State.getTiles());



    private List<Goal> goals = State.getGoals();



    public void postToBlackBoard() {
        //TODO
    }

    public void serializeGoals(oldState initialOldState){
        //TODO: This is where we sort out which order our goals have to be completed in

    }
}
