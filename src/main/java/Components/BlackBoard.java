package Components;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SubmissionPublisher;


public class BlackBoard implements Runnable  {

    private Thread t;
    HashSet<Task> todo = new HashSet<>();
    HashSet<HeuristicProposal> heuristicProposal = new HashSet<>();
    HashSet<Plan> planProposals = new HashSet<>();
    ArrayList<HashSet<Message>> agentChannels = new ArrayList<>();
    ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();
    ArrayList<Task> tasks;
    ArrayList<Agent> agents;
    SubmissionPublisher<Task> publisher = new SubmissionPublisher<>();
    long taskCounter; // TODO: If more tasks than long can hold, problems can occur

    public BlackBoard(ArrayList<Task> tasks){
        taskCounter = 0;
        this.tasks = tasks;
    }

    @Override
    public void run() {

        Message nextMessage;

        while(true) {
            //TODO: Consider not to "sleep"
            while ((nextMessage = messagesToBlackboard.poll()) != null) {
                System.out.println("Removed: " + nextMessage.toString());
                //TODO: Handle message



            }
            try {
                Thread.currentThread().sleep(100);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void start () {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    public void setAgents(ArrayList<Agent> agents) {
        this.agents = agents;

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
}
