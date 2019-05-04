package Components;
import java.util.Random;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import AI.Heuristic;
import Components.State.State;
import Components.State.Goal;


public class Agent implements Subscriber<MessageToAgent> {


    private final int agentNumber;
    private final Color color;
    private int col;
    private int row;
    private Subscription blackboardChannel;
    private BlackBoard blackBoard;
    private boolean working;

    public Agent (int agentNumber, Color color, int row, int col, BlackBoard blackBoard) {
        this.agentNumber = agentNumber;
        this.color = color;
        this.row = row;
        this.col = col;
        this.blackBoard = blackBoard;
        this.working = false;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.blackboardChannel = subscription;
        this.blackboardChannel.request(1);
    }

    @Override
    public void onNext(MessageToAgent message) {
        if (message.toAll != null && message.toAll
        		|| message.toColor != null && message.toColor == color
                || message.toAgent != null && message.toAgent == agentNumber) {        	
            if (message.messageType == MessageType.HEURISTIC) {
                proposeHeuristic(calculateHeuristic(message.task), message.task);            
            } else if (message.messageType == MessageType.PLAN) {
                //TODO: Make awesome plan
                System.err.println("Agent " + agentNumber + " is planning");
                //System.err.println("Agent " + agentNumber + " makes plan for task " + message.task.id);
            }
        }
        this.blackboardChannel.request(1);
    }

    private void proposeHeuristic(int h, Task t) {
        blackBoard.messagesToBlackboard.add(new HeuristicProposal(h,this, t.getId()));
    }

    private int calculateHeuristic(Task task) {
        //TODO: update function with actual heuristic calculation
        Random rand = new Random();
        return rand.nextInt((10 - 1) + 1) + 1; //Random number between 1 and 10
    }

    public int getHeuristic(State state, Goal goal){
        return Heuristic.h(state, this, goal);
    }

    @Override
    public void onError(Throwable e) {
        System.err.println("Some error happened in agent " + agentNumber + " subscription:");
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        // System.out.println("All Processing Done");
    }

    @Override
    public String toString() {
        return Integer.toString(getAgentNumber());
    }

    //GETTERS
    public int getAgentNumber() {
        return agentNumber;
    }

    public Color getColor() {
        return color;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean getWorking() { return working; }
    //END GETTERS


    //SETTERS
    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setWorking(boolean working) { this.working = working; }
    //END SETTERS

}
