package Components.State;

public class Block {



    private final String color;
    private int xCoordinate;
    private int yCoordinate;

    public Block(String color, int xCoordinate, int yCoordinate) {
        this.color = color;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    //GETTERS
    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public String getColor() {
        return color;
    }
    // END GETTERS

    //SETTERS
    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
    //END SETTERS

    @Override
    public String toString() {
        return this.color.substring(0,1).toUpperCase();
    }



}
