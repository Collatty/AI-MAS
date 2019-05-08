package levelTests;

import AI.BFS;
import AI.Heuristic;
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
        //setUpTest(file);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            LevelReader.stringCreator(LevelReader.readAllLines(bufferedReader));
            BlackBoard.setNewBlackboard();
            State.loadNewState();
            BFS.loadNewLevel();
            Heuristic.loadNewLevel();
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
        setUpTest(file);
    }

    @Test
    public void testMAExample3() throws IOException{
        File file = new File("levels/MAExample3.lvl");
        setUpTest(file);
    }

    @Test
    public void testMAAiAiCap() throws IOException{
        File file = new File("levels/MAAiAiCap.lvl");
        //tUpTest(file);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            LevelReader.stringCreator(LevelReader.readAllLines(bufferedReader));
            BlackBoard.setNewBlackboard();
            State.loadNewState();
            BFS.loadNewLevel();
            Heuristic.loadNewLevel();
            SubGoalPlanner.serialize();
            BlackBoard.getBlackBoard().setTasks(SubGoalPlanner.postToBlackBoard());
            BlackBoard.getBlackBoard().run();
            assertTrue(State.isSolved());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }


    private void setUpTest(File file) throws IOException{
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            LevelReader.stringCreator(LevelReader.readAllLines(bufferedReader));
            BlackBoard.setNewBlackboard();
            State.loadNewState();
            BFS.loadNewLevel();
            Heuristic.loadNewLevel();
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

