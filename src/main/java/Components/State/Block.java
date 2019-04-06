package Components.State;

public class Block {


    private char name;
    private final String color;
    private int column;
    private int row;

    public Block(char name, String color, int row, int column) {
        this.name = name;
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

    public char getName() {
        return name;
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
