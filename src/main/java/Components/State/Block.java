package Components.State;

public class Block {



    private final String color;
    private int column;
    private int row;

    public Block(String color, int column, int row) {
        this.color = color;
        this.column = column;
        this.row = row;
    }

    //GETTERS
    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public String getColor() {
        return color;
    }
    // END GETTERS

    //SETTERS
    public void setColumn(int column) {
        this.column = column;
    }

    public void setRow(int row) {
        this.row = row;
    }
    //END SETTERS

    @Override
    public String toString() {
        return this.color.substring(0,1).toUpperCase();
    }



}
