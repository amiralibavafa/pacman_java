import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class AStar {
    private static String[] map = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX         X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };
    

    static final int ROWS= 21;
    static final int COLS = 19;

    //Directions to move R L U D
    private static int[][] DIRECTIONS = {
        {0, 1},
        {0, -1},
        {1, 0},
        {-1, 0}
    };

    public static class Node implements Comparable<Node> {
        int row;
        int col;
        int f;
        int g;
        int h;
        Node parent;

        Node(int row, int col, int g , int h, Node parent) {
            this.row = row;
            this.col = col;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f, other.f);
        }
    }

    private static int manhattan(int row, int col, int goalRow, int goalCol) {
        return Math.abs(row - goalRow) + Math.abs(col - goalCol);
    }

    public static List<int[]> aStar(int startRow, int startCol, int goalRow, int goalCol) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        boolean[][] closedList = new boolean[ROWS][COLS];

        Node startNode = new Node(startRow, startCol, 0, manhattan(startRow, startCol, goalRow, goalCol), null);
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            if (current.row == goalRow && current.col == goalCol) {
                return reconstructPath(current);
            }

            closedList[current.row][current.col] = true;

            for (int[] dir : DIRECTIONS) {
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];

                if (isValid(newRow, newCol) && !closedList[newRow][newCol]) {
                    int newG = current.g + 1;
                    int newH = manhattan(newRow, newCol, goalRow, goalCol);
                    Node neighbor = new Node(newRow, newCol, newG, newH, current);
                    openList.add(neighbor);
                }
            }
        }

        return null; // No path found
    }

    private static boolean isValid(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS && map[row].charAt(col) != 'X';
    }

    private static List<int[]> reconstructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(new int[]{node.row, node.col});
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }


}
