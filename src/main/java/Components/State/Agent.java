package Components.State;

import java.util.Observable;
import java.util.Observer;

public class Agent implements Observer {



    private final char name;
    private final String color;
    private int row;
    private int column;


    public Agent (char name, String color, int row, int column){
        this.name = name;
        this.color = color;
        this.row = row;
        this.column = column;
    }


    //GETTERS
    public char getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
    //END GETTERS


    //SETTERS
    public void setColumn(int column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
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
