package AI;

import components.state.Box;

public class HeuristicAndBox {

    public int getHeuristic() {
	return heuristic;
    }

    public final int heuristic;
    public final Box box;

    public HeuristicAndBox(int heuristic, Box box) {
	this.heuristic = heuristic;
	this.box = box;
    }

    @Override
    public String toString() {
	return this.heuristic + " " + box.toString();
    }
}
