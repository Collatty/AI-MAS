import Components.Agent;
import Components.BlackBoard;
import Components.Color;
import Components.State.oldState;
import Utilities.LevelReader;
import Components.Task;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Client {

    public Client(BufferedReader serverMessages) {
        System.out.println();

    }

    public static void main(String[] args) throws IOException{
        /*

        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Ballefrans"); //CLIENTNAME - INITATING SERVER COMMUNICATION


        //READING IN LEVEL INFORMATION FROM SERVER
        LevelReader.stringCreator(LevelReader.readAllLines(serverMessages));
        try {
            oldState initialOldState = new oldState(LevelReader.getInitial(), LevelReader.getGoals());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Move(W);Move(E)");
        String response = serverMessages.readLine();
        System.err.println("Serverresponse is: " + response);
        */


        //TODO: Get subGoals from Planner
        ArrayList<Task> tasks = new ArrayList<>();

        Task t1 = new Task(4,1, Color.RED, new ArrayList<>());
        Task t2 = new Task(1,3, Color.RED, new ArrayList<>());
        tasks.add(t1);
        tasks.add(t2);

        BlackBoard blackboard = new BlackBoard(tasks);

        Agent a0 = new Agent(0, Color.RED, 1, 1, blackboard);
        Agent a1 = new Agent(1, Color.RED, 4, 3, blackboard);
        Agent a2 = new Agent(2, Color.RED, 2, 3, blackboard);
        Agent a3 = new Agent(3, Color.RED, 3, 2, blackboard);
        Agent a4 = new Agent(4, Color.RED, 7, 1, blackboard);
        Agent a5 = new Agent(5, Color.RED, 6, 3, blackboard);

        ArrayList<Agent> agents = new ArrayList<>();
        agents.add(a0);
        agents.add(a1);
        agents.add(a2);
        agents.add(a3);
        agents.add(a4);
        agents.add(a5);

        blackboard.setAgents(agents);
        blackboard.start();


        //Client client = new Client(serverMessages);

        //TODO main method for running client program
    }
}
