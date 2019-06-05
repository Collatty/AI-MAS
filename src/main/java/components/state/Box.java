package components.state;

import java.util.ArrayList;
import java.util.List;

import components.Agent;
import components.Color;

public class Box {
    private char type;
    private final Color color;
    private int row;
    private int col;
    private List<Agent> reachableAgents;

    public Box(char type, Color color, int row, int col) {
	this.type = type;
	this.color = color;
	this.row = row;
	this.col = col;
	this.reachableAgents = new ArrayList<>();
    }

    // GETTERS
    public int getCol() {
	return col;
    }

    public int getRow() {
	return row;
    }

    public Color getColor() {
	return color;
    }

    public char getType() {
	return type;
    }

    public List<Agent> getReachableAgents() {
	return reachableAgents;
    }
    // END GETTERS

    // SETTERS
    public void setCol(int col) {
	this.col = col;
    }

    public void setRow(int row) {
	this.row = row;
    }
    // END SETTERS

    public void addReachableAgent(Agent agent) {
	reachableAgents.add(agent);
    }

    @Override
    public String toString() {
	return this.color.toString().toUpperCase();
    }
}
