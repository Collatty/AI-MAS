package Components.State;

public class Wall {

    private final int column;
    private final int row;

    public Wall(final int column, final int row) {
        this.column = column;
        this.row = row;
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
