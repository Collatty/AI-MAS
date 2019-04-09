package Components;

public class HeuristicProposal extends Message {
    int h;
    Agent a;
    long taskID;

    public HeuristicProposal(int h, Agent a, long taskID){
        this.h = h;
        this.a = a;
        this.taskID = taskID;
    }

    public void print(){
        System.out.println("Agent " + a.getAgentNumber() + " has heuristic h=" + h + " for task " + taskID);
    }

}
