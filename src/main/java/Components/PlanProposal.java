package Components;

import java.util.ArrayList;

public class PlanProposal extends Message {
    ArrayList<Action> actions;
    Agent a;
    long taskID;

    public PlanProposal(ArrayList<Action> actions, Agent a, long taskID) {
        this.actions = actions;
        this.a = a;
        this.taskID = taskID;
    }

    public void print() {
        //TODO: implement properly
        System.err.println("Agent " + a.getAgentNumber() + " has plan of actions ");
    }
}
