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

}
