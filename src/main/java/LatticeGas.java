import models.Cell;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LatticeGas {

    public static void main(String[] args) {

        Properties properties = new Properties();

//        try {
//            FileWriter myWriter = new FileWriter("src/main/resources/output.txt");
//            myWriter.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        try {
            ConsoleParser.parseArguments(args, properties);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        Cell[][] cells = initializeCells(10, 10);

        cells[0][0].createParticle(1);
        cells[0][2].createParticle(4);
        cells[9][0].createParticle(0);
        cells[8][2].createParticle(0);
        cells[8][1].createParticle(0);
        cells[6][5].createParticle(0);
        cells[5][5].createParticle(0);
        printCells(cells);
        System.out.println("\n");
        try {
            FileWriter myWriter = new FileWriter("src/main/resources/output.txt");
            myWriter.write("5\n");
            myWriter.write("1\n");
            myWriter.write("10 10\n");
            myWriter.write("timestep?\n");

            for (int times = 0; times < 3; times++) {
                for (int i = 0; i < cells.length; i++) {
                    for (int j = 0; j < cells[i].length; j++) {
//                    cells[i][j].setDirections(cells[i][j].getNewDirections());
                        boolean[] inDirections = collectDirections(cells, i, j, i % 2 != 0);
                        boolean[] outDirections = evaluate(inDirections, cells[i][j]);
                        cells[i][j].setNewDirections(outDirections);
                    }
                }
                updateCellsWithNewDirections(cells);
                System.out.println("Iteration " + (times + 1));
                printCells(cells);
                updateOutputToFile(myWriter, cells);
            }
            myWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateOutputToFile(FileWriter myWriter, Cell[][] cells) throws Exception{
        myWriter.write("nada");
    }

    private static Cell[][] initializeCells(int horizontal, int vertical) {
        Cell[][] cells = new Cell[10][10];
        for (int i = 0; i < vertical; i++) {
            for (int j = 0; j < horizontal; j++) {
                cells[i][j] = new Cell(false);
            }
        }
        return cells;
    }

    private static void updateCellsWithNewDirections(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].setDirections(cells[i][j].getNewDirections());
            }
        }
    }

    //i -> par: (i - 1, j) B, (i + 1, j) E, (i, j + 1) F, (i - 1, j + 1) A, (i, j - 1) D, (i - 1, j - 1) C
    //i -> impar: (i - 1, j) B , (i + 1, j) E, (i, j + 1) A, (i + 1, j + 1) F, (i, j - 1) C, (i + 1, j - 1) D
    static boolean[] collectDirections(Cell[][] cells, int i, int j, boolean isOdd) {
        boolean[] directions = new boolean[6];
//        directions[4] = checkNeighbour(i - 1, j, 1);
//        directions[1] = checkNeighbour(i + 1, j, 4);
//        if (isOdd) {
//            directions[3] = checkNeighbour(i, j + 1, 0);
//            directions[2] = checkNeighbour(i + 1, j + 1, 5);
//            directions[5] = checkNeighbour(i, j - 1, 2);
//            directions[0] = checkNeighbour(i + 1, j - 1, 3);
//        } else {
//            directions[2] = checkNeighbour(i, j + 1, 5);
//            directions[3] = checkNeighbour(i - 1, j + 1, 0);
//            directions[0] = checkNeighbour(i, j - 1, 3);
//            directions[5] = checkNeighbour(i - 1, j - 1, 2);
//        }
        directions[4] = checkNeighbour(cells, i, j - 1, 1);
        directions[1] = checkNeighbour(cells, i, j + 1, 4);
        if (isOdd) {
            directions[3] = checkNeighbour(cells, i + 1, j, 0);
            directions[2] = checkNeighbour(cells, i + 1, j + 1, 5);
            directions[5] = checkNeighbour(cells, i - 1, j, 2);
            directions[0] = checkNeighbour(cells, i - 1, j + 1, 3);
        } else {
            directions[2] = checkNeighbour(cells, i + 1, j, 5);
            directions[3] = checkNeighbour(cells, i + 1, j - 1, 0);
            directions[0] = checkNeighbour(cells, i - 1, j, 3);
            directions[5] = checkNeighbour(cells, i - 1, j - 1, 2);
        }

//        if (directions[0] || directions[1] || directions[2] || directions[3] || directions[4] || directions[5])
//            System.out.println("i: " + i + " j: " + j + " directions: " + directions[0] + " " + directions[1] + " " + directions[2] + " " + directions[3] + " " + directions[4] + " " + directions[5]);

        return directions;
    }

    static boolean checkNeighbour(Cell[][] cells, int i, int j, int index) {
        try {
            return cells[i][j].getDirections()[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    static boolean[] evaluate(boolean[] inDirections, Cell cell) {
        boolean[] outDirections = new boolean[6];
        int collisionCount = countCollitions(inDirections);
        // When there are no particles colliding, the particle simply passes through
        if (collisionCount == 1) {
            for (int i = 0; i < outDirections.length; i++) {
                if (inDirections[i]) {
                    outDirections[(i + 3) % 6] = inDirections[i];
                    break;
                }
            }
        }
        // When there are 2 particles colliding, we can shift the inDirections array by 1 or 2 indices, depending
        // on the random value generated by the cell (0 or 1)
        else if (collisionCount == 2) {
            for (int i = 0; i < outDirections.length; i++) {
                outDirections[(i + (int) cell.getRandom() + 1) % 6] = inDirections[i];
            }
        }
        // When there are 3 particles colliding, we can simply invert the inDirections array
        else if (collisionCount == 3) {
            for (int i = 0; i < outDirections.length; i++) {
                outDirections[i] = !inDirections[i];
            }
        }
        // When there are 4 or more particles colliding, nos desconocemos
        else {
            outDirections = inDirections;
        }
        return outDirections;
    }

    private static int countCollitions(boolean[] inDirections) {
        int count = 0;
        for (boolean element : inDirections) {
            if (element) count++;
        }
        return count;
    }

    static void printCells(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j].getDirections()[1]) {
                    System.out.print("1");
                } else if (cells[i][j].getDirections()[4]) {
                    System.out.print("4");
                } else if (cells[i][j].getDirections()[2]) {
                    System.out.print("2");
                } else if (cells[i][j].getDirections()[3]) {
                    System.out.print("3");
                } else if (cells[i][j].getDirections()[5]) {
                    System.out.print("5");
                } else if (cells[i][j].getDirections()[0]) {
                    System.out.print("0");
                } else {
                    System.out.print("-");
                }
            }
            System.out.println();
        }
    }
}
