package components;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

import components.state.Block;
import components.state.Goal;
import components.state.State;
import components.state.Tile;

public class BlackBoard extends SubmissionPublisher<MessageToAgent>{

    private static BlackBoard blackBoard = new BlackBoard();
    private List<Task> unsolvedTasks = new ArrayList<>();
    private Map<Long, List<HeuristicProposal>> heuristicProposalMap = new HashMap<>();
    private Map<Color, Integer> colorAgentAmountMap = new HashMap<>();
    private Map<Long, Task> taskMap = new HashMap<>();
    private Map<Integer, Integer> heuristicPenaltyMap = new HashMap<>();
    private List<State> states = new ArrayList<>();
    private List<List<Action>> acceptedPlans = new ArrayList<>();
    private ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();

    public List<String> getOutputStrings() {
        return outputStrings;
    }

    private List<String> outputStrings= new ArrayList<>();

    private BlackBoard() {
    }

    //Testing purposes
    public static void setNewBlackboard() {
        blackBoard = new BlackBoard();
    }


    public void setTasks(List<Task> tasks) {
        this.unsolvedTasks = tasks;
        this.taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
    }

    private void populateAcceptedPlans(){
        for (int i = 0; i<State.getInitialAgents().size(); i++) {
            acceptedPlans.add(new CopyOnWriteArrayList<>());
        }
    }

    public void run() {
        this.start(State.getInitialAgents());
	    Message nextMessage;

            while (!State.isSolved()) {
                while ((nextMessage = messagesToBlackboard.poll()) != null && !State.isSolved()) {//TODO check why plans
                    // come in after finished
                    String messageType = nextMessage.getClass().getSimpleName();
                    if (messageType.equals(HeuristicProposal.class.getSimpleName())) {
                        HeuristicProposal hp = (HeuristicProposal) nextMessage;
                        System.err
                                .println("Agent " + hp.getA().getAgentNumber() + " proposes " + hp.getH() + " actions for " +
                                        "task " + hp.getTaskID());
                        List<HeuristicProposal> hpArray = new ArrayList<>();
                        if (heuristicProposalMap.containsKey(hp.getTaskID())) {
                            hpArray = heuristicProposalMap.get(hp.getTaskID());
                        }
                        HeuristicProposal oldHeuristicProposal = null;
                        for (HeuristicProposal heuristicProposal : hpArray) {
                            if (heuristicProposal.getA() == hp.getA()) {
                                oldHeuristicProposal = heuristicProposal;
                            }
                        }
                        hpArray.remove(oldHeuristicProposal);
                        hpArray.add(hp);
                        heuristicProposalMap.put(hp.getTaskID(), hpArray);

                        // Check if all agents of a given color have send a heuristic for this task
                        // TODO: reset hparray
                        //  once task is resubmitted
                        if (colorAgentAmountMap.get(hp.getA().getColor()) == hpArray.size()) {
                            delegateTask(hpArray);
                        }
                    } else if (messageType.equals(PlanProposal.class.getSimpleName())) {
                        PlanProposal pp = (PlanProposal) nextMessage;
                        System.err.println(pp.toString());
                        Agent agent = agentInTheWay(pp.getActions());
                        Block block = blockInTheWay(pp.getActions());
                        System.err.println("91: Conflict 2nd input " + this.acceptedPlans.get(pp.getAgent().getAgentNumber()).size());
                        if (conflict(pp.getActions(), this.acceptedPlans.get(pp.getAgent().getAgentNumber()).size())) {
                            if (agent != null && !agent.equals(pp.getAgent())) {
                                System.err.println("Agent" + agent.toString() + " is in the way!");
                                Task task = new Task.MoveAgentTask(agent.getColor(), new ArrayList<>(), pp.getActions());
                                this.taskMap.get(pp.getTask().getId()).getDependencies().add(task.getId());
                                this.taskMap.put(task.getId(), task);
                                this.submit(new MessageToAgent(false, agent.getColor(), agent.getAgentNumber(),
                                        MessageType.PLAN, task));
                            } else if (block != null && pp.getTask().getBlock() != null && !pp.getTask().getBlock().equals(block)) {
                                System.err.println("Block" + block.toString() + "is in the way");
                                Task task = new Task.MoveBlockTask(block.getColor(), new ArrayList<>(), pp.getActions(), block);
                                this.taskMap.get(pp.getTask().getId()).getDependencies().add(task.getId());
                                this.taskMap.put(task.getId(), task);
                                this.submit(new MessageToAgent(false, block.getColor(), null,
                                        MessageType.PLAN, task));
                            } else {
                                pp.getAgent().replan();
                            }
                        } else {
                                if (acceptedPlans.get(pp.getAgent().getAgentNumber()).size() == 0) {
                                    acceptedPlans.remove(pp.getAgent().getAgentNumber());
                                    acceptedPlans.add(pp.getAgent().getAgentNumber(), pp.getActions());
                                    this.taskMap.get(pp.getTask().getId()).setSolved(true);
                                    this.unsolvedTasks.remove(this.taskMap.get(pp.getTask().getId()));
                                    executePlan(pp, 0);
                                    submitTasks();
                                } else {
                                    acceptedPlans.get(pp.getAgent().getAgentNumber()).addAll(pp.getActions());
                                    this.taskMap.get(pp.getTask().getId()).setSolved(true);
                                    this.unsolvedTasks.remove(this.taskMap.get(pp.getTask().getId()));
                                    executePlan(pp,
                                            acceptedPlans.get(pp.getAgent().getAgentNumber()).size() - pp.getActions().size());
                                    submitTasks();
                                }

                        }
                    }
                    checkCompleted();
                }
            }
        appendNoOpAction();
        stringBuilder();
        for (String string : this.outputStrings){
            System.out.println(string);

        }
    }

