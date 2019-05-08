package levelTests;

import Components.BlackBoard;
import Components.State.State;
import Components.SubGoalPlanner;
import Utilities.LevelReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class MultiAgentTest {

    @Test
    public void testMAExample() throws IOException{
        File file = new File("levels/MAExample.lvl");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            LevelReader.stringCreator(LevelReader.readAllLines(bufferedReader));
            BlackBoard.setNewBlackboard();
            State.loadNewState();
            SubGoalPlanner.serialize();
            BlackBoard.getBlackBoard().setTasks(SubGoalPlanner.postToBlackBoard());
            BlackBoard.getBlackBoard().run();
            assertTrue(State.isSolved());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();

        }

    }

    @Test
    public void testMAExample2() throws IOException{
        File file = new File("levels/MAExample2.lvl");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            LevelReader.stringCreator(LevelReader.readAllLines(bufferedReader));
            BlackBoard.setNewBlackboard();
            State.loadNewState();
            SubGoalPlanner.serialize();
            BlackBoard.getBlackBoard().setTasks(SubGoalPlanner.postToBlackBoard());
            BlackBoard.getBlackBoard().run();
            assertTrue(State.isSolved());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();

        }

    }


}

