package components.state;

import components.Color;

import java.util.Collection;
import java.util.HashSet;


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

        System.err.println("Constructing goal");

        //TODO: Here we basically set a golved to solved if a block of the right color is standing on top of it.
        // this can, however, be problematic if this box has to moved for another goal to be solved for instance.
        if (State.getInitialState().get(this.getRow()).get(this.getCol()).hasBlock()) {
            if (((Block) State.getInitialState().get(this.getRow()).get(this.getCol()).getTileOccupant()).getColor().equals(this.color)) {
                System.err.println("Goal 42: " + Character.toString(this.type) + " is completed by default");
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