    // Delegate task to agents
    private void delegateTask(List<HeuristicProposal> hpArray) {
        HeuristicProposal hpChosen = null;
        Long minEndIndex = null;
        Long endIndex = null;
        long startIndex = 0;
        long maxDependencyEndIndex = 0;
        long agentPenalty;


        // Find best heuristic proposed
       for (HeuristicProposal hp : hpArray) {
           int currentPenalty = heuristicPenaltyMap.get(hp.getA().getAgentNumber());
           if(hpChosen == null || (currentPenalty + hp.getH().getHeuristic() < hpChosen.getH().getHeuristic())){
               hpChosen = hp;
           }
           /*agentPenalty = heuristicsActionsMap.get(hp.getA().getAgentNumber()).size();
           startIndex = Math.max(agentPenalty, maxDependencyEndIndex);
           endIndex = startIndex + hp.getH().heuristic;

           if (minEndIndex == null || minEndIndex > endIndex) {
               minEndIndex = endIndex;*/

        }

        System.err.print("Blackboard has chosen a heuristic proposal: ");
        System.err.print("given by agent " + hpChosen.getA().getAgentNumber());
        heuristicPenaltyMap.put(hpChosen.getA().getAgentNumber(), hpChosen.getH().getHeuristic() +
                heuristicPenaltyMap.get(hpChosen.getA().getAgentNumber()));
        this.submit(new MessageToAgent(false, Color.NAC, hpChosen.getA().getAgentNumber(),
                MessageType.PLAN, this.taskMap.get(hpChosen.getTaskID())));
    }
    private void submitTasks() {

        for (Task task : this.unsolvedTasks) {
            if (task.getDependencies().size() == 0) {
                if (task instanceof Task.MoveAgentTask) {
                    this.submit(new MessageToAgent(false, task.getColor(), null,
                            MessageType.PLAN, task));
                } else {
                    this.submitHeuristicTask(task);
                }
            } else {
                boolean solvedDeps = true;
                for (Long ID : task.getDependencies()) {
                    if (!this.taskMap.get(ID).isSolved()) {
                        solvedDeps = false;
                        break;
                    }
                }
                if (solvedDeps) {
                    if (task instanceof Task.MoveAgentTask) {
                        this.submit(new MessageToAgent(false, task.getColor(), null,
                                MessageType.PLAN, task));
                    } else {
                        this.submitHeuristicTask(task);
                    }
                }
            }
        }
    }

