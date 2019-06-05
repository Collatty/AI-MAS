package AI;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;

import components.Agent;
import components.Color;
import components.state.Box;
import components.state.Goal;
import components.state.State;

public abstract class Heuristic implements Comparator<State> {

    private static AllPairsShortestPath apsp = new AllPairsShortestPath();

    public static HeuristicAndBox h(State n, Agent agent, Goal goal) {
	List<Box> boxes = State.getBoxes();
	char goalType = goal.getType();
	String agentColor = agent.getColor().toString();

	int heuristicValue = 10000;
	int tempHeuristicValue;
	Box tempBox = new Box('x', Color.BLUE, -1, -1);

	for (Box box : boxes) {
	    String boxColor = box.getColor().toString();
	    char boxType = box.getType();

	    if (boxType == goalType && agentColor.equals(boxColor)) {
		tempHeuristicValue = 0;
		double distToBox = Math.abs(bfs(goal.getRow(), goal.getCol(), box.getRow(), box.getCol()));
		tempHeuristicValue += distToBox;
		// IF GOAL IS SATISFIED, JUMP TO NEXT GOAL
		if (distToBox == 0) {
		    heuristicValue = tempHeuristicValue;
		    tempBox = box;
		    break;
		}

		// CALCULATING DISTANCE FROM BOX TO AGENT
		double distanceToAgent = Math.abs(bfs(agent.getRow(), agent.getCol(), box.getRow(), box.getCol()) - 1);
		tempHeuristicValue += distanceToAgent;

		if (tempHeuristicValue < heuristicValue) {
		    heuristicValue = tempHeuristicValue;
		    tempBox = box;
		}
	    }
	}
	return new HeuristicAndBox(heuristicValue, tempBox);
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
