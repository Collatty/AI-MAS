package levelTests;

import AI.BFS;
import AI.Heuristic;
import Components.BlackBoard;
import Components.State.State;
import Components.SubGoalPlanner;
import Utilities.LevelReader;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(JUnitParamsRunner.class)
public class MATest {
    private static Collection<Object[]> parametersForTestMA() {
        List<Object[]> params = new ArrayList<>();
        File dir = new File("levels");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if(child.toString().startsWith("levels/MA")){
                   params.add(new Object[] {child});
                }
            }
        } else {}
        return params;
    }

    @Test
    @Parameters
    public void testMA(File param) throws IOException{
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(param));
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