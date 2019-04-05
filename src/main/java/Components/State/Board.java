package Components.State;

import Utilities.LevelReader;

import javax.swing.text.AbstractDocument;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class Board {

    private static List<List<Tile>> tiles = new ArrayList<>();

    private final static String domain = LevelReader.getDomain();
    private final static String levelName = LevelReader.getLevelName();
    private final static String colors = LevelReader.getColors();
    private final static String initial = LevelReader.getInitial();
    private final static String goals = LevelReader.getGoals();




    public Board(BufferedReader serverMessages) throws Exception {

        int row = 0;
        int col = 0;
        List<Tile> currentList = new ArrayList<>();

        for (char character : initial.toCharArray()){
            if (character == '\n') {
                col = 0;
                row++;
                tiles.add(currentList);
                currentList = new ArrayList<>();
                continue;
            }

            if (character == '+') { // Wall.
                Tile tile = new Tile(col, row);
                tile.setTileOccupant(new Wall(col, row));
                currentList.add(tile);
            } else if ('0' <= character && character <= '9') { // Agent.
                Tile tile = new Tile(col, row);
                tile.setTileOccupant(new Agent(character, getColorAgent(character), col, row));
                currentList.add(tile);
            } else if ('A' <= character && character <= 'Z') { // Box.
                Tile tile = new Tile(col, row);
                tile.setTileOccupant(new Block(getColorBlock(character), col, row));
                currentList.add(tile);
            }  else if (character == ' ') {
                Tile tile = new Tile(col, row);
                currentList.add(tile);
            } else {
                System.err.println("Something's fishy at: " + (int) character);
                System.exit(1);
            }
            col++;
        }
        col = 0;
        row = 0;
        currentList = new ArrayList<>();

        for (char character : goals.toCharArray()) {

            if (character == '\n') {
                col = 0;
                row++;
                tiles.add(currentList);
                currentList = new ArrayList<>();
                continue;
            }
            if ('A' <= character && character <= 'Z') { // Goal.
                tiles.get(row).get(col).setTileOccupant(new Goal(getColorGoal(character), col, row));
            }
            col++;
        }
    }

    //GETTERS

    private String getColorAgent(char Agent) {
        return "";
    }

    private String getColorBlock(char Block) {
        //TODO
        return "";
    }

    private String getColorGoal(char Goal) {
        //TODO
        return "";
    }

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




}
