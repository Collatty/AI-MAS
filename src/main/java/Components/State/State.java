package Components.State;

import java.util.ArrayList;
import java.util.List;

import Components.Agent;
import Components.Color;
import Utilities.LevelReader;

public class State {

    private final static List<List<Tile>> INITIAL_STATE = new ArrayList<>();
    private final static List<Goal> GOALS = new ArrayList<>();
    private static boolean[][] wallMatrix;
    private static int maxCol = 0;
    private static State state = new State();

    private List<Agent> agents = new ArrayList<>();
    private List<Block> blocks = new ArrayList<>();
    private List<List<Tile>> currentTiles = new ArrayList<>();




    //INITIAL STATE STRINGS STORED HERE
    private final static String STRING_DOMAIN = LevelReader.getDomain();
    private final static String STRING_LEVEL_NAME = LevelReader.getLevelName();
    private final static String STRING_COLORS = LevelReader.getColors();
    private final static String STRING_INITIAL = LevelReader.getInitial();
    private final static String STRING_GOALS = LevelReader.getGoals();

    public State() {
        int row = 0;
        int col = 0;
        List<Tile> currentList = new ArrayList<>();

        for (char character : STRING_INITIAL.toCharArray()){
            if (character == '\n') {
                if (col > maxCol) {
                    maxCol = col;
                }
                col = 0;
                row++;
                INITIAL_STATE.add(currentList);
                currentList = new ArrayList<>();
                continue;
            }

            if (character == '+') { // Wall.
                Tile tile = new Tile(row, col);
                tile.setWall(true);
                currentList.add(tile);
            } else if ('0' <= character && character <= '9') { // Agent.
                Tile tile = new Tile(row, col);
                Agent agt = new Agent(Character.getNumericValue(character),
                        convertFromStringToColor(getColorAgent(character)), row, col, null);
                tile.setTileOccupant(agt);
                currentList.add(tile);
                agents.add(agt);
            } else if ('A' <= character && character <= 'Z') { // Box.
                Tile tile = new Tile(row, col);
                Block box = new Block(character, convertFromStringToColor(getColorBlockAndGoal(character)), row, col);
                tile.setTileOccupant(box);
                currentList.add(tile);
                blocks.add(box);
            }  else if (character == ' ') {
                Tile tile = new Tile(row, col);
                currentList.add(tile);
            } else {
                System.err.println("Something's fishy at: " + (int) character);
                System.exit(1);
            }
            col++;
        }
        col = 0;
        row = 0;

        for (char character : STRING_GOALS.toCharArray()) {

            if (character == '\n') {
                row++;
                col = 0;
                continue;
            }
            if ('A' <= character && character <= 'Z') { // Goal.
                final Goal goal = new Goal(character, convertFromStringToColor(getColorBlockAndGoal(character)), row, col);
                INITIAL_STATE.get(row).get(col).setGoal(goal);
                GOALS.add(goal);
            }
            col++;
        }
        //REITERATING THROUGH TILES TO "CONNECT" THE BOARD
        setNeighbors(INITIAL_STATE);
        wallMatrix = createWallBoard(INITIAL_STATE);

    }


    public State(State copyState) {
        List<List<Tile>> copy = new ArrayList<>();
        List<Agent> agentCopy = new ArrayList<>();
        List<Block> blocksCopy = new ArrayList<>();
        for (List<Tile> row: copyState.getCurrentTiles()) {
            ArrayList<Tile> copyRow = new ArrayList<>();
            for (Tile tile : row) {
                Tile copyTile = new Tile(tile.getRow(), tile.getCol());
                if (tile.isGoal()) {
                    copyTile.setGoal(new Goal(tile.getGoal().getType(), tile.getGoal().getColor(),
                            tile.getGoal().getRow(),
                            tile.getGoal().getCol()));
                }
                if (tile.isWall()) {
                    copyTile.setWall(true);
                }
                if (tile.hasBlock()) {
                    Block block = new Block(
                            ((Block) tile.getTileOccupant()).getType(),
                            ((Block) tile.getTileOccupant()).getColor(),
                            ((Block) tile.getTileOccupant()).getRow(),
                            ((Block) tile.getTileOccupant()).getCol());
                    copyTile.setTileOccupant(block);
                    blocks.add(block);
                }
                if (tile.hasAgent()) {
                    Agent agent = new Agent(
                            ((Agent) tile.getTileOccupant()).getAgentNumber(),
                            ((Agent) tile.getTileOccupant()).getColor(),
                            ((Agent) tile.getTileOccupant()).getCol(),
                            ((Agent) tile.getTileOccupant()).getRow(),
                            null);
                    copyTile.setTileOccupant(agent);
                    agentCopy.add(agent);
                }
                copyRow.add(copyTile);
            }
            copy.add(copyRow);
        }
        setNeighbors(copy);
        this.currentTiles = copy;
        this.agents = agentCopy;
        this.blocks = blocksCopy;
    }

