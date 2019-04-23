package Components;

import AI.Heuristic;
import Components.State.Tile;

import java.util.ArrayList;
import java.util.List;

public class Plan {
    //TODO: Implement this class.

    private final int agentRow;
    private final int agentCol;
    private final int boxRow;
    private final int boxCol;
    private final int goalRow;
    private final int goalCol;

    private List<Action> plan = new ArrayList<>();

    public Plan(int agentRow, int agentCol, int boxRow, int boxCol, int goalRow, int goalCol) {
        this.agentRow = agentRow;
        this.agentCol = agentCol;
        this.boxRow = boxRow;
        this.boxCol = boxCol;
        this.goalRow = goalRow;
        this.goalCol = goalCol;

    }

    public Plan(int agentRow, int agentCol, int goalRow, int goalCol) {
        this.agentRow = agentRow;
        this.agentCol = agentCol;
        this.boxRow = -1;
        this.boxCol = -1;
        this.goalRow = goalRow;
        this.goalCol = goalCol;
    }

    public void calculatePlan() {
        //TODO
    }

    public void aStarSearch() {
        List<Tile> expandedTiles = new ArrayList<>();
        Tile initialTile = 

    }



    //GETTERS
    public int getAgentRow() {
        return agentRow;
    }

    public int getAgentCol() {
        return agentCol;
    }

    public int getGoalRow() {
        return goalRow;
    }

    public int getGoalCol() {
        return goalCol;
    }
}
