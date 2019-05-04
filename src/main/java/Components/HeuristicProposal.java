package Components;

// TODO: Consider if it is better with getters and setters
public class HeuristicProposal extends Message {
    private int h;
    private Agent a;

    public int getH() {
        return h;
    }

    public Agent getA() {
        return a;
    }

    public long getTaskID() {
        return taskID;
    }

    private long taskID;

    public HeuristicProposal(int h, Agent a, long taskID) {
        this.h = h;
        this.a = a;
        this.taskID = taskID;
    }

    public void print() {
        System.err.println("Agent " + this.a.getAgentNumber() + " has heuristic h=" + this.h + " for task " + this.taskID);
    }
}
