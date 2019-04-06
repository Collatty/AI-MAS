package Components.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Goal {

    private final char type;
    private final int column;
    private final int row;

    public Collection<Goal> predonditions = new HashSet<>();


    public Goal(char type, int row, int column) {
        this.type = type;
        this.row = row;
        this.column = column;

    }

    //GETTERS
    public char getType() {
        return type;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public Collection<Goal> getPredonditions() {
        return this.predonditions;
    }
    //END GETTERS

    @Override
    public String toString() {
        return Character.toString(this.type);
    }


}
