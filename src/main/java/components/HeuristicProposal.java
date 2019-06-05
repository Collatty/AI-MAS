package components;

import AI.HeuristicAndBox;

public class HeuristicProposal extends Message {
    private HeuristicAndBox h;
    private Agent a;

    public HeuristicAndBox getH() {
	return h;
    }

    public Agent getA() {
	return a;
    }

    public long getTaskID() {
	return taskID;
    }

    private long taskID;

    public HeuristicProposal(HeuristicAndBox h, Agent a, long taskID) {
	this.h = h;
	this.a = a;
	this.taskID = taskID;
    }
}
