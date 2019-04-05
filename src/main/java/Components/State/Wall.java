package Components.State;

public class Wall {

    private final int xCoordinate;
    private final int yCoordinate;

    public Wall(final int xCoordinate, final int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }


    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    @Override
    public String toString() {
        return "+";
    }


}
