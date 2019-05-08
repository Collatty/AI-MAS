package Components.State;

import Components.Color;
import Components.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class Goal {

    private Collection<Goal> parents = new HashSet<>();
    private Collection<Goal> children = new HashSet<>();
    private Collection<Goal> preconditions = new HashSet<>();

    private final char type;
    private final int col;
    private final int row;
    private final Color color;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    private boolean completed;

    //By preconditions we mean goals that cannot be completed prior to this goal's attempted completion


    public Goal(char type, Color color, int row, int col) {
        this.type = type;
        this.color = color;
        this.row = row;
        this.col = col;
        this.completed = false;

        if (State.getInitialState().get(this.getRow()).get(this.getCol()).hasBlock()) {
            if (((Block) State.getInitialState().get(this.getRow()).get(this.getCol()).getTileOccupant()).getColor().equals(this.color)) {
                setCompleted(true);
            }
        }

    }

    //GETTERS
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

    //END GETTERS

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


    public Collection<Goal> getPreconditions() {
        return this.preconditions;
    }

}