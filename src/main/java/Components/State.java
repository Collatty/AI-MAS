package Components;

public class State {

    public static final int MAX_ROW = 50;
    public static final int MAX_COLUMN = 50;
    public static final char[][] GOALS = null;
    public static final char[][] WALLS = null;
    public char[][] boxes = new char[MAX_ROW][MAX_COLUMN];
    public char[][] agents = new char[MAX_ROW][MAX_COLUMN];
    public State parent;

    public State(State parent) {
        this.parent = parent;




    }



    public boolean isGoalState() {
        for (int row = 1; row < MAX_ROW; row++) {
            for (int column = 1; column < MAX_COLUMN; column++) {
                char goal = GOALS[row][column];
                char box = Character.toLowerCase(this.boxes[row][column]);
                if (goal > 0 && goal != box) {
                    return false;
                }
            }
        }
        return true;
    }

    //TODO implement state representation
}
