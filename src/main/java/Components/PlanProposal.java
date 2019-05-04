package Components;

import java.util.ArrayList;

public class PlanProposal extends Message {
    ArrayList<Action> actions;
    Agent a;
    long taskID;
    long startIndex;
    long endIndex;

    public PlanProposal(ArrayList<Action> actions, Agent a, long taskID, long startIndex, long endIndex) {
	this.actions = actions;
	this.a = a;
	this.taskID = taskID;
	this.startIndex = startIndex;
	this.endIndex = endIndex;
    }

    public String toString() {
	return "Agent " + a.getAgentNumber() + " has plan for task  " + taskID + " with start index " + startIndex
		+ " and end index " + endIndex + " and the actions: " + actions.toString();
    }
}
