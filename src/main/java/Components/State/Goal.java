package Components.State;

public class Goal {

    private final String color;
    private final int xCoordinate;
    private final int yCoordinate;


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

    @Override
    public String toString() {
        return this.color.substring(0,1).toLowerCase();
    }


}
