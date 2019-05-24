package components;

import AI.HeuristicAndBlock;

public class HeuristicProposal extends Message {
    private HeuristicAndBlock h;
    private Agent a;

    public HeuristicAndBlock getH() {
	return h;
    }

    public Agent getA() {
	return a;
    }

    public long getTaskID() {
	return taskID;
    }

    private long taskID;

    public HeuristicProposal(HeuristicAndBlock h, Agent a, long taskID) {
	this.h = h;
	this.a = a;
	this.taskID = taskID;
    }
}
