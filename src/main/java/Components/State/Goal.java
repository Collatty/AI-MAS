package Components.State;

public class Goal {

    private final String color;
    private final int column;
    private final int row;


    public Goal(String color, int column, int row) {
        this.color = color;
        this.column = column;
        this.row = row;

    }

    //GETTERS
    public String getColor() {
        return color;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
    //END GETTERS

    @Override
    public String toString() {
        return this.color.substring(0,1).toLowerCase();
    }


}
