package Components;

import java.util.ArrayList;

public class Action {

    //TODO implement actions, push pull move

    public final Type actionType;
    public final Dir dir1;
    public final Dir dir2;

    // Order of enum is used for determining opposite actions
    public enum Dir {
        N, W, E, S
    }

    public enum Type {
        Move, Push, Pull //, NoOp
    }

    private Action(Dir d) {
        this.actionType = Type.Move;
        this.dir1 = d;
        this.dir2 = null;
    }

    private Action(Type t, Dir d1, Dir d2) {
        this.actionType = t;
        this.dir1 = d1;
        this.dir2 = d2;
    }


    public static final Action[] EVERY;
    static {
        ArrayList<Action> cmds = new ArrayList<>();

        //push
        for (Dir d1 : Dir.values()) {
            for (Dir d2 : Dir.values()) {
                if (!Action.isOpposite(d1, d2)) {
                    cmds.add(new Action(Type.Push, d1, d2));
                }
            }
        }
        //pull
        for (Dir d1 : Dir.values()) {
            for (Dir d2 : Dir.values()) {
                if (d1 != d2) {
                    cmds.add(new Action(Type.Pull, d1, d2));
                }
            }
        }

        //Move
        for (Dir d : Dir.values()) {
            cmds.add(new Action(d));
        }

        // add every possible action to static final list of action objects
        EVERY = cmds.toArray(new Action[0]);
    }

    // check if direction are opposite of each other. i.e. if N-S, S-N, W-E or E-W
    private static boolean isOpposite(Dir d1, Dir d2) {
        return d1.ordinal() + d2.ordinal() == 3;
    }

    //convert N, S to row coordinates
    public static int dirToRowChange(Dir d) {
        // South is down one row (1), north is up one row (-1).
        switch (d) {
            case S:
                return 1;
            case N:
                return -1;
            default:
                return 0;
        }
    }

    //convert W, E to col coordinates
    public static int dirToColChange(Dir d) {
        // East is right one column (1), west is left one column (-1).
        switch (d) {
            case E:
                return 1;
            case W:
                return -1;
            default:
                return 0;
        }
    }


    @Override
    public String toString() {
        if (this.actionType == Type.Move)
            return String.format("[%s(%s)]", this.actionType.toString(), this.dir1.toString());
        else
            return String.format("[%s(%s,%s)]", this.actionType.toString(), this.dir1.toString(), this.dir2.toString());
    }
}


