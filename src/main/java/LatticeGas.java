import models.Cell;
import utils.ConsoleParser;
import utils.Properties;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class LatticeGas {

    private static final double EPSILON = 0.1;

    public static void main(String[] args) throws FileNotFoundException {

        Properties properties = new Properties();

        try {
            ConsoleParser.parseArguments(args, properties);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        Cell[][] cells = Cell.initializeCells(properties.getH(), properties.getV(), properties.getD(), ConsoleParser.dynamicFile);

        printCells(cells);
        System.out.println("\n");
        try {
            FileWriter myWriter = new FileWriter("src/main/resources/output.txt");
            PrintWriter printWriter = new PrintWriter(myWriter);
            printWriter.println(properties.getN());
            printWriter.println(properties.getD());
            printWriter.println(properties.getH() + "\t" + properties.getV());

            int particles_right = 0;
            int iterations = 0;
            long start = System.currentTimeMillis();
            System.out.println("Particles right: " + particles_right);
            System.out.println("Particles total: " + properties.getN());
            System.out.println("Condición de corte: " + ((properties.getN() / 2) * (1 - EPSILON)));
            while (particles_right < ((int)(properties.getN() / 2) * (1 - EPSILON))) {
//            for(int l = 0; l < 5; l++) {
                printWriter.println(iterations);
                particles_right = 0;
                for (int i = 0; i < cells.length; i++) {
                    for (int j = 0; j < cells[i].length; j++) {
                        boolean[] inDirections = collectDirections(cells, i, j, i % 2 != 0);
                        boolean[] outDirections;
                        if(cells[i][j].isSolid())
                            outDirections = inDirections;
                        else
                            outDirections = evaluate(inDirections, cells[i][j]);
                        cells[i][j].setNewDirections(outDirections);
                        if (j > 100)
                            particles_right += countCollisions(outDirections);

                        printOutputToFile(printWriter, cells, j, i);
                    }
                }
                updateCellsWithNewDirections(cells);
//                printCells(cells);
                iterations++;
                if(particles_right % 100 == 0 || particles_right > 2000) {
                    System.out.println("Particles right: " + particles_right);
                    System.out.println(iterations);
                }
            }
            printCells(cells);
            System.out.println("Iterations: " + iterations);
            System.out.println("Time: " + (System.currentTimeMillis() - start) + " ms");
            myWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printOutputToFile(PrintWriter printWriter, Cell[][] cells, int j, int i) {
        printWriter.printf("%d %d %d %d %d %d %d %d %d %d %d\n",
                j,
                i,
                cells[i][j].getDirections()[1] ? 1 : 0,
                cells[i][j].getDirections()[0] ? 1 : 0,
                cells[i][j].getDirections()[5] ? 1 : 0,
                cells[i][j].getDirections()[4] ? 1 : 0,
                cells[i][j].getDirections()[3] ? 1 : 0,
                cells[i][j].getDirections()[2] ? 1 : 0,
                cells[i][j].isSolid() ? 1 : 0,
                cells[i][j].isSolid() ? 1 : 0,
                cells[i][j].getRandom()
        );
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
        int collisionCount = countCollisions(inDirections);
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
            if(distanceTrueTwo(inDirections) == 3) {
                headOn(inDirections, outDirections, cell.getRandom());
            } else
                outDirections = inDirections;
        }
        // When there are 3 particles colliding, we can simply invert the inDirections array
        else if (collisionCount == 3) {
            //TODO: casos head on + spectator
            switch (distanceTrueThree(inDirections)) {
                case 0: {
                    for (int i = 0; i < outDirections.length; i++) {
                        outDirections[i] = !inDirections[i];
                    }
                }
                case 1: {
                    headOn(inDirections, outDirections, 0);
                }
                case 2: {
                    headOn(inDirections, outDirections, 1);
                }
                break;
            }

        }
        else if(collisionCount == 4) {
            if(distanceFalseTwo(inDirections) == 0) {
                headOn(inDirections, outDirections, cell.getRandom());
            } else
                //TODO: si no es head on, nos desconocemos?
                outDirections = inDirections;
        }
        // When there are 4 or more particles colliding, nos desconocemos
        else {
            outDirections = inDirections;
        }
        return outDirections;
    }

    private static void headOn(boolean[] inDirections, boolean[] outDirections, int random) {
        for (int i = 0; i < outDirections.length; i++) {
            outDirections[(i + random + 1) % 6] = inDirections[i];
        }
    }

    private static int countCollisions(boolean[] inDirections) {
        int count = 0;
        for (boolean element : inDirections) {
            if (element) count++;
        }
        return count;
    }

    private static int distanceTrueTwo(boolean[] inDirections) {
        int first = -1;
        int second = -1;
        for (int i = 0; i < inDirections.length; i++) {
            if (inDirections[i]) {
                if (first == -1) {
                    first = i;
                } else {
                    second = i;
                }
            }
        }
        return ((second - first) + 6) % 6;
    }

    private static int distanceFalseTwo(boolean[] inDirections) {
        int first = -1;
        int second = -1;
        for (int i = 0; i < inDirections.length; i++) {
            if (!inDirections[i]) {
                if (first == -1) {
                    first = i;
                } else {
                    second = i;
                }
            }
        }
        return ((second - first) + 6) % 6;
    }

    private static int distanceTrueThree(boolean[] inDirections) {
        int first = -1;
        int second = -1;
        int third = -1;
        for (int i = 0; i < inDirections.length; i++) {
            if (inDirections[i]) {
                if (first == -1) {
                    first = i;
                } else if(second == -1) {
                    second = i;
                } else {
                    third = i;
                }
            }
        }
        int distance1 = ((second - first) + 6) % 6;
        int distance2 = ((third - second) + 6) % 6;
        int distance3 = ((first - third) + 6) % 6;
        if(distance1 == 3)
            return inDirections[(third + 1) % 6]? 1 : 2;
        else if(distance2 == 3)
            return inDirections[(first + 1) % 6]? 1 : 2;
        else if(distance3 == 3)
            return inDirections[(second + 1) % 6]? 1 : 2;
        else
            return 0;
    }

    static void printCells(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < cells[i][j].getDirections().length; k++) {
                    if(cells[i][j].getDirections()[k])
                        sb.append(k);
                }
                if(sb.length() == 0)
                    sb.append("-");
                if(cells[i][j].isSolid())
                    sb = new StringBuilder("|");
                System.out.print(sb + " ");
            }
            System.out.println();
        }
    }
}
