package Components;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class BlackBoard implements Runnable {

    private Thread t;
    HashSet<Task> todo = new HashSet<>();
    HashSet<HeuristicProposal> heuristicProposal = new HashSet<>();
    HashSet<Plan> planProposals = new HashSet<>();
    ArrayList<HashSet<Message>> agentChannels = new ArrayList<>();
    ConcurrentLinkedQueue<Message> messagesToBlackboard = new ConcurrentLinkedQueue<>();
    ArrayList<Task> tasks;
    long taskCounter; // TODO: If more tasks than long can hold, problems can occur

    public BlackBoard(ArrayList<Task> tasks){
        taskCounter = 0;
        this.tasks = tasks;

        for(Task t : tasks){
            if(t.dependencies.isEmpty()){
                t.id = taskCounter;
                taskCounter++;
                todo.add(t);
                tasks.remove(t);
            }
        }
    }

    @Override
    public void run() {

        Message nextMessage;

        while(true) {
            //TODO: Consider not to "sleep"
            while ((nextMessage = messagesToBlackboard.poll()) != null) {
                System.out.println("Removed: " + nextMessage);
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
}