    private void submitHeuristicTask(Task task) {
        System.err.println("Blackboard submits task with id " + task.getId() + " and color " + task.getColor());
        MessageToAgent messageToAgent = new MessageToAgent(null, task.getColor(), null, MessageType.HEURISTIC, task);
        this.submit(messageToAgent);
    }


    private void start(List<Agent> agents) {
        populateAcceptedPlans();
        this.states.add(State.getState());
        // Setup agents
        for (Agent a : agents) {
            //Thread thread = new Thread(a);
            //thread.start();
            heuristicPenaltyMap.put(a.getAgentNumber(), 0);
            if (!colorAgentAmountMap.containsKey(a.getColor())) {
                colorAgentAmountMap.put(a.getColor(), 1);
            } else {
                colorAgentAmountMap.put(a.getColor(), colorAgentAmountMap.get(a.getColor()) + 1);
            }
            this.subscribe(a);
        }
        submitTasks();

    }

    private Agent agentInTheWay(List<Action> actions) {
        Map<Tile, Agent> occupiedTiles = new HashMap();
        for (List<Action> acceptedPLan : acceptedPlans) {
            Agent agent = State.getAgentByNumber(acceptedPlans.indexOf(acceptedPLan));
            if (acceptedPLan.size() == 0) {
                occupiedTiles.put(State.getInitialState().get(agent.getRow()).get(agent.getCol()), agent);
            } else {
                occupiedTiles
                        .put(State.getInitialState()
                        .get(acceptedPLan.get(acceptedPLan.size()-1).getEndAgent().getRow())
                        .get(acceptedPLan.get(acceptedPLan.size()-1).getEndAgent().getCol()), agent);
            }
        }
        for (Action action : actions) {
            if (occupiedTiles.keySet().contains(action.getEndBox())){
                return occupiedTiles.get(action.getEndBox());
            } else if (occupiedTiles.keySet().contains(action.getEndAgent())) {
                return occupiedTiles.get(action.getEndAgent());

            }
        }
        return null;
    }

    private Block blockInTheWay(List<Action> actions) {
        Map<Tile, Block> occupiedTiles = new HashMap();
        for (Block block : State.getBlocks()) {
            occupiedTiles.put(State.getInitialState().get(block.getRow()).get(block.getCol()), block);
        }
        for (Action action : actions) {
            if (occupiedTiles.keySet().contains(action.getEndBox())){
                return occupiedTiles.get(action.getEndBox());
            } else if (occupiedTiles.keySet().contains(action.getEndAgent())) {
                return occupiedTiles.get(action.getEndAgent());
            }
        }
        return null;
    }

    private void appendNoOpAction() {
        int maxSize = 0;
        for (List<Action> acceptedPlan : this.acceptedPlans) {
            if (acceptedPlan.size() > maxSize) {
                maxSize = acceptedPlan.size();
            }

        }
        for (List<Action> acceptedPlan : this.acceptedPlans) {
            if (acceptedPlan.size() < maxSize) {
                List<Action> noOps = new ArrayList<>();
                for (int i = 0; i < maxSize - acceptedPlan.size(); i++) {
                    if(acceptedPlan.isEmpty()) {
                        noOps.add(new Action(
                                State.getInitialState().get(
                                State.getInitialAgents().get(this.acceptedPlans.indexOf(acceptedPlan)).getRow())
                                .get(State.getInitialAgents().get(this.acceptedPlans.indexOf(acceptedPlan)).getCol())));
                    } else {
                        noOps.add(new Action(acceptedPlan.get(acceptedPlan.size() - 1).getEndAgent()));
                    }
                }
                acceptedPlan.addAll(noOps);
            }


        }
    }

//

    public ConcurrentLinkedQueue<Message> getMessagesToBlackboard() {
        return messagesToBlackboard;
    }

    public static  BlackBoard getBlackBoard() {
        return blackBoard;
    }

