package AI;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Heuristic implements Comparator<State> {

    private List<Point2D.Double> goals = new ArrayList<>();


    public Heuristic(State initialState) {
        for (int i = 0; i<initialState.MAX_ROW; i++) {
            for (int j = 0; j<initialState.MAX_COL; j++) {
                if (initialState.goals[i][j] <= 'z' && initialState.goals[i][j] >= 'a') {
                    //change goal state representation according to haakon
                    goals.add(new Point2D.Double(i, j));
                }
            }
        }
    }

    public int h(State n) {
        int heuristicValue = 0;
        double distanceToAgent = 10000;
        List<Point2D.Double> boxes = new ArrayList<>();
        //List<Point2D.Double> boxesNotOnGoals = new ArrayList<>();
        //List<Point2D.Double> goalsWithBox = new ArrayList<>();
        Point2D.Double agent = new Point2D.Double(n.agentRow, n.agentCol);
        for (int i = 0; i < n.MAX_ROW; i++) {
            for (int j = 0; j < n.MAX_COL; j++) {
                if (n.boxes[i][j] <= 'Z' && n.boxes[i][j] >= 'A') {
                    boxes.add(new Point2D.Double(i, j));
                }
            }
        }
/*
        for (Point2D.Double box: boxes) {
            boolean onGoal = false;
            for (Point2D.Double goal: goals) {
                if ((box.getX() == goal.getX())&&(box.getY()==goal.getY())) {
                    onGoal = true;
                    goalsWithBox.add(goal);
                }
            }
            if (onGoal == false) {
                boxesNotOnGoals.add(box);
            }

        }

        *///List<Point2D.Double> unfinishedGoals = new ArrayList<>();
        //goals.removeAll(goalsWithBox);
        for (Point2D.Double box : boxes) {


            double distance = 10000;

            if (manhattanDistance(agent.getX(), agent.getY(), box.getX(), box.getY()) > distanceToAgent) {
                distanceToAgent = manhattanDistance(agent.getX(), agent.getY(), box.getX(), box.getY());
            }
            for (Point2D.Double goal : goals) {
                if (manhattanDistance(goal.getX(), goal.getY(), box.getX(), box.getY()) < distance) {
                    distance = manhattanDistance(goal.getX(), goal.getY(), box.getX(), box.getY());
                } if (manhattanDistance(goal.getX(), goal.getY(), box.getX(), box.getY()) == 0) {
                    distance = -100;
                }

            }
            heuristicValue += Math.round(distance);

        }
        heuristicValue += Math.round(distanceToAgent);

        return heuristicValue;
    }


    //euclidian distance
    public double euclidianDistance (double goalCordX, double goalCordY, double boxCordX, double boxCordY){
        return Math.hypot( (goalCordX-boxCordX), (goalCordY-boxCordY));

    }

    //manhattan distance
    public double manhattanDistance (double goalCordX, double goalCordY, double boxCordX, double boxCordY) {
        return (Math.abs(goalCordX-boxCordX) + Math.abs(goalCordY-boxCordY));
    }

    public abstract int f(State n);

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
}