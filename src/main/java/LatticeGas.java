import models.Cell;

public class LatticeGas {
    static Cell[][] cells = new Cell[10][10];
    public static void main(String[] args) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell(false);
            }
        }
        cells[9][0].createParticle(0);
        while (true) {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    cells[i][j].setDirections(cells[i][j].getNewDirections());
                    boolean[] inDirections = collectDirections(i, j, i % 2 != 0);
                    cells[i][j].setNewDirections(evaluate(inDirections));

                }
            }
        }
    }

    //i -> par: (i - 1, j) B, (i + 1, j) E, (i, j + 1) F, (i - 1, j + 1) A, (i, j - 1) D, (i - 1, j - 1) C
    //i -> impar: (i - 1, j) B , (i + 1, j) E, (i, j + 1) A, (i + 1, j + 1) F, (i, j - 1) C, (i + 1, j - 1) D
    static boolean[] collectDirections(int i, int j, boolean isOdd) {
        boolean[] directions = new boolean[6];
        directions[4] = checkNeighbour(i - 1, j, 1);
        directions[1] = checkNeighbour(i + 1, j, 4);
        if (isOdd) {
            directions[3] = checkNeighbour(i, j + 1, 0);
            directions[2] = checkNeighbour(i + 1, j + 1, 5);
            directions[5] = checkNeighbour(i, j - 1, 2);
            directions[0] = checkNeighbour(i + 1, j - 1, 3);
        } else {
            directions[2] = checkNeighbour(i, j + 1, 5);
            directions[3] = checkNeighbour(i - 1, j + 1, 0);
            directions[0] = checkNeighbour(i, j - 1, 3);
            directions[5] = checkNeighbour(i - 1, j - 1, 2);
        }
        return directions;
    }

    static boolean checkNeighbour(int i, int j, int index) {
        return cells[i][j].getDirections()[index];
    }

    static boolean[] evaluate(boolean[] inDirections) {
        //TODO
        return inDirections;
    }

    static void printCells(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                System.out.print(cells[i][j].getDirections()[0] ? "1" : "0");
            }
            System.out.println();
        }
    }
}
