package Components.State;

import java.util.ArrayList;
import java.util.Collection;

public class Tile {

    private final int xCoordinate;
    private final int yCoordinate;

    private Object tileOccupant;

    private Tile northNeighbor;
    private Tile southNeighbor;
    private Tile eastNeighbor;
    private Tile westNeighbor;

    public static Collection<Tile> neighbors = new ArrayList<>();


    public Tile(final int xCoordinate, final int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.tileOccupant = null;

    }

    //GETTERS
    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public Object getTileOccupant() {
        return tileOccupant;
    }

    public Tile getNorthNeighbor() {
        return northNeighbor;
    }

    public Tile getSouthNeighbor() {
        return southNeighbor;
    }

    public Tile getEastNeighbor() {
        return eastNeighbor;
    }

    public Tile getWestNeighbor() {
        return westNeighbor;
    }
    //END GETTERS

    //SETTERS
    public void setTileOccupant(Object tileOccupant) {
        this.tileOccupant = tileOccupant;
    }

    public void setWestNeighbor(Tile westNeighbor) {
        this.westNeighbor = westNeighbor;
    }

    public void setEastNeighbor(Tile eastNeighbor) {
        this.eastNeighbor = eastNeighbor;
    }

    public void setNorthNeighbor(Tile northNeighbor) {
        this.northNeighbor = northNeighbor;
    }

    public void setSouthNeighbor(Tile southNeighbor) {
        this.southNeighbor = southNeighbor;
    }

    //END SETTERS

    public void removeTileOccupant() {
        this.tileOccupant = null;
    }

    public boolean isWall() {
        return this.tileOccupant instanceof Wall;
    }

    public boolean isGoal() {
        return this.tileOccupant instanceof Goal;
    }

    public boolean isBox() {
        return this.tileOccupant instanceof Block;
    }

    public boolean isAgent() {
        return this.tileOccupant instanceof Agent;
    }

    public boolean isFree() {
        return this.tileOccupant == null;
    }

    @Override
    public String toString() {
        return this.isFree() ? "-" : this.tileOccupant.toString();
    }

}
