package Components.State;

import Components.Agent;

import java.util.Collection;
import java.util.HashSet;

public class Tile {


    private final int col;
    private final int row;

    private Object tileOccupant;
    private Goal goal;
    private boolean isWall;

    private Tile northNeighbor;
    private Tile southNeighbor;
    private Tile eastNeighbor;
    private Tile westNeighbor;

    public Collection<Tile> getNeighbors() {

        Collection<Tile> neighbors = new HashSet<>();
        if (this.northNeighbor != null) {
            neighbors.add(this.northNeighbor);
        }
        if (this.southNeighbor != null) {
            neighbors.add(this.southNeighbor);
        }
        if (this.eastNeighbor != null) {
            neighbors.add(this.eastNeighbor);
        }
        if (this.westNeighbor != null) {
            neighbors.add(this.westNeighbor);
        }
        return neighbors;
    }

    public Tile(final int row, final int col) {
        this.col = col;
        this.row = row;
        this.tileOccupant = null;
        this.goal = null;


    }

    //GETTERS
    public int getCol() {
        return col;
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

    public Goal getGoal() {
        return this.goal;
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

    public void setWall (boolean isWall) {
        this.isWall = isWall;
    }

    //END SETTERS

    public void removeTileOccupant() {
        this.tileOccupant = null;
    }

    public boolean isGoal() {
        return this.goal != null;
    }

    public boolean isWall() {
        return this.isWall;
    }

    public boolean hasBlock() {
        return this.tileOccupant instanceof Block;
    }

    public boolean hasAgent() {
        return this.tileOccupant instanceof Agent;
    }

    public boolean isFree() {
        return this.tileOccupant == null;
    }

    public boolean isCompletedGoal () {
        if (this.isGoal() && this.hasBlock()){
            return this.goal.getType() == ((Block) this.getTileOccupant()).getType();
        }
        return false;
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
