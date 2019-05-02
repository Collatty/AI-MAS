package Components;

import Components.State.State;
import Components.State.Tile;

import static Components.ActionsRetake.ActionType.*;


public class ActionsRetake {

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

    public enum ActionType{
        MOVE,
        PUSH,
        PULL,
        NOOP
    }

    public ActionsRetake(Tile startAgent, Tile endAgent) {
        this.actionType = MOVE;
        this.startAgent = startAgent;
        this.endAgent = endAgent;

    }

    public ActionsRetake(Tile startBox, Tile endBox, Tile startAgent) {
        this.startAgent = startAgent;
        this.startBox = startBox;
        this.endBox = endBox;
        if (startAgent.getRow() == endBox.getRow() && startAgent.getCol() == endBox.getCol()) {
            this.actionType = PULL;
                if (startBox.getRow() - endBox.getRow() == 0) {
                    if(!endBox.getNorthNeighbor().isWall()) {
                        this.endAgent = endBox.getNorthNeighbor();
                    } else if (!endBox.getSouthNeighbor().isWall()) {
                        this.endAgent = endBox.getSouthNeighbor();
                    } else {
                        this.endAgent =
                                State.getInitialState()
                                        .get(startAgent.getRow() + (endBox.getRow()-startBox.getRow())).get(startAgent.getCol()
                                        + (endBox.getCol() - startBox.getCol()));
                    }
                } else {
                    if(!endBox.getEastNeighbor().isWall()) {
                        this.endAgent = endBox.getEastNeighbor();
                    } else if (!endBox.getWestNeighbor().isWall()) {
                        this.endAgent = endBox.getWestNeighbor();
                    } else {
                        this.endAgent =
                                State.getInitialState()
                                        .get(startAgent.getRow() + (endBox.getRow()-startBox.getRow())).get(startAgent.getCol()
                                        + (endBox.getCol() - startBox.getCol()));
                    }
                }
        } else {
            this.actionType = PUSH;
            this.endAgent = startBox;
        }
    }

    public ActionsRetake() {
        this.actionType = NOOP;
    }


}
