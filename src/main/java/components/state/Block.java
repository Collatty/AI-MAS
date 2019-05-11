package components.state;
import components.Color;

public class Block {

	// TODO: Consider changing color to our type Color
    private char type;
    private final Color color;
    private int row;
    private int col;


    public Block(char type, Color color, int row, int col) {
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

    public Color getColor() {
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
        return this.color.toString().toUpperCase();
    }



}
