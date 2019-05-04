package AI;
import Components.State.State;

import java.awt.geom.Point2D;

public class AllPairsShortestPath {

    private static boolean[][] wallMatrix;
    public static int[][] allPairsShortestPathMatrix;
    private int[][] index_matrix;
    int max_row;
    int max_col;
    int dim;

    public AllPairsShortestPath() {
        wallMatrix = State.getWallMatrix();
        max_row = wallMatrix.length;
        max_col = wallMatrix[0].length;
        dim = max_row*max_col;
        setIndexMatrix(wallMatrix);
        setAllPairsShortestPathMatrix();

    }

    private void setAllPairsShortestPathMatrix(){
        BFS bfs;
        int[][] bfsMatrix;
        int adjRowIndex = 0;
        int adjColIndex= 0;

        allPairsShortestPathMatrix = initializeMatrix();

        for(int i = 0; i < max_row; i++){
            for(int j = 0; j < max_col; j++){
                if(!wallMatrix[i][j]){
                    adjRowIndex = index_matrix[i][j];
                    bfs = new BFS(new Point2D.Float(i,j));
                    bfsMatrix = bfs.getBfsMatrix();
                    for(int i1 = 0; i1 < max_row; i1++){
                        for(int j1 = 0; j1 < max_row; j1++){
                            if(bfsMatrix[i1][j1] != -1){
                                adjColIndex = index_matrix[i1][j1];
                                allPairsShortestPathMatrix[adjRowIndex][adjColIndex] = bfsMatrix[i1][j1];
                                allPairsShortestPathMatrix[adjColIndex][adjRowIndex] = bfsMatrix[i1][j1];
                            }
                        }
                    }
                }
            }
        }
    }

    private int[][] initializeMatrix(){
        int [][] m = new int[dim][dim];
        for(int i = 0; i < dim; i++){
            for(int j = 0; j < dim; j++){
                m[i][j] = -1;
            }
        }
        return m;
    }

    private int[][] setIndexMatrix(boolean[][] m){
        int count = 0;
        int[][] indexMatrix = new int[m.length][m[0].length];
        for(int i = 0; i < m.length; i++){
            for(int j = 0; j < m[0].length; j++){
                indexMatrix[i][j] = count;
                count++;
            }
        }
        this.index_matrix = indexMatrix;
        return indexMatrix;
    }

    public int getHeuristic(Point2D.Float start, Point2D.Float end){
        int row = index_matrix[(int) start.x][(int) start.y];
        int col = index_matrix[(int) end.x][(int) end.y];
        return allPairsShortestPathMatrix[row][col];
    }

    public static int[][] getAllPairsShortestPathMatrix(){
        return allPairsShortestPathMatrix;
    }

    @Override
    public String toString() {
        String out = "---All Pairs Shortest Path Matrix--- \n";
        for(int i = 0; i < allPairsShortestPathMatrix.length; i++){
            for(int j = 0; j < allPairsShortestPathMatrix[0].length; j++){
                out += allPairsShortestPathMatrix[i][j] + "\t";
            }
            out+="\n\n";
        }
        return out;
    }
}
