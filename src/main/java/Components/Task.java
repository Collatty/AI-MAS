package Components;

import java.util.List;

//TODO: Consider if attributes should be private and have getters and setters
public class Task {

    private long id;
    private int row;
    private int col;
    private Color color;
    private List<Long> dependencies;
    private TaskType taskType;

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

    public Task(int row, int col, List<Long> dependencies) {
	this.row = row;
	this.col = col;
	this.dependencies = dependencies;
    }

    public Task() {

    }

}
