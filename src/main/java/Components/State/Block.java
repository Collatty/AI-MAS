package Components.State;

public class Block {

	// TODO: Consider changing color to our type Color
    private char type;
    private final String color;
    private int row;
    private int col;


    public Block(char type, String color, int col, int row) {
        this.type = type;
        this.color = color;
        this.row = row;
        this.col = col;
    }

    //GETTERS
    public int getCol() {
        return col;
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
    public void setCol(int col) {
        this.col = col;
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
