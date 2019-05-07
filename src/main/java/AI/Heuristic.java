package AI;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;

import Components.Agent;
import Components.Color;
import Components.State.Block;
import Components.State.Goal;
import Components.State.State;

public abstract class Heuristic implements Comparator<State> {

    private static AllPairsShortestPath apsp = new AllPairsShortestPath();

    public static HeuristicAndBlock h(State n, Agent agent, Goal goal){


        List<Block> blocks = n.getBlocks();
        char goalType = goal.getType();
        String agentColor = agent.getColor().toString();

        int heuristicValue = 10000;
        int tempHeuristicValue;
        Block tempBlock = new Block('x', Color.BLUE, -1, -1);

        for (Block block : blocks){
          String blockColor = block.getColor().toString();
          char blockType = block.getType();

          if(blockType == goalType && agentColor.equals(blockColor)) {
              tempHeuristicValue = 0;
              //double distToBlock = manhattanDistance(goal.getCol(), goal.getRow(), block.getCol(),
              //        block.getRow());
              double distToBlock = Math.abs(bfs(goal.getRow(), goal.getCol(), block.getRow(),
                      block.getCol()));
              tempHeuristicValue += distToBlock;
              System.err.println("Distance from block " + block.toString() + " to goal " + goal.toString() +
                    ": " + distToBlock +
                    "\t" + "Goal: [" + goal.getRow() + "," + goal.getCol() + "]" +
                    "\t" + "Block: [" + block.getRow() + "," + block.getCol() + "]");
              //IF GOAL IS SATISFIED, JUMP TO NEXT GOAL
              if(distToBlock == 0){ //could change to: if tempHeuristicvalue == 0 ?
                break;
              }

            //CALCULATING DISTANCE FROM BLOCK TO AGENT
            //blockColor should be consistent with enum color of agent
            //double distanceToAgent = manhattanDistance(agent.getCol(), agent.getRow(), block.getCol(),
            //            block.getRow()) - 1;
            double distanceToAgent = Math.abs(bfs(agent.getRow(), agent.getCol(), block.getRow(),
                        block.getCol()) - 1);
            tempHeuristicValue += distanceToAgent;
            System.err.println("Distance from agent " + agent.toString() + " to block " + block.toString() +
                    ": " + distanceToAgent +
                    "\t" + "Agent: [" + agent.getRow() + "," + agent.getCol() + "]" +
                    "\t" + "Block: [" + block.getRow() + "," + block.getCol() + "]");

            if(tempHeuristicValue < heuristicValue){
              heuristicValue = tempHeuristicValue;
              tempBlock = block;
            }
          }
        }
        return new HeuristicAndBlock(heuristicValue, tempBlock);
    }

    //CHOOSE HEURISTIC

   /* private static float chooseHeuristic(char method, float goalCordX, float goalCordY, float boxCordX, float boxCordY){
        method = Character.toLowerCase(method);
        switch(method) { //e=euclidianDistance, b = bfs, m = manhattanDistance
            case 'e':
              return (float) euclidianDistance(goalCordX, goalCordY, boxCordX, boxCordY);
            case 'b':
              return bfs(goalCordX, goalCordY, boxCordX, boxCordY);
            default:
              return manhattanDistance(goalCordX, goalCordY, boxCordX, boxCordY);
          }
    }*/

    //EUCLIDEAN DISTANCE
    private static double euclidianDistance (double goalRow, double goalCol, double boxRow, double boxCol){
        return Math.hypot( (goalRow-boxRow), (goalCol-boxCol));
    }

    //MANHATTAN DISTANCE
    private static float manhattanDistance (float goalRow, float goalCol, float boxRow, float boxCol) {
        return (Math.abs(goalRow-boxRow) + Math.abs(goalCol-boxCol));
    }

    //BFS
    private static float bfs (float goalRow, float goalCol, float boxRow, float boxCol) {
        Point2D.Float start = new Point2D.Float(goalRow, goalCol);
        Point2D.Float end = new Point2D.Float(boxRow, boxCol);
        return apsp.getHeuristic(start, end);
    }


/*public abstract int f(State n);

    @Override
    public int compare(State n1, State n2) {
        return this.f(n1) - this.f(n2);
    }

    public static class AStarSearch extends Heuristic {
        public AStarSearch(State initialState) {
            super(initialState);
        }

        @Override
        public int f(State n) {
            return n.g() + this.h(n);
        }

        @Override
        public String toString() {
            return "A* evaluation";
        }
    }

    public static class WeightedAStar extends Heuristic {
        private int W;

        public WeightedAStar(State initialState, int W) {
            super(initialState);
            this.W = W;
        }

        @Override
        public int f(State n) {
            return n.g() + this.W * this.h(n);
        }

        @Override
        public String toString() {
            return String.format("WA*(%d) evaluation", this.W);
        }
    }

    public static class Greedy extends Heuristic {
        public Greedy(State initialState) {
            super(initialState);
        }

        @Override
        public int f(State n) {
            return this.h(n);
        }

        @Override
        public String toString() {
            return "Greedy evaluation";
        }
    }
    */
}
