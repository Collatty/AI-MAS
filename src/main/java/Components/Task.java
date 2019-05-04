package Components;

import java.util.List;

import Components.State.Goal;
import Components.State.State;

//TODO: Consider if attributes should be private and have getters and setters
public class Task {

    private static int counter = 0;
    private long id;
    private int row;
    private int col;
    private Color color;
    private List<Long> dependencies;
    private TaskType taskType;
    private State prestate;
    private Goal goal;

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public int getRow() {
	return row;
    }

    public void setRow(int row) {
	this.row = row;
    }

    public int getCol() {
	return col;
    }

    public void setCol(int col) {
	this.col = col;
    }

    public Color getColor() {
	return color;
    }

    public void setColor(Color color) {
	this.color = color;
    }

    public List<Long> getDependencies() {
	return dependencies;
    }

    public void setDependencies(List<Long> dependencies) {
	this.dependencies = dependencies;
    }

    public TaskType getTaskType() {
	return taskType;
    }

    public void setTaskType(TaskType taskType) {
	this.taskType = taskType;
    }


    public Task(int row, int col, Color color, List<Long> dependencies, Goal goal) {
        this.id = counter;
        counter++;
        this.row = row;
        this.col = col;
        this.color = color;
        this.dependencies = dependencies;
        this.goal = goal;

    }

    public State getPrestate() {
	return prestate;
    }


    @Override
    public String toString() {
        return "Task: " + this.id + "of color: " + this.color;
    }

    public void setPrestate(State prestate) {
	this.prestate = prestate;
    }

    public Goal getGoal() {
        return this.goal;
    }
}
