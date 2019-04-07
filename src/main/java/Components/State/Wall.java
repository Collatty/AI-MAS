package Components.State;


//THIS IS PROBABLY REDUNDANT
//TODO: MAYBE REFACTOR TO BOOLEAN VALUE IN TILE
public class Wall {

    private final int column;
    private final int row;

    public Wall(final int row, final int column) {
        this.row = row;
        this.column = column;
    }


    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public String toString() {
        return "+";
    }


}
