import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.List;


public class LevelReader {



    public static String domain;
    public static String levelName;
    public static String colors;
    public static String initial;
    public static String goal;

    public static List<String> message;

    public static BufferedReader bufferedReader;

    public LevelReader() throws IOException{
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            message = readAllLines(reader);
        }

        stringCreator();

    }

    private void stringCreator() {
        StringBuilder stringBuilderDomain = new StringBuilder();
        StringBuilder stringBuilderLevelName = new StringBuilder();
        StringBuilder stringBuilderColors = new StringBuilder();
        StringBuilder stringBuilderInitial = new StringBuilder();
        StringBuilder stringBuilderGoal = new StringBuilder();
        StringBuilder throwAway = new StringBuilder();


        StringBuilder activeStringBuilder = throwAway;
       for (String line : message) {
           if (line.contains("#domain")) {
               activeStringBuilder = stringBuilderDomain;
               continue;
           }
           if (line.contains("#levelname")) {
               activeStringBuilder = stringBuilderLevelName;
               continue;
           }
           if (line.contains("#colors")) {
               activeStringBuilder = stringBuilderColors;
               continue;
           }
           if (line.contains("#initial")) {
               activeStringBuilder = stringBuilderInitial;
               continue;
           }
           if (line.contains("#goal")) {
               activeStringBuilder = stringBuilderGoal;
               continue;
           } if (line.contains("#end")) {
               continue;
           }

           activeStringBuilder.append(line);
           activeStringBuilder.append("\n");


       }

       domain = stringBuilderDomain.toString();
       levelName = stringBuilderLevelName.toString();
       initial = stringBuilderInitial.toString();
       goal = stringBuilderGoal.toString();
       colors = stringBuilderColors.toString();

    }

    public static List<String> readAllLines(BufferedReader reader) throws IOException {
        StringBuilder content = new StringBuilder();
        String line;
        List<String> message = new ArrayList<>();
        while (!(line = reader.readLine()).contains("#end")) {
            message.add(line);
        }
        return message;
    }
}
