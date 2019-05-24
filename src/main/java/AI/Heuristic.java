package AI;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;

import components.Agent;
import components.Color;
import components.state.Block;
import components.state.Goal;
import components.state.State;

public abstract class Heuristic implements Comparator<State> {

    private static AllPairsShortestPath apsp = new AllPairsShortestPath();

    public static HeuristicAndBlock h(State n, Agent agent, Goal goal) {
	List<Block> blocks = State.getBlocks();
	char goalType = goal.getType();
	String agentColor = agent.getColor().toString();

	int heuristicValue = 10000;
	int tempHeuristicValue;
	Block tempBlock = new Block('x', Color.BLUE, -1, -1);

	for (Block block : blocks) {
	    String blockColor = block.getColor().toString();
	    char blockType = block.getType();

	    if (blockType == goalType && agentColor.equals(blockColor)) {
		tempHeuristicValue = 0;
		double distToBlock = Math.abs(bfs(goal.getRow(), goal.getCol(), block.getRow(), block.getCol()));
		tempHeuristicValue += distToBlock;
		// IF GOAL IS SATISFIED, JUMP TO NEXT GOAL
		if (distToBlock == 0) {
		    heuristicValue = tempHeuristicValue;
		    tempBlock = block;
		    break;
		}

		// CALCULATING DISTANCE FROM BLOCK TO AGENT
		double distanceToAgent = Math
			.abs(bfs(agent.getRow(), agent.getCol(), block.getRow(), block.getCol()) - 1);
		tempHeuristicValue += distanceToAgent;

		if (tempHeuristicValue < heuristicValue) {
		    heuristicValue = tempHeuristicValue;
		    tempBlock = block;
		}
	    }
	}
	return new HeuristicAndBlock(heuristicValue, tempBlock);
    }

    // BFS
    private static float bfs(float goalRow, float goalCol, float boxRow, float boxCol) {
	Point2D.Float start = new Point2D.Float(goalRow, goalCol);
	Point2D.Float end = new Point2D.Float(boxRow, boxCol);
	return apsp.getHeuristic(start, end);
    }

    public static void loadNewLevel() {
	apsp = new AllPairsShortestPath();
    }
}
