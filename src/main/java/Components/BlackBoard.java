package Components;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

import Components.State.Goal;
import Components.State.State;
import Components.State.Tile;

public class BlackBoard extends SubmissionPublisher<MessageToAgent>{

    private static BlackBoard blackBoard = new BlackBoard();
    private Thread t;
    private List<Task> tasksReadyForSubmit = new ArrayList<>();
    private List<Task> tasksNotSubmitted = new ArrayList<>();
    private Map<Long, List<HeuristicProposal>> heuristicProposalMap = new HashMap<>();
    private Map<Integer, List<Action>> acceptedActionsMap = new HashMap<>();
    private Map<Integer, ArrayList<Action>> heuristicsActionsMap = new HashMap<>();
    private Map<Color, Integer> colorAgentAmountMap = new HashMap<>();
    private Map<Long, PlanProposal> delegatedTasksMap = new HashMap<>();
    private Map<Long, Task> taskMap = new HashMap<>();
    private List<State> stateSequence = new ArrayList<>();
    private Map<Long, PlanProposal> acceptedPlansMap = new HashMap<>();

    public List<List<Action>> getAcceptedPlans() {
        return acceptedPlans;
    }

    private List<List<Action>> acceptedPlans = new ArrayList<>(10);
    private List<String> outputStrings = new ArrayList<>();

    private State currentState;
    private ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();
    private List<Task> tasks;
    private List<Agent> agents;
    private long taskCounter;

    private BlackBoard() {
    }

    public void setTasks(List<Task> tasks) {
        this.tasksNotSubmitted = tasks;
        this.taskMap = tasks.stream().collect(Collectors.toMap(Task::getId, task -> task));
    }

    private void populateAcceptedPlans(){
        for (int i = 0; i<State.getState().getAgents().size(); i++) {
            acceptedPlans.add(new ArrayList<>());
        }
    }

