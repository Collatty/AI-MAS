package Components;

import Components.State.Goal;
import Components.State.State;
import Components.State.Tile;

import static Components.Action.ActionType.*;


public class Action {

    public ActionType getActionType() {
        return actionType;
    }

    public Tile getStartAgent() {
        return startAgent;

    }

    public Tile getEndAgent() {
        return endAgent;
    }

    public Tile getStartBox() {
        return startBox;

    }


    public Tile getEndBox() {
        return endBox;

    }

    private ActionType actionType;
    private Tile startAgent;
    private Tile endAgent;
    private Tile startBox;
    private Tile endBox;
    private Direction agentDirection = null;
    private Direction boxDirection = null;

    public enum ActionType {
        Move,
        Push,
        Pull,
        NoOp
    }

    public Action(Tile startAgent, Tile endAgent) {
        this.actionType = Move;
        this.startAgent = startAgent;
        this.endAgent = endAgent;
        setDirections();

    }

    public Action(Tile startBox, Tile endBox, Tile startAgent) {
        if (endBox.isGoal()){
            endBox.getGoal().setCompleted(true);
        }
        this.startBox = startBox;
        this.endBox = endBox;
        this.startAgent = startAgent;
        if (startAgent.getRow() == endBox.getRow() && startAgent.getCol() == endBox.getCol()) {
            this.actionType = Pull;
            if (startBox.getRow() - endBox.getRow() == 0) {
                if (!endBox.getNorthNeighbor().isWall()) {
                    this.endAgent = endBox.getNorthNeighbor();
                } else if (!endBox.getSouthNeighbor().isWall()) {
                    this.endAgent = endBox.getSouthNeighbor();
                } else {
                    this.endAgent =
                            State.getInitialState()
                                    .get(startAgent.getRow() + (endBox.getRow() - startBox.getRow())).get(startAgent.getCol()
                                    + (endBox.getCol() - startBox.getCol()));
                }
            } else {
                if (!endBox.getEastNeighbor().isWall()) {
                    this.endAgent = endBox.getEastNeighbor();
                } else if (!endBox.getWestNeighbor().isWall()) {
                    this.endAgent = endBox.getWestNeighbor();
                } else {
                    this.endAgent =
                            State.getInitialState()
                                    .get(startAgent.getRow() + (endBox.getRow() - startBox.getRow())).get(startAgent.getCol()
                                    + (endBox.getCol() - startBox.getCol()));
                }
            }
        } else {
            this.actionType = Push;
            this.endAgent = startBox;
        }
        setDirections();
    }


    public Action() {
        this.actionType = NoOp;
        setDirections();

    }

    public enum Direction {
        N, W, E, S
    }

    private void setDirections() {
        if(this.actionType == NoOp) {
            return;
        }
        if (this.startAgent.getRow()-this.endAgent.getRow() == 1) {
            this.agentDirection = Direction.N;
        } else if (this.startAgent.getRow()-this.endAgent.getRow() == -1) {
            this.agentDirection = Direction.S;
        } else if (this.startAgent.getCol()-this.endAgent.getCol() == 1) {
            this.agentDirection = Direction.W;
        } else if (this.startAgent.getCol()-this.endAgent.getCol() == -1) {
            this.agentDirection = Direction.E;
        }
        if (this.actionType == Pull) {
            if (this.startBox.getRow()-this.endBox.getRow() == 1) {
                this.boxDirection = Direction.N;
            } else if (this.startBox.getRow()-this.endBox.getRow() == -1) {
                this.boxDirection = Direction.S;
            } else if (this.startBox.getCol()-this.endBox.getCol() == 1) {
                this.boxDirection = Direction.E;
            } else if (this.startBox.getCol()-this.endBox.getCol() == -1) {
                this.boxDirection = Direction.W;
            }
        }
        if (this.actionType == Push) {
            if (this.startBox.getRow()-this.endBox.getRow() == 1) {
                this.boxDirection = Direction.N;
            } else if (this.startBox.getRow()-this.endBox.getRow() == -1) {
                this.boxDirection = Direction.S;
            } else if (this.startBox.getCol()-this.endBox.getCol() == 1) {
                this.boxDirection = Direction.W;
            } else if (this.startBox.getCol()-this.endBox.getCol() == -1) {
                this.boxDirection = Direction.E;
            }
        }

    }



    @Override
    public String toString() {
        if (this.actionType == Push || this.actionType == Pull)
            return this.actionType.toString() + "(" + this.agentDirection.toString() + "," + this.boxDirection.toString() + ")";
        if (this.actionType == Move)
            return this.actionType.toString() + "(" + this.agentDirection.toString() + ")";
        return this.actionType.toString();
    }

}