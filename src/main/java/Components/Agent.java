package Components;
import Components.Color;
import Components.Task;
import java.util.concurrent.Flow.*;
import java.util.Random;

public class Agent implements Subscriber<Task> {

    private final int agentNumber;
    private final Color color;
    private int x;
    private int y;
    private Subscription todoSub;
    private BlackBoard blackBoard;

    public Agent (int agentNumber, Color color, int x, int y, BlackBoard blackBoard){
        this.agentNumber = agentNumber;
        this.color = color;
        this.x = x;
        this.y = y;
        this.blackBoard = blackBoard;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println("Subscribed");
        this.todoSub = subscription;
        this.todoSub.request(1);
        System.out.println("Agent " + agentNumber + ": onSubscribe request 1 task");
    }

    @Override
    public void onNext(Task task) {
        System.out.println("Agent " + agentNumber + " with color " + color + " checks task with color " + task.color);
        if(task.color == color){
            proposeHeuristic(calculateHeuristic(task), task);
            System.out.println("Agent " + agentNumber + " suggest heuristic " + calculateHeuristic(task) + " for task "
                    + task.id);
        }
        this.todoSub.request(1);
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
        System.out.println("Some error happened");
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("All Processing Done");
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
    //END GETTERS


    //SETTERS
    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }
    //END SETTERS

}
