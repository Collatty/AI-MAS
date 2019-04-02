import Components.BlackBoard;
import Components.State;
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

        LevelReader levelReader = new LevelReader(serverMessages);
        try {
            State initialState = new State(levelReader.initial, levelReader.goal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Move(W);Move(E)");
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