    public void run() {
        this.start(State.getState().getAgents());
	    Message nextMessage;

        while (!State.isSolved()) {
            while ((nextMessage = messagesToBlackboard.poll()) != null) {
            String messageType = nextMessage.getClass().getSimpleName();
            // Heuristic proposal received
                if (messageType == HeuristicProposal.class.getSimpleName()) {
                    HeuristicProposal hp = (HeuristicProposal) nextMessage;
                    System.err
                        .println("Agent " + hp.getA().getAgentNumber() + " proposes " + hp.getH() + " actions for " +
                                "task " + hp.getTaskID());
                    List<HeuristicProposal> hpArray = new ArrayList<>();
                    if (heuristicProposalMap.containsKey(hp.getTaskID())) {
                        hpArray = heuristicProposalMap.get(hp.getTaskID());
                        }
                    hpArray.add(hp);
                    heuristicProposalMap.put(hp.getTaskID(), hpArray);

                    // Check if all agents of a given color have send a heuristic for this task
                    if (colorAgentAmountMap.get(hp.getA().getColor()) == hpArray.size()) {
                        delegateTask(hpArray);
                        if (!tasksReadyForSubmit.isEmpty()) {
                            submitTask(tasksReadyForSubmit.remove(0));
                        } else {
                            System.err.print("All tasks are delegated, time for planning!");
                            // TODO: All tasks are delegated, make the agents start planning
                        }
                    }
                } else if (messageType == PlanProposal.class.getSimpleName()) {
                    PlanProposal pp = (PlanProposal) nextMessage;
                    if(conflict(pp.getActions())){
                        pp.getA().replan();
                    } else {
                       if (acceptedPlans.get(pp.getA().getAgentNumber()).size() == 0) {
                           acceptedPlans.remove(pp.getA().getAgentNumber());
                           acceptedPlans.add(pp.getA().getAgentNumber(), pp.getActions());
                       } else {
                           acceptedPlans.get(pp.getA().getAgentNumber()).addAll(pp.getActions());
                       }
                    }



                    // If there is no conflict, input the new actions in the plan map
                    if (!conflict(pp.getActions())) {
                    // TODO: Update stateSequence

                        System.err.println(pp.toString());
                        // Add NoOps if necessary
                        int numberOfNoOps = (int) (pp.getStartIndex()
                            - acceptedActionsMap.get(pp.getA().getAgentNumber()).size());
                        if (numberOfNoOps >= 0) {
                            //Action[] noOpArr = new Action[numberOfNoOps];
                            //Arrays.fill(noOpArr, new Action()); //TODO
                            //acceptedActionsMap.get(pp.getA().getAgentNumber()).addAll(Arrays.asList(noOpArr));

                            // Add actions to accepted actions
                            //acceptedActionsMap.get(pp.getA().getAgentNumber()).addAll(pp.getActions());

                            // Add plan to accepted plans
                            //acceptedPlansMap.put(pp.getTaskID(), pp);


                            System.err.print("Current actions planned: ");
                            System.err.println(acceptedActionsMap.toString());
                        } else {
                            // TODO: Agent has actions where this plan starts. Deny plan
                        }
                    }
                }
                checkCompleted();

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println("Still here");
        }
        appendNoOpAction();
    }

    // Delegate task to agents
    private void delegateTask(List<HeuristicProposal> hpArray) {
        HeuristicProposal hpChosen = null;
        Long minEndIndex = null;
        Long endIndex = null;
        long startIndex = 0;
        long maxDependencyEndIndex = 0;
        long agentPenalty;

        // Find max endIndex of dependencies
        for (Long depId : taskMap.get(hpArray.get(0).getTaskID()).getDependencies()) {
            maxDependencyEndIndex = Math.max(maxDependencyEndIndex, delegatedTasksMap.get(depId).getEndIndex());
        }

        // Find best heuristic proposed
        for (HeuristicProposal hp : hpArray) {
            agentPenalty = heuristicsActionsMap.get(hp.getA().getAgentNumber()).size();
            startIndex = Math.max(agentPenalty, maxDependencyEndIndex);
            endIndex = startIndex + hp.getH().heuristic;

            if (minEndIndex == null || minEndIndex > endIndex) {
            minEndIndex = endIndex;
            hpChosen = hp;
            }
        }

        System.err.print("Blackboard has chosen a heuristic proposal: ");
        hpChosen.print();
        this.submit(new MessageToAgent(false, hpChosen.getA().getColor(), hpChosen.getA().getAgentNumber(),
                MessageType.PLAN, this.taskMap.get(hpChosen.getTaskID()), hpChosen.getH().block));

        // TODO: Update the currentState:
        // - Moving agent to new position near goal solved.
        // - Remove box used by agent.
        // - Change goal solved to wall.

        // Add NoOps to plan if necessary
        int numberOfNoOps = (int) (startIndex - heuristicsActionsMap.get(hpChosen.getA().getAgentNumber()).size());
        if (numberOfNoOps > 0) {
            //Action[] noOpArr = new Action[numberOfNoOps];
            //Arrays.fill(noOpArr, new Action()); //TODO
            //heuristicsActionsMap.get(hpChosen.getA().getAgentNumber()).addAll(Arrays.asList(noOpArr));
        }

        // Add NoOps instead of real actions
        //Action[] unknArr = new Action[10];
        //Arrays.fill(unknArr, new Action()); //TODO
        //ArrayList<Action> unknArrList = new ArrayList<>(Arrays.asList(unknArr));
        //heuristicsActionsMap.get(hpChosen.getA().getAgentNumber()).addAll(unknArrList);

        System.err.println("Actions planed with heuristics: ");
        for (Integer agentId : heuristicsActionsMap.keySet()) {
            System.err.println("Agent " + agentId + ": " + heuristicsActionsMap.get(agentId).toString());
        }

        // Save plan
        //delegatedTasksMap.put(hpChosen.getTaskID(),
        //    new PlanProposal(unknArrList, hpChosen.getA(), hpChosen.getTaskID(), startIndex, endIndex));

        // Find tasks with solved dependencies
        Iterator<Task> taskItr = tasksNotSubmitted.iterator();
        while (taskItr.hasNext()) {
            Task task = taskItr.next();
            System.err.println("Task " + task.getId() + " dependencies: " + task.getDependencies().toString());
            if (delegatedTasksMap.keySet().containsAll(task.getDependencies())) {
            System.err.println("All dependencies solved for task " + task.getId());
            tasksReadyForSubmit.add(task);
            //taskItr.remove(); this is still illegal
            }
        }
    }

    private void submitTask(Task task) {
        task.setPrestate(currentState);
        System.err.println("Blackboard submits task with id " + task.getId() + " and color " + task.getColor());
        MessageToAgent messageToAgent = new MessageToAgent(null, task.getColor(), null, MessageType.HEURISTIC, task,
                null);
        this.submit(messageToAgent);
    }

    private boolean conflict(List<Action> actions) {
        int counter = 0;
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
	return false;
    }

    private void start(List<Agent> agents) {
        // Setup agents
        for (Agent a : agents) {
            Thread thread = new Thread(a);
            thread.start();
            if (!colorAgentAmountMap.containsKey(a.getColor())) {
                colorAgentAmountMap.put(a.getColor(), 1);
            } else {
                colorAgentAmountMap.put(a.getColor(), colorAgentAmountMap.get(a.getColor()) + 1);
            }

            this.subscribe(a);
            ArrayList<Action> actions = new ArrayList<>();
            heuristicsActionsMap.put(a.getAgentNumber(), actions);
            acceptedActionsMap.put(a.getAgentNumber(), actions);
        }

        // Find tasks with no dependencies
        Iterator<Task> taskItr = tasksNotSubmitted.iterator();
        Collection<Task> submittedTasks = new ArrayList<>();
        while (taskItr.hasNext()) {
            Task task = taskItr.next();
            System.err.println("Task " + task.getId() + " has color " + task.getColor());
            if (task.getDependencies().isEmpty()) {
                System.err.println("Task " + task.getId() + " has no dependencies");
                tasksReadyForSubmit.add(task);
                submittedTasks.add(task);
            }
	    }
        tasksNotSubmitted.removeAll(submittedTasks);

        // Submit first task
        submitTask(tasksReadyForSubmit.remove(0));
        populateAcceptedPlans();
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
                    if(acceptedPlan.size()==0) {
                        noOps.add(new Action(
                                State.getInitialState().get(
                                State.getState().getAgents().get(this.acceptedPlans.indexOf(acceptedPlan)).getRow())
                                .get(State.getState().getAgents().get(this.acceptedPlans.indexOf(acceptedPlan)).getCol())));
                    }
                    noOps.add(new Action(acceptedPlan.get(acceptedPlan.size() - 1).getEndAgent()));
                }
                acceptedPlan.addAll(noOps);
            }


        }
    }


    public PlanProposal getAcceptedPlan(long taskID) {
	    return acceptedPlansMap.get(taskID);
    }

    public List<Task> getTasksNotSubmitted() {
        return tasksNotSubmitted;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public ConcurrentLinkedQueue<Message> getMessagesToBlackboard() {
        return messagesToBlackboard;
    }

    public static BlackBoard getBlackBoard() {
        return blackBoard;
    }

    public String toString() {
        return "Blackboard";
    }

    public void checkCompleted() {
        for (Goal goal : State.getGoals()) {
            if (!goal.isCompleted()) {
                return;
            }
        }
        State.setSolved(true);
    }


}
