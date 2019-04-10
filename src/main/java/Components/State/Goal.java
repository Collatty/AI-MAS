package Components.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Goal {

    private final char type;
    private final int col;
    private final int row;

    private Collection<Goal> predonditions = new HashSet<>();


    public Goal(char type, int col, int row) {
        this.type = type;
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

    public Collection<Goal> getPredonditions() {
        return this.predonditions;
    }
    //END GETTERS

    public void setPredondition(Goal goal){
        this.predonditions.add(goal);
    }

    @Override
    public String toString() {
        return Character.toString(this.type);
    }


}
