package Components;
import java.util.Random;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class Agent implements Subscriber<MessageToAgent> {
    private final int agentNumber;
    private final Color color;
    private int x;
    private int y;
    private Subscription blackboardChannel;
    private BlackBoard blackBoard;
    private boolean working;

    public Agent (int agentNumber, Color color, int x, int y, BlackBoard blackBoard) {
        this.agentNumber = agentNumber;
        this.color = color;
        this.x = x;
        this.y = y;
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
        blackBoard.messagesToBlackboard.add(new HeuristicProposal(h,this, t.id));
    }

    private int calculateHeuristic(Task task) {
        //TODO: update function with actual heuristic calculation
        Random rand = new Random();
        return rand.nextInt((10 - 1) + 1) + 1; //Random number between 1 and 10
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

    //GETTERS
    public int getAgentNumber() {
        return agentNumber;
    }

    public Color getColor() {
        return color;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public boolean getWorking() { return working; }
    //END GETTERS


    //SETTERS
    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setWorking(boolean working) { this.working = working; }

    //END SETTERS

}
