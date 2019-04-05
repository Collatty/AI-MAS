package Components.State;

import java.util.ArrayList;
import java.util.Collection;

public class Tile {

    private final int column;
    private final int row;

    private Object tileOccupant;
    private Goal goal;

    private Tile northNeighbor;
    private Tile southNeighbor;
    private Tile eastNeighbor;
    private Tile westNeighbor;

    public static Collection<Tile> neighbors = new ArrayList<>();


    public Tile(final int row, final int column) {
        this.column = row;
        this.row = column;
        this.tileOccupant = null;
        this.goal = null;


    }

    //GETTERS
    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public Object getTileOccupant() {
        return tileOccupant;
    }

    public Tile getTile() {
        return this;
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

    public void setGoal(Goal goal) {
        this.goal = goal;
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

    public boolean isGoal() {
        return this.goal != null;
    }

    public boolean isWall() {
        return this.tileOccupant instanceof Wall;
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
        if (this.isFree()) {
            if (this.isGoal()) {
                return this.goal.toString();
            }
            return "-";
        }
        return this.tileOccupant.toString();
    }

}
