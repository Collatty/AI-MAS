package Components;

import Components.State.Block;

import java.util.ArrayList;
import java.util.List;

public class PlanProposal extends Message {
    public List<Action> getActions() {
        return actions;
    }

    public Agent getA() {
        return a;
    }

    public long getTaskID() {
        return taskID;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public long getEndIndex() {
        return endIndex;
    }

    private List<Action> actions;
    private Agent a;
    private long taskID;
    private long startIndex;
    private long endIndex;

    public Block getBlock() {
        return block;
    }

    private Block block;

    public PlanProposal(List<Action> actions, Agent a, long taskID, long startIndex, long endIndex, Block block) {
	this.actions = actions;
	this.a = a;
	this.taskID = taskID;
	this.startIndex = startIndex;
	this.endIndex = endIndex;
	this.block = block;
    }

    public String toString() {
	return "Agent " + a.getAgentNumber() + " has plan for task  " + this.taskID + " with start index " + this.startIndex
		+ " and end index " + this.endIndex + " and the actions: " + this.actions.toString();
    }
}
