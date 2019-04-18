package Components;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SubmissionPublisher;

public class BlackBoard implements Runnable {
    private Thread t;
    HashMap<Long, Task> todoMap = new HashMap<>();
    HashMap<Long, ArrayList<HeuristicProposal>> heuristicProposalMap = new HashMap<>();
    HashMap<Integer, ArrayList<Action>> planProposalsMap = new HashMap<>();
    HashMap<Color, Integer> colorAgentAmountMap = new HashMap<>();
    ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();
    SubmissionPublisher<MessageToAgent> publisher = new SubmissionPublisher<>();
    List<Task> tasks;
    ArrayList<Agent> agents;
    long taskCounter; // TODO: If more tasks than long can hold, problems can occur. Fix that

    public BlackBoard(List<Task> tasks) {
        taskCounter = 0;
        this.tasks = tasks;
    }

    @Override
    public void run() {
        Message nextMessage;

        while (true) {
            //TODO: Consider smarter solution than "sleep"
            while ((nextMessage = messagesToBlackboard.poll()) != null) {
                String messageType = nextMessage.getClass().getSimpleName();
                
                // Heuristic proposal received
                if (messageType == HeuristicProposal.class.getSimpleName()) {
                    HeuristicProposal hp = (HeuristicProposal) nextMessage;
                    System.err.println("Agent " + hp.a.getAgentNumber() + " propose " + hp.h + " for task " + hp.taskID);
                    ArrayList<HeuristicProposal> hpArray = new ArrayList<>();
                    if (heuristicProposalMap.containsKey(hp.taskID)) {
                        hpArray = heuristicProposalMap.get(hp.taskID);
                    }
                    hpArray.add(hp);
                    heuristicProposalMap.put(hp.taskID, hpArray);

                    //TODO: subtract the number of unavailable agents
                    if (colorAgentAmountMap.get(hp.a.getColor()) == hpArray.size()) {
                        delegateTask(hpArray);
                    }
                } else if (messageType == PlanProposal.class.getSimpleName()) {
                    PlanProposal pp = (PlanProposal) nextMessage;

                    ArrayList<Action> currentActions = planProposalsMap.get(pp.a.getAgentNumber());
                    ArrayList<Action> newActions = pp.actions;
                    ArrayList<Action> allActions = new ArrayList<>();

                    allActions.addAll(currentActions);
                    allActions.addAll(newActions);
                    System.err.println("Agent " + pp.a.getAgentNumber() + " propose action list " + allActions.toString());
                    planProposalsMap.put(pp.a.getAgentNumber(), pp.actions);
                }
            }
            try {
                Thread.sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Assign task to not-working agent with best heuristic
    private void delegateTask(ArrayList<HeuristicProposal> hpArray) {        
    	Collections.sort(hpArray, (hp1, hp2) -> hp1.h - hp2.h);
        for (HeuristicProposal hp : hpArray) {
            // hp.print();
            if (!hp.a.getWorking()) {
                MessageToAgent messageToAgent = new MessageToAgent(null, null, hp.a.getAgentNumber(),
                        MessageType.PLAN, todoMap.get(hp.taskID));
                todoMap.remove(hp.taskID);
                publisher.submit(messageToAgent);
                break;
            }
        }
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;
        calculateColorAgentAmountMap(agents);

        for(Agent a : agents) {
            publisher.subscribe(a);
            ArrayList<Action> actions = new ArrayList<>();
            planProposalsMap.put(a.getAgentNumber(), actions);
        }

        for(Task t : tasks) {
            if (t.getDependencies().isEmpty()) {
                t.setId(taskCounter);
                taskCounter++;
                todoMap.put(t.getId(), t);
                // System.err.println("Blackboard submits task with id " + t.id);
                MessageToAgent messageToAgent = new MessageToAgent(null, t.getColor(), null, MessageType.HEURISTIC, t);
                publisher.submit(messageToAgent);
                // tasks.remove(t); TODO: This doesn't work. Find another way.
            }
        }
    }

    //TODO: Make nicer
    public void calculateColorAgentAmountMap(ArrayList<Agent> agents) {
        for(Agent a : agents) {
            if(!colorAgentAmountMap.containsKey(a.getColor())) {
                colorAgentAmountMap.put(a.getColor(), 1);
            } else {
                colorAgentAmountMap.put(a.getColor(), colorAgentAmountMap.get(a.getColor()) + 1);
            }
        }
    }

    public void start() {
    	if (t == null) {
    		t = new Thread(this);
    		t.start();
    	}
    }
}

