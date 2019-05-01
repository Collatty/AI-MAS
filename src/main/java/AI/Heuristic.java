package AI;

import java.util.Comparator;
import java.util.List;

import Components.Agent;
import Components.State.Block;
import Components.State.Goal;
import Components.State.State;

public abstract class Heuristic implements Comparator<State> {

    public Heuristic(){}

    public static int h(State n, Agent agent, Goal goal){
        List<Block> blocks = n.getBlocks();
        char goalType = goal.getType();
        String agentColor = agent.getColor().toString();

        int heuristicValue = 10000;
        int tempHeuristicValue = 10000;

        for (Block block : blocks){
          String blockColor = block.getColor().toString();
          char blockType = block.getType();

          if(blockType == goalType && agentColor.equals(blockColor)) {
              tempHeuristicValue = 0;
              double distToBlock = manhattanDistance(goal.getCol(), goal.getRow(), block.getCol(),
                      block.getRow());
              tempHeuristicValue += distToBlock;
              System.err.println("Distance from block " + block.toString() + " to goal " + goal.toString() +
                    ": " + distToBlock +
                    "\t" + "Goal: [" + goal.getCol() + "," + goal.getRow() + "]" +
                    "\t" + "Block: [" + block.getCol() + "," + block.getRow() + "]");
              //IF GOAL IS SATISFIED, JUMP TO NEXT GOAL
              if(distToBlock == 0){ //could change to: if tempHeuristicvalue == 0 ?
                break;
              }

            //CALCULATING DISTANCE FROM BLOCK TO AGENT
            //blockColor should be consistent with enum color of agent
            double distanceToAgent = manhattanDistance(agent.getCol(), agent.getRow(), block.getCol(),
                        block.getRow()) - 1;
            tempHeuristicValue += distanceToAgent;
            System.err.println("Distance from agent " + agent.toString() + " to block " + block.toString() +
                    ": " + distanceToAgent +
                    "\t" + "Agent: [" + agent.getCol() + "," + agent.getRow() + "]" +
                    "\t" + "Block: [" + block.getCol() + "," + block.getRow() + "]");

            if(tempHeuristicValue < heuristicValue){
              heuristicValue = tempHeuristicValue;
            }
          }
        }
        return heuristicValue;
    }

    //EUCLIDEAN DISTANCE
    private static double euclidianDistance (double goalCordX, double goalCordY, double boxCordX, double boxCordY){
        return Math.hypot( (goalCordX-boxCordX), (goalCordY-boxCordY));
    }

    //MANHATTAN DISTANCE
    private static double manhattanDistance (double goalCordX, double goalCordY, double boxCordX, double boxCordY) {
        return (Math.abs(goalCordX-boxCordX) + Math.abs(goalCordY-boxCordY));
    }


/*public abstract int f(State n);

    @Override
    public int compare(State n1, State n2) {
        return this.f(n1) - this.f(n2);
    }

    public static class AStar extends Heuristic {
        public AStar(State initialState) {
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
