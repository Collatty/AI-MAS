package Components.State;

public class Block {


    private char type;
    private final String color;
    private int column;
    private int row;

    public Block(char type, String color, int row, int column) {
        this.type = type;
        this.color = color;
        this.row = row;
        this.column = column;
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

    public char getType() {
        return type;
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
