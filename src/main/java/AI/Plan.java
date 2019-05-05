package AI;

import Components.Action;
import Components.State.State;

import java.util.ArrayList;
import java.util.List;

public abstract class Plan {
    //TODO: Implement this class.

    protected final int startRow;
    protected final int startCol;
    protected final int endRow;
    protected final int endCol;
    protected Node[][] nodes;

    public List<Action> getPlan() {
        return plan;
    }

    protected List<Action> plan = new ArrayList<>();

    public Plan(int startRow, int startCol, int endRow, int endCol) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
    }

    private void setNodes() {
        for (int i = 0; i < State.getInitialState().size(); i++) {
            for (int j = 0; j < State.getInitialState().get(i).size(); j++) {
                Node Node = new Node(i, j);
                Node.calculateHeuristic(getFinalNode());
                if(State.getInitialState().get(i).get(j).isWall()) {
                    Node.setBlock(true);
                }
                this.nodes[i][j] = Node;
            }
        }
    }

    public abstract void calculatePlan();

    public List<Node> aStarSearch(int startRow, int startCol, int endRow, int endCol) {
        AStarSearch search = new AStarSearch(State.getInitialState().size(), State.getMaxCol(), new Node(startRow,
                startCol), new Node(endRow, endCol), 1);
        return search.findPath();
    }



    //GETTERS
    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndRow() {
        return endRow;
    }


    public int getEndCol() {
        return endCol;
    }
    //END GETTERS

    public Node getFinalNode() {
        return new Node(this.endRow, this.endCol);
    }







    public static class MovePlan extends Plan{


        public MovePlan(int startRow, int startCol, int endRow, int endCol) {
            super(startRow, startCol, endRow, endCol);
            calculatePlan();

        }

        @Override
        public void calculatePlan() {
            Node previous = null ;
            List<Node> searchResults = aStarSearch(this.startRow, this.startCol, this.endRow, this.endCol);
            for (Node step : searchResults) {
                if (previous != null){
                    this.plan.add(
                            new Action(
                                    State.getInitialState().get(previous.getRow()).get(previous.getCol()),
                                    State.getInitialState().get(step.getRow()).get(step.getCol())
                            ));
                }
                previous = step;
            }
        }




    }

    public static class MoveBoxPlan extends Plan{

        private final int startBoxRow;
        private final int startBoxCol;

        public MoveBoxPlan(int startRow, int startCol, int startBoxRow, int startBoxCol, int endRow, int endCol) {
            super(startRow, startCol, endRow, endCol);
            this.startBoxRow = startBoxRow;
            this.startBoxCol = startBoxCol;
            calculatePlan();



        }

        @Override
        public void calculatePlan() {
            MovePlan movePlan = new MovePlan(this.startRow, this.startCol, this.startBoxRow, this.startBoxCol);
            List<Action> partialPlan = movePlan.getPlan();
            if( partialPlan.size()!=0) {
                partialPlan.remove(partialPlan.size()-1); // Removing the move moving the agent into the box
            }
            List<Node> searchResults = aStarSearch(this.startBoxRow, this.startBoxCol, this.endRow, this.endCol);
            Node previous = null;
            for (Node step : searchResults) {
                if (previous != null) {
                    partialPlan.add(new Action(State.getInitialState().get(previous.getRow()).get(previous.getCol()),
                                    State.getInitialState().get(step.getRow()).get(step.getCol()),
                            partialPlan.get(partialPlan.size()-1).getEndAgent()));
                }
                previous = step;
            }
            this.plan = partialPlan;
        }
    }
}
