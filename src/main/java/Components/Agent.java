package Components;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;


public class Agent implements Subscriber<MessageToAgent> {
    private final int agentNumber;
    private final Color color;
    private int col;
    private int row;
    private Subscription blackboardChannel;
    private BlackBoard blackBoard;
    private boolean workingOnPlan;

    public Agent (int agentNumber, Color color, int col, int row, BlackBoard blackBoard) {
        this.agentNumber = agentNumber;
        this.color = color;
        this.col = col;
        this.row = row;
        this.blackBoard = blackBoard;
        this.workingOnPlan = false;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.blackboardChannel = subscription;
        this.blackboardChannel.request(1);
    }

    @Override
    public void onNext(MessageToAgent message) {
        if (message.toAll != null && message.toAll
        		|| message.toColor != null && message.toColor == color
                || message.toAgent != null && message.toAgent == agentNumber) {        	
            if (message.messageType == MessageType.HEURISTIC) {
                proposeHeuristic(calculateHeuristic(message.task), message.task);            
            } else if (message.messageType == MessageType.PLAN) {
                //TODO: Make awesome plan
                System.err.println("Agent " + agentNumber + " is planning for task " + message.task.getId());
                createPlan(message.task);
                //System.err.println("Agent " + agentNumber + " makes plan for task " + message.task.id);
            }
        }
        this.blackboardChannel.request(1);
    }

    private void createPlan(Task task) {
    	workingOnPlan = true;
        int startIndex = 0;
        for(Long dependencyId : task.getDependencies()) {
            PlanProposal acceptedPlan = blackBoard.getAcceptedPlan(dependencyId);
            if (acceptedPlan != null) {
                startIndex = Math.max(startIndex, acceptedPlan.endIndex + 1);
            }
        }

        ArrayList<Action> actions = new ArrayList<>();

        //TODO: implement properly
        if(task.getId() == 1) {
            Action a = new Action(Action.Type.Push, Action.Dir.W, Action.Dir.W);
            boolean add = actions.add(a);
        } else { // when task.getId() is 2
            Action a = new Action(Action.Type.Push, Action.Dir.E, Action.Dir.E);
            Action a2 = new Action(Action.Type.Push, Action.Dir.E, Action.Dir.E);
            actions.add(a);
            actions.add(a2);
        }

        int endIndex = startIndex + actions.size() - 1;
        PlanProposal pp = new PlanProposal(actions, this, task.getId(), startIndex, endIndex);
        blackBoard.messagesToBlackboard.add(pp);
        workingOnPlan = false;
    }

    private void proposeHeuristic(int h, Task t) {
        blackBoard.messagesToBlackboard.add(new HeuristicProposal(h,this, t.getId()));
    }

    private int calculateHeuristic(Task task) {
        //TODO: update function with actual heuristic calculation
        Random rand = new Random();
        return rand.nextInt((10 - 1) + 1) + 1; //Random number between 1 and 10
    }

    @Override
    public void onError(Throwable e) {
        System.err.println("Some error happened in agent " + agentNumber + " subscription:");
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        // System.out.println("All Processing Done");
    }

    //GETTERS
    public int getAgentNumber() {
        return agentNumber;
    }

    public Color getColor() {
        return color;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean getWorking() { return workingOnPlan; }
    //END GETTERS


    //SETTERS
    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setWorking(boolean working) { this.workingOnPlan = working; }
    //END SETTERS

}
