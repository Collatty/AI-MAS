package AI;

import components.state.State;

import java.awt.geom.Point2D;
import java.util.LinkedList;


public class BFS {

    private static int max_row = State.getWallMatrix().length;
    private static int max_col = State.getWallMatrix()[0].length;
    private static boolean[][] walls = State.getWallMatrix();
    private int[][] bfsMatrix;
    private LinkedList<Point2D.Float> queue;
    private boolean[][] visited;
    private int move_count;
    private int nodes_left_in_layer;
    private int nodes_in_next_layer;

    public BFS(Point2D.Float start) {
        bfsMatrix = solveBFS(start);
    }

    public static void loadNewLevel() {
        walls = State.getWallMatrix();
        max_row = State.getWallMatrix().length;
        max_col = State.getWallMatrix()[0].length;
    }

    private int[][] solveBFS(Point2D.Float start){
        initializebfsMatrix();
        move_count = 0;
        nodes_left_in_layer = 1;
        nodes_in_next_layer = 0;

        queue = new LinkedList<>();
        Point2D.Float field = start;
        visited = new boolean[max_row][max_col];

        visited[(int) field.x][(int) field.y] = true;
        queue.add(field);
        //bfsMatrix[(int) field.y][(int) field.x] = 0;

        while(queue.size() > 0){
            field = queue.poll();
            bfsMatrix[(int) field.x][(int) field.y] = move_count;
            explore_neigbours((int) field.x, (int) field.y);
            nodes_left_in_layer--;
            if (nodes_left_in_layer == 0) {
                nodes_left_in_layer = nodes_in_next_layer;
                nodes_in_next_layer = 0;
                move_count++;
            }
        }
        return bfsMatrix;
    }

    private void explore_neigbours(int r, int c){
      int[] dr = {0, -1, 1, 0, 0};
      int[] dc = {0,0 , 0, 1, -1};

      for(int i=0; i < 5; i++){
          int rr = r + dr[i];
          int cc = c + dc[i];

          // SKIP INVAILD CELLS, i.e. cells out of grid
          if(rr < 0 || cc < 0){
              continue;
          }
          if(rr >= max_row || cc >= max_col){
              continue;
          }
          //Skip visitd locations or wall cells
          if (visited[rr][cc]){continue;}
          if (walls[rr][cc]){continue;}
          //(rr, cc) is a neighbouring cell of (r,c)

          queue.add(new Point2D.Float(rr, cc));
          bfsMatrix[rr][cc] = move_count+1;
          visited[rr][cc] = true;
          nodes_in_next_layer++;
      }
    }

    @Override
    public String toString() {
        String out = "---BFS Matrix--- \n";
        for(int i = 0; i < bfsMatrix.length; i++){
            for(int j = 0; j < bfsMatrix[0].length; j++){
                out += bfsMatrix[i][j] + "\t";
            }
            out+="\n";
        }
        return out;
    }

    private void initializebfsMatrix(){
        bfsMatrix = new int[max_row][max_col];
        for(int i = 0; i < bfsMatrix.length; i++){
            for(int j = 0; j < bfsMatrix[0].length; j++){
                bfsMatrix[i][j] = -1;
            }
        }
    }

    public int[][] getBfsMatrix(){
        return bfsMatrix;
    }
}
