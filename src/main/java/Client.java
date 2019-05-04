import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;


import Components.Agent;
import Components.BlackBoard;
import Components.Color;
import Components.SubGoalPlanner;
import Components.Task;
import Components.State.Goal;
import Components.State.State;
import Utilities.LevelReader;

public class Client {

    public Client(BufferedReader serverMessages) {
        System.out.println();

    }


    public static void main(String[] args) throws IOException, ParseException {
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Ballefrans"); //CLIENTNAME - INITATING SERVER COMMUNICATION


        //READING IN LEVEL INFORMATION FROM SERVER
        LevelReader.stringCreator(LevelReader.readAllLines(serverMessages));
        try {
            //oldState initialOldState = new oldState(LevelReader.getInitial(), LevelReader.getGoals());
            State state = new State();
            SubGoalPlanner.serialize();
            BlackBoard bb = new BlackBoard(SubGoalPlanner.convertToTask());
            for (Task task : bb.getTasksNotSubmitted()) {
                System.err.println(task.toString());
            }
            //MEASURE RUN TIME OF HEURISTIC
            System.err.println("Heuristic w/ agent and goal:");
            Instant start = Instant.now();
            int heuristic = state.getAgents().get(0).getHeuristic(state, State.getGoals().get(0));
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            System.err.println("Heuristic: " + heuristic + "\t" + timeElapsed + "ms");




        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Move(W);Move(E)");
        //String response = serverMessages.readLine();
        //System.err.println("Serverresponse is: " + response);


    }
}
        //ArrayList<Agent> agents = new ArrayList<>();

        // HARDCODED TEST
//        Task t1 = new Task(4,1, Color.RED, new ArrayList<>());
//        Task t2 = new Task(1,3, Color.RED, new ArrayList<>());
//        tasks.add(t1);
//        tasks.add(t2);

        //BlackBoard blackboard = new BlackBoard(SubGoalPlanner.convertToTask());

        // HARDCODED TEST
//        Agent a0 = new Agent(0, Color.RED, 1, 1, blackboard);
//        Agent a1 = new Agent(1, Color.RED, 4, 3, blackboard);
//        Agent a2 = new Agent(2, Color.RED, 2, 3, blackboard);
//        Agent a3 = new Agent(3, Color.RED, 3, 2, blackboard);
//        Agent a4 = new Agent(4, Color.RED, 7, 1, blackboard);
//        Agent a5 = new Agent(5, Color.RED, 6, 3, blackboard);
//
//        agents.add(a0);
//        agents.add(a1);
//        agents.add(a2);
//        agents.add(a3);
//        agents.add(a4);
//        agents.add(a5);

        //blackboard.setAgents(agents);
        //blackboard.start();


        //Client client = new Client(serverMessages);
/*
    public static void main(String[] args) throws IOException, ParseException {
	BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
	System.out.println("Ballefrans"); // CLIENTNAME - INITATING SERVER COMMUNICATION

	// READING IN LEVEL INFORMATION FROM SERVER
	LevelReader.stringCreator(LevelReader.readAllLines(serverMessages));
	try {
	    // oldState initialOldState = new oldState(LevelReader.getInitial(),
	    // LevelReader.getGoals());
	    State state = new State();
	    BlackBoard bb = new BlackBoard(SubGoalPlanner.convertToTask());
	    Agent agt = new Agent(0, Color.GREEN, 3, 5, bb);
	    Goal goal = new Goal('B', 1, 5);

	    // MEASURE RUN TIME OF HEURISTIC
	    System.err.println("Heuristic w/ agent and goal:");
	    Instant start = Instant.now();
	    int heuristic = agt.getHeuristic(state, goal);
	    Instant finish = Instant.now();
	    long timeElapsed = Duration.between(start, finish).toMillis();
	    System.err.println("Heuristic: " + heuristic + "\t" + timeElapsed + "ms");
	    // BFS bfs = new BFS(new Point2D.Float(3,5));
	    // System.err.println(Heuristic.h_bfs(new Point2D.Float(1,5), new
	    // Point2D.Float(10,1)));
	    // System.err.println(apsp.toString());

	} catch (Exception e) {
	    e.printStackTrace();
	}
	// System.out.println("Move(W);Move(E)");
	String response = serverMessages.readLine();
	System.err.println("Serverresponse is: " + response);

	ArrayList<Agent> agents = new ArrayList<>();
	ArrayList<Task> tasks = new ArrayList<>();

	// HARDCODED TEST
	Task t1 = new Task(4, 1, new ArrayList<>());
	Task t2 = new Task(1, 3, new ArrayList<>());
	Task t3 = new Task(2, 3, new ArrayList<>());
	t1.setColor(Color.RED);
	t2.setColor(Color.BLUE);
	t3.setColor(Color.BLUE);
	ArrayList<Long> dep = new ArrayList<Long>();
	dep.add(t3.getId());
	t2.setDependencies(dep);
	tasks.add(t1);
	tasks.add(t2);
	tasks.add(t3);

	// TODO: should be the real initial state
//	State initState = new State();
//	BlackBoard blackboard = new BlackBoard(SubGoalPlanner.convertToTask(), initState);
	BlackBoard blackboard = new BlackBoard(tasks);

	// HARDCODED TEST
	Agent a0 = new Agent(0, Color.RED, 1, 1, blackboard);
	Agent a1 = new Agent(1, Color.BLUE, 4, 3, blackboard);
	Agent a2 = new Agent(2, Color.RED, 1, 2, blackboard);
	Agent a3 = new Agent(3, Color.BLUE, 3, 2, blackboard);
	// Agent a4 = new Agent(4, Color.RED, 7, 1, blackboard);
	// Agent a5 = new Agent(5, Color.RED, 6, 3, blackboard);

	agents.add(a0);
	agents.add(a1);
	agents.add(a2);
	agents.add(a3);
	// agents.add(a4);
	// agents.add(a5);

	blackboard.start(agents);

	// Client client = new Client(serverMessages);

	// TODO main method for running client program

    }
}*/
