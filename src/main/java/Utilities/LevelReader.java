package Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public abstract class LevelReader {



    private static String domain;
    private static String levelName;
    private static String colors;
    private static String initial;
    private static String goals;


    public static String getDomain() {
        return domain;
    }

    public static String getLevelName() {
        return levelName;
    }

    public static String getColors() {
        return colors;
    }

    public static String getInitial() {
        return initial;
    }

    public static String getGoals() {
        return goals;
    }

    public static void stringCreator(List<String> message) {
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
       goals = stringBuilderGoal.toString();
       colors = stringBuilderColors.toString();

    }

    public static List<String> readAllLines(BufferedReader reader) throws IOException {
        String line;
        List<String> message = new ArrayList<>();
        while (!(line = reader.readLine()).contains("#end")) {
            message.add(line);
        }
        return message;
    }
}
