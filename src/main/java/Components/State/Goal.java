package Components.State;

import Components.Color;
import Components.Task;

import java.util.Collection;
import java.util.HashSet;

public class Goal {

    private final char type;
    private final int col;
    private final int row;
    private final Color color;

    //By preconditions we mean goals that cannot be completed prior to this goal's attempted completion
    private Collection<Goal> preconditions = new HashSet<>();


    public Goal(char type, Color color, int row, int col) {
        this.type = type;
        this.color = color;
        this.row = row;
        this.col = col;

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

    public Collection<Goal> getPreconditions() {
        return this.preconditions;
    }
    //END GETTERS

    public void setPrecondition(Goal goal){
        this.preconditions.add(goal);
    }

    public Collection<Goal> accumulatePreconditions() {
        Collection<Goal> accumulatedPreconditions = new HashSet<>();
        if (!this.preconditions.isEmpty()) {
            for (Goal goal : this.preconditions) {
                if (goal.getPreconditions() != null)
                    accumulatedPreconditions.addAll(goal.accumulatePreconditions());
            }
        }

        this.preconditions.addAll(accumulatedPreconditions);
        return this.preconditions;
    }

    @Override
    public String toString() {
        return Character.toString(this.type);
    }


}
