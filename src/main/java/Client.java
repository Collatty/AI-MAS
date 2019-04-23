import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import AI.Heuristic;
import Components.*;
import Components.State.State;
import Utilities.LevelReader;

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
            //oldState initialOldState = new oldState(LevelReader.getInitial(), LevelReader.getGoals());

            State state = new State();

            Heuristic heuristic = new Heuristic() {
                @Override
                public int compare(State o1, State o2) {
                    return 0;
                }
            };

            heuristic.h(state);

            System.err.println("Heuristic: " + heuristic.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Move(W);Move(E)");
        String response = serverMessages.readLine();
        System.err.println("Serverresponse is: " + response);


        */
        ArrayList<Agent> agents = new ArrayList<>();
        ArrayList<Task> tasks = new ArrayList<>();

        // HARDCODED TEST
        Task t1 = new Task(4,1, new ArrayList<>());
        Task t2 = new Task(1,3, new ArrayList<>());
        t1.setColor(Color.RED);
        t2.setColor(Color.BLUE);
        ArrayList<Long> dep = new ArrayList<Long>();
        dep.add(t1.getId());
        t2.setDependencies(dep);
        tasks.add(t1);
        tasks.add(t2);

        //BlackBoard blackboard = new BlackBoard(SubGoalPlanner.convertToTask());

        //TODO: should be the real initial state
        //State initState = new State();
        BlackBoard blackboard = new BlackBoard(tasks);

        // HARDCODED TEST
        Agent a0 = new Agent(0, Color.RED, 1, 1, blackboard);
        Agent a1 = new Agent(1, Color.BLUE, 4, 3, blackboard);
        //Agent a2 = new Agent(2, Color.RED, 2, 3, blackboard);
        //Agent a3 = new Agent(3, Color.RED, 3, 2, blackboard);
        //Agent a4 = new Agent(4, Color.RED, 7, 1, blackboard);
        //Agent a5 = new Agent(5, Color.RED, 6, 3, blackboard);

        agents.add(a0);
        agents.add(a1);
        //agents.add(a2);
        //agents.add(a3);
        //agents.add(a4);
        //agents.add(a5);

        blackboard.setAgents(agents);
        blackboard.start();


        //Client client = new Client(serverMessages);

        //TODO main method for running client program
    }
}
