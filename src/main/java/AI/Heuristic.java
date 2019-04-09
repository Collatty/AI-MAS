package AI;

import java.util.Comparator;
import java.util.List;

import Components.Agent;
import Components.State.Block;
import Components.State.Goal;
import Components.State.State;

public abstract class Heuristic implements Comparator<State> {

    private List<Goal> goals;// = new ArrayList<>();
    private int heuristic = -1;


    public Heuristic() {
        goals = State.getGoals();
    }

    public int h(State n) {
        int heuristicValue = 0;
        List<Block> blocks = n.getBlocks();
        List<Agent> agents = n.getAgents();

        //only use unfinished goal?

        for (Block block : blocks) {
            double distance = 10000;
            double distanceToAgent = 10000;
            String blockColor = block.getColor();
            char blockType = block.getType();

            //CALCULATING DISTANCE FROM BLOCK TO NEAREST POSSIBLE AGENT
            for (Agent agent : agents) {
                String agentColor = agent.getColor().toString();
                if(agentColor.equals(blockColor.toUpperCase())){
                    if (manhattanDistance(agent.getCol(), agent.getRow(), block.getColumn(), block.getRow()) < distanceToAgent) {
                        distanceToAgent = manhattanDistance(agent.getCol(), agent.getRow(), block.getColumn(), block.getRow());
                        /*System.err.println("Distance from agent " + agent.toString() + " to block " + block.toString() +
                        ": " + distanceToAgent);
                        */
                    }
                }
            }
            //CALCULATING DISTANCE FROM BLOCK TO NEAREST POSSIBLE GOAL
            for (Goal goal : goals) {
                char goalType = goal.getType();
                if(goalType == blockType) {
                    if (manhattanDistance(goal.getColumn(), goal.getRow(), block.getColumn(), block.getRow()) < distance) {
                        distance = manhattanDistance(goal.getColumn(), goal.getRow(), block.getColumn(), block.getRow());
                    }
                    if (manhattanDistance(goal.getColumn(), goal.getRow(), block.getColumn(), block.getRow()) == 0) {
                        distance = -100;
                    }
                    /*System.err.println("Distance from block " + block.toString() + " to goal " + goal.toString() +
                    ": " + distance);*/
                }

            }
            //System.err.println("FinalDistToAgent: " + distanceToAgent);
            //System.err.println("FinalDist: " + distanceToAgent);

            heuristicValue += Math.round(distanceToAgent);
            heuristicValue += Math.round(distance);
        }

        this.heuristic = heuristicValue;
        return heuristicValue;
    }

    @Override
    public String toString() {
        return Integer.toString(this.heuristic);
    }

    //euclidian distance
    public double euclidianDistance (double goalCordX, double goalCordY, double boxCordX, double boxCordY){
        return Math.hypot( (goalCordX-boxCordX), (goalCordY-boxCordY));

    }

    //manhattan distance
    public double manhattanDistance (double goalCordX, double goalCordY, double boxCordX, double boxCordY) {
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
