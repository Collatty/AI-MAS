package Components.State;

import java.util.Observable;
import java.util.Observer;

public class Agent implements Observer {



    private final char name;
    private final String color;
    private int yCoord;
    private int xCoord;


    public Agent (char name, String color, int xCoord, int yCoord){
        this.name = name;
        this.color = color;
        this.xCoord = xCoord;
        this.yCoord = yCoord;


    }


    //GETTERS
    public char getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }
    //END GETTERS


    //SETTERS
    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }
    //END SETTERS

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public String toString() {
        return Character.toString(this.name);
    }


    //TODO implement agents
}
