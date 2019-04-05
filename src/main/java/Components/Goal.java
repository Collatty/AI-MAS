package Components;

public class Goal {

    private final String color;
    private int xCoordinate;
    private int yCoordinate;


    public Goal(String color, int xCoordinate, int yCoordinate) {
        this.color = color;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;

    }

    //GETTERS
    public String getColor() {
        return color;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }
    //END GETTERS

    //SETTERS
    public void setxCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public void setyCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
    }
    //END SETTERS


}
