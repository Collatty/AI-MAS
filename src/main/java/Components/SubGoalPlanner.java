package Components;

import Components.State.State;

import java.util.ArrayList;
import java.util.List;

public class SubGoalPlanner {

    private State state;
    private List<Object> goals = new ArrayList<Object>();

    public SubGoalPlanner(State initialState) {
        this.state = initialState;
    }

    public void postToBlackBoard() {
        BlackBoard.addElementToList(this.goals);
    }

    public void serializeGoals(State initialState){
        //TODO: This is where we sort out which order our goals have to be completed in

    }
}
