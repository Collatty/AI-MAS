package Components.State;

import Utilities.LevelReader;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final static List<List<Tile>> tiles = new ArrayList<>();

    //INITIAL STATE STORED HERE
    private final static String STRING_DOMAIN = LevelReader.getDomain();
    private final static String STRING_LEVEL_NAME = LevelReader.getLevelName();
    private final static String STRING_COLORS = LevelReader.getColors();
    private final static String STRING_INITIAL = LevelReader.getInitial();
    private final static String STRING_GOALS = LevelReader.getGoals();

    private final static List<Goal> GOALS = new ArrayList<>();




    public Board() throws Exception {

        int row = 0;
        int col = 0;
        List<Tile> currentList = new ArrayList<>();

        for (char character : STRING_INITIAL.toCharArray()){
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
                tile.setTileOccupant(new Block(getColorBlockOrGoal(character), col, row));
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

        for (char character : STRING_GOALS.toCharArray()) {

            if (character == '\n') {
                col = 0;
                row++;
                tiles.add(currentList);
                currentList = new ArrayList<>();
                continue;
            }
            if ('A' <= character && character <= 'Z') { // Goal.
                Goal goal = new Goal(getColorBlockOrGoal(character), col, row);
                tiles.get(row).get(col).setGoal(goal);
                GOALS.add(goal);
            }
            col++;
        }
        //REITERATING THROUGH TILES TO "CONNECT" THE BOARD
        setNeighbors();
    }

    //GETTERS

    public static List<Goal> getGoals() {
        return GOALS;
    }

    public static String getStringDomain() {
        return STRING_DOMAIN;
    }

    public static String getStringLevelName() {
        return STRING_LEVEL_NAME;
    }

    public static String getStringColors() {
        return STRING_COLORS;
    }

    public static String getStringInitial() {
        return STRING_INITIAL;
    }

    public static String getStringGoals() {
        return STRING_GOALS;
    }

    // There should be no other numbers in the color string besides the agents
    private String getColorAgent(char agent) {
        String[] colorsSplitted = STRING_COLORS.split("\n");
        for (String string : colorsSplitted) {
            if (string.contains(Character.toString(agent))){
                String[] splittedEvenMore = string.split(":");
                return splittedEvenMore[0];
            }
        }
        return "NA";
    }

    private String getColorBlockOrGoal(char block) {
        String[] colorsSplitted = STRING_COLORS.split("\n");
        for (String string : colorsSplitted) {
            String[] splittedEvenMore = string.split(":");
            if (splittedEvenMore[1].contains(Character.toString(block).toUpperCase())) {
                return splittedEvenMore[0];
            }
        }
        return "NA";
    }

    public void setNeighbors() {
        for (List<Tile> row : tiles) {
            for (Tile tile : row) {
                try {
                    tile.setNorthNeighbor(tiles.get(tile.getRow()-1).get(tile.getColumn()).getTile());
                } catch (Exception e) {
                    tile.setNorthNeighbor(null);
                    e.printStackTrace();
                }
                try {
                    tile.setWestNeighbor(tiles.get(tile.getRow()).get(tile.getColumn()-1).getTile());
                } catch (Exception e) {
                    tile.setWestNeighbor(null);
                    e.printStackTrace();
                }
                try {
                    tile.setEastNeighbor(tiles.get(tile.getRow()).get(tile.getColumn()+1).getTile());
                } catch (Exception e) {
                    tile.setEastNeighbor(null);
                    e.printStackTrace();
                }
                try {
                    tile.setSouthNeighbor(tiles.get(tile.getRow()+1).get(tile.getColumn()).getTile());
                } catch (Exception e) {
                    tile.setSouthNeighbor(null);
                    e.printStackTrace();
                }
            }
        }
    }




}
