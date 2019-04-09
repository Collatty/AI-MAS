import AI.Heuristic;
import Components.BlackBoard;
import Components.State.State;
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


        //TODO: Get subGoals from Planner
        ArrayList<Task> tasks = new ArrayList<>();

        BlackBoard blackboard = new BlackBoard(tasks);
        blackboard.start();

        //Client client = new Client(serverMessages);

        //TODO main method for running client program
    }
}
