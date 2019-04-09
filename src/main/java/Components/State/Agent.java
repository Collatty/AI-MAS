package Components.State;
import Components.Color;
import Components.Task;
import java.util.concurrent.Flow.*;

public class Agent implements Subscriber<Task> {

    private final int agentNumber;
    private final Color color;
    private int x;
    private int y;


    public Agent (int agentNumber, Color color, int x, int y){
        this.agentNumber = agentNumber;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    @Override
    public void onSubscribe(Subscription subscription) {

    }

    @Override
    public void onNext(Task item) {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onComplete() {

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
