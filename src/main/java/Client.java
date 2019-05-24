import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import AI.SubGoalPlanner;
import components.BlackBoard;
import utilities.LevelReader;

public class Client {

    public Client(BufferedReader serverMessages) {
	System.out.println();
    }

    public static void main(String[] args) throws IOException {
	BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
	System.out.println("deepurple"); // CLIENTNAME - INITATING SERVER COMMUNICATION
	// READING IN LEVEL INFORMATION FROM SERVER
	LevelReader.stringCreator(LevelReader.readAllLines(serverMessages));
	try {
	    SubGoalPlanner.serialize();
	    BlackBoard.getBlackBoard().setTasks(SubGoalPlanner.postToBlackBoard());
	    BlackBoard.getBlackBoard().run();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
