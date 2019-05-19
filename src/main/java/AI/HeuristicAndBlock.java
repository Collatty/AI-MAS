package AI;

import components.state.Block;

public class HeuristicAndBlock {

    public int getHeuristic() {
        return heuristic;
    }

    public final int heuristic;
    public final Block block;

    public HeuristicAndBlock (int heuristic, Block block) {
        this.heuristic = heuristic;
        this.block = block;
    }

    @Override
    public String toString() {
        return this.heuristic +" "+ block.toString();
    }
}