    public static List<List<Tile>> copyTiles(List<List<Tile>> tiles) {
        List<List<Tile>> copy = new ArrayList<>();
        for (List<Tile> row: tiles) {
            ArrayList<Tile> copyRow = new ArrayList<>();
            for (Tile tile : row) {
                Tile copyTile = new Tile(tile.getRow(), tile.getCol());
                if (tile.isGoal()) {
                    copyTile.setGoal(new Goal(tile.getGoal().getType(), tile.getGoal().getColor(),
                            tile.getGoal().getRow(),
                            tile.getGoal().getCol()));
                }
                if (tile.isWall()) {
                    copyTile.setWall(true);
                }
                if (tile.hasBlock()) {
                    Block block = new Block(
                            ((Block) tile.getTileOccupant()).getType(),
                            ((Block) tile.getTileOccupant()).getColor(),
                            ((Block) tile.getTileOccupant()).getRow(),
                            ((Block) tile.getTileOccupant()).getCol());
                    copyTile.setTileOccupant(block);
                }
                if (tile.hasAgent()) {
                    Agent agent = new Agent(
                            ((Agent) tile.getTileOccupant()).getAgentNumber(),
                            ((Agent) tile.getTileOccupant()).getColor(),
                            ((Agent) tile.getTileOccupant()).getCol(),
                            ((Agent) tile.getTileOccupant()).getRow(),
                            null);
                    copyTile.setTileOccupant(agent);
                }
                copyRow.add(copyTile);
            }
            copy.add(copyRow);
        }
        setNeighbors(copy);
        return copy;
    }

    public static List<Goal> copyGoals(List<Goal> goals) {
        List<Goal> copyGoals = new ArrayList<>();
        for (Goal goal : goals) {
            Goal copiedGoal = new Goal(goal.getType(), goal.getColor(), goal.getRow(), goal.getCol());
            copyGoals.add(copiedGoal);

        }
        return copyGoals;
    }

    //GETTERS

    public static State getState() {
        return state;
    }

    public static int getMaxCol() {
        return maxCol;
    }

    public List<List<Tile>> getCurrentTiles() {
        return currentTiles;
    }


    public static List<List<Tile>> getInitialState() {
        return INITIAL_STATE;
    }

    public static List<Goal> getGoals() {
        return GOALS;
    }

    public static boolean[][] getWallMatrix(){
        return wallMatrix;
    }

    public List<Agent> getAgents() { return agents; }

    public List<Block> getBlocks() { return blocks; }


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

    private String getColorBlockAndGoal(char blockAndGoal) {
        String[] colorsSplitted = STRING_COLORS.split("\n");
        for (String string : colorsSplitted) {
            String[] splittedEvenMore = string.split(":");
            if (splittedEvenMore[1].contains(Character.toString(blockAndGoal).toUpperCase())) {
                return splittedEvenMore[0];
            }
        }
        return "NA";
    }

    private static void setNeighbors(List<List<Tile>> tiles) {
        for (List<Tile> row : tiles) {
            for (Tile tile : row) {
                try {
                    tile.setNorthNeighbor(tiles.get(tile.getRow()-1).get(tile.getCol()).getTile());
                } catch (Exception e) {
                    tile.setNorthNeighbor(null);
                    //e.printStackTrace();
                }
                try {
                    tile.setWestNeighbor(tiles.get(tile.getRow()).get(tile.getCol()-1).getTile());
                } catch (Exception e) {
                    tile.setWestNeighbor(null);
                    //e.printStackTrace();
                }
                try {
                    tile.setEastNeighbor(tiles.get(tile.getRow()).get(tile.getCol()+1).getTile());
                } catch (Exception e) {
                    tile.setEastNeighbor(null);
                    //e.printStackTrace();
                }
                try {
                    tile.setSouthNeighbor(tiles.get(tile.getRow()+1).get(tile.getCol()).getTile());
                } catch (Exception e) {
                    tile.setSouthNeighbor(null);
                    //e.printStackTrace();
                }
            }
        }
    }

    private boolean[][] createWallBoard(List<List<Tile>> state){
        int max_row = state.size();
        int max_col = state.get(0).size();
        boolean[][] walls = new boolean[max_row][max_col];
        int i_row = 0;
        int i_col;
        for(List<Tile> row : state){
            i_col = 0;
            for(Tile col : row){
                if(col.isWall()){walls[i_row][i_col] = true;}
                else{walls[i_row][i_col] = false;}
                i_col++;
            }
            i_row++;
        }
        return walls;
    }

    private Color convertFromStringToColor(String stringColor){
        for (Color enumColor : Color.values()){
            if(stringColor.toUpperCase().equals(enumColor.toString())){
                return enumColor;
            }
        }
        throw new RuntimeException("Color unknown: " + stringColor);
    }
}
