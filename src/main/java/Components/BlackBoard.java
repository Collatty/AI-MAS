package Components;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SubmissionPublisher;

//TODO test if BlackBoard should implement SubmissionPublisher
public class BlackBoard implements Runnable  {

    private Thread t;
    HashSet<Task> todo = new HashSet<>();
    HashMap<Long, ArrayList<HeuristicProposal>> heuristicProposalMap = new HashMap<>();
    HashSet<Plan> planProposals = new HashSet<>();
    ArrayList<HashSet<Message>> agentChannels = new ArrayList<>();
    ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();
    ArrayList<Task> tasks;
    ArrayList<Agent> agents;
    SubmissionPublisher<Task> publisher = new SubmissionPublisher<>();
    long taskCounter; // TODO: If more tasks than long can hold, problems can occur
    HashMap<Color, Integer> colorAgentAmountMap = new HashMap<>();

    public BlackBoard(ArrayList<Task> tasks){
        taskCounter = 0;
        this.tasks = tasks;
    }

    @Override
    public void run() {

        Message nextMessage;

        while(true) {
            //TODO: Consider not to "sleep"
            //TODO: Handle message
            while ((nextMessage = messagesToBlackboard.poll()) != null) {

                String messageType = nextMessage.getClass().getSimpleName();

                if (messageType == HeuristicProposal.class.getSimpleName()){
                    HeuristicProposal hp = (HeuristicProposal) nextMessage;
                    System.out.println("Agent " + hp.a.getAgentNumber() + " propose " + hp.h + " for task " + hp.taskID);
                    ArrayList<HeuristicProposal> hpArray = new ArrayList<>();
                    if(heuristicProposalMap.containsKey(hp.taskID)){
                        hpArray = heuristicProposalMap.get(hp.taskID);
                    }
                    hpArray.add(hp);
                    heuristicProposalMap.put(hp.taskID, hpArray);

                    if(colorAgentAmountMap.get(hp.a.getColor())==hpArray.size()){
                        delegateTask(hpArray);
                    }
                }


            }
            try {
                Thread.currentThread().sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void delegateTask(ArrayList<HeuristicProposal> hpArray) {
        //hpArray.sort(Comparator.comparingInt(HeuristicProposal::getH));

        Collections.sort(hpArray, (hp1, hp2) -> hp1.h - hp2.h);

        for(HeuristicProposal hp : hpArray){
            hp.print();
            if(!hp.a.getWorking()){

            }
        }

        //HeuristicProposal bestHeuristicProposal =  Collections.min(hpArray, Comparator.comparing(hp -> hp.h));
        //System.out.println("Agent " + bestHeuristicProposal.a.getAgentNumber() + " had the best h with value "
        //+ bestHeuristicProposal.h + " for task " + bestHeuristicProposal.taskID);
    }

    public void start () {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;

        calculateColorAgentAmountMap(agents);

        for(Agent a : agents){
            publisher.subscribe(a);
        }

        for(Task t : tasks){
            if(t.dependencies.isEmpty()){
                t.id = taskCounter;
                taskCounter++;
                todo.add(t);
                System.out.println("Blackboard submits task with id " + t.id);
                publisher.submit(t);
                //tasks.remove(t);
            }
        }
    }

    //TODO: make nicer
    public void calculateColorAgentAmountMap(ArrayList<Agent> agents){
        for(Agent a : agents){
            if(!colorAgentAmountMap.containsKey(a.getColor())){
                colorAgentAmountMap.put(a.getColor(),1);
            } else {
                colorAgentAmountMap.put(a.getColor(), colorAgentAmountMap.get(a.getColor())+1);
            }
        }
    }
}