    public String toString() {
        return "Blackboard";
    }

    private void checkCompleted() {
        for (Goal goal : State.getGoals()) {
            if (!goal.isCompleted()) {
                return;
            }
        }
        State.setSolved(true);
    }

    private void executePlan(PlanProposal acceptedPlan, int indexFrom) {
        int difference = indexFrom+acceptedPlan.getActions().size()+1 - this.states.size();
        if(difference>0) {
            for (int i = 0; i < difference; i++) {
                this.states.add(new State(this.states.get(this.states.size() - 1)));
            }
        }
        for(Action action : acceptedPlan.getActions()) {
            for (int i = indexFrom + acceptedPlan.getActions().indexOf(action)+1; i<indexFrom + acceptedPlan.getActions().size()+1; i++) {
                boolean firstTimeMoveIsMade = i == indexFrom + acceptedPlan.getActions().indexOf(action)+1;
                this.states.get(i).makeMove(action, firstTimeMoveIsMade);
        }

        /*for (Action action : acceptedPlan.getActions()){
            for (int i = indexFrom + acceptedPlan.getActions().indexOf(action)+1; i<indexFrom + acceptedPlan.getActions().size()+1; i++) {
                try {
                    boolean firstTimeMoveIsMade = i == indexFrom + acceptedPlan.getActions().indexOf(action)+1;
                    this.states.get(i).makeMove(action, firstTimeMoveIsMade);
                } catch (IndexOutOfBoundsException e) {
                    this.states.add(new state(this.states.get(i-1)));
                    break;
                }
            }*/
        }

        acceptedPlan.getAgent().executePlan();

    }
    private boolean conflict(List<Action> actions, int indexFrom) {
        State prevState = null;
        List<Action> performedActions = new ArrayList<>();
        for (Action action : actions){
            try {
                if (!this.states.get(indexFrom + actions.indexOf(action)+1).isLegalMove(action)) {
                    return true;
                } else {
                    performedActions.add(action);
                }
            } catch (IndexOutOfBoundsException e) {
                    if (prevState == null) {
                        prevState = new State(this.states.get(indexFrom + actions.indexOf(action)));
                        if (!performedActions.isEmpty()) {
                            for (Action action1 : performedActions) {
                                prevState.makeMove(action1, true);
                                prevState = new State(prevState);
                            }
                        }
                    }
                if (!prevState.isLegalMove(action)) {
                    return true;
                } else {
                    prevState.makeMove(action, true);
                    prevState = new State(prevState);
                }
            }
        }
        return false;

      /*  int counter = 0;
	    for (Action action : actions) {
	        Collection<Tile> suggestedTiles = new HashSet<>();
	        suggestedTiles.add(action.getStartAgent());
	        suggestedTiles.add(action.getEndAgent());
	        suggestedTiles.add(action.getStartBox());
	        suggestedTiles.add(action.getEndBox());
	        for (List<Action> acceptedPlan : acceptedPlans) {
	            if (acceptedPlan != null && acceptedPlan.size() > 0 && counter < acceptedPlan.size()) {
                    Collection<Tile> usedTiles = new HashSet<>();
                    usedTiles.add(acceptedPlan.get(counter).getEndBox());
                    usedTiles.add(acceptedPlan.get(counter).getStartBox());
                    usedTiles.add(acceptedPlan.get(counter).getStartAgent());
                    usedTiles.add(acceptedPlan.get(counter).getEndAgent());
                    for (Tile tile : suggestedTiles) {
                        if (tile != null && usedTiles.contains(tile)) {
                            return true;
                        }
                    }
                }

            }
	    counter++;
        }
	return false;*/
    }

    public void stringBuilder() {
        for (int i = 0; i<acceptedPlans.get(0).size(); i++){
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j< State.getInitialAgents().size(); j++) {
                stringBuilder.append(acceptedPlans.get(j).get(i));
                stringBuilder.append(";");
            }
            this.outputStrings.add(stringBuilder.substring(0, stringBuilder.toString().length()-1));
        }


    }

}
