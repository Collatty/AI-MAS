package components.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import components.Color;

public class Goal {
    private Collection<Goal> parents = new HashSet<>();
    private Collection<Goal> children = new HashSet<>();
    private Collection<Goal> preconditions = new HashSet<>();
    private List<Block> reachableBlocks;

    private final char type;
    private final int col;
    private final int row;
    private final Color color;
    private boolean completed;

    public Goal(char type, Color color, int row, int col) {
	this.type = type;
	this.color = color;
	this.row = row;
	this.col = col;
	this.completed = false;
	this.reachableBlocks = new ArrayList<>();

	if (State.getInitialState().get(this.getRow()).get(this.getCol()).hasBlock()) {
	    if (((Block) State.getInitialState().get(this.getRow()).get(this.getCol()).getTileOccupant())
		    .getType() == (this.type)) {
		setCompleted(true);
	    }
	}
    }

    public boolean isCompleted() {
	return completed;
    }

    public char getType() {
	return type;
    }

    public int getCol() {
	return col;
    }

    public int getRow() {
	return row;
    }

    public Color getColor() {
	return color;
    }

    public Collection<Goal> getPreconditions() {
	return this.preconditions;
    }

    public List<Block> getReachableBlocks() {
	return reachableBlocks;
    }

    public Collection<Goal> getParents() {
	return parents;
    }

    public void setParent(Goal parent) {
	this.parents.add(parent);
    }

    public Collection<Goal> getChildren() {
	return children;
    }

    public void setChild(Goal child) {
	this.children.add(child);
    }

    public void setCompleted(boolean completed) {
	this.completed = completed;
    }

    public void populatePreconditions() {
	findTopOfTree();
	accumulatePreconditions();
    }

    private void accumulatePreconditions() {
	for (Goal goal : this.children) {
	    if (goal.getChildren().isEmpty()) {
		this.preconditions.add(goal);
	    } else {
		goal.accumulatePreconditions();
	    }
	}
    }

    private void findTopOfTree() {
	if (!this.parents.isEmpty()) {
	    for (Goal goal : this.parents) {
		goal.findTopOfTree();
	    }
	}
    }

    @Override
    public String toString() {
	return Character.toString(this.type);
    }

    public void addReachableBlock(Block block) {
	reachableBlocks.add(block);
    }

}