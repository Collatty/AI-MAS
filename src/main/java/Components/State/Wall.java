package Components.State;


//THIS IS PROBABLY REDUNDANT
//TODO: MAYBE REFACTOR TO BOOLEAN VALUE IN TILE
public class Wall {

    private final int col;
    private final int row;

    public Wall(final int col, final int row) {
        this.row = row;
        this.col = col;
    }


    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    @Override
    public String toString() {
        return "+";
    }


}
