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
            int particles_left = 0;
            int iterations = 0;
            long start = System.currentTimeMillis();
            System.out.println("Particles right: " + particles_right);
            System.out.println("Particles total: " + properties.getN());
            System.out.println("Condición de corte: " + ((properties.getN() / 2) * (1 - EPSILON)));
            while (particles_right < ((int)(properties.getN() / 2) * (1 - EPSILON))) {
//            for(int l = 0; l < 5; l++) {
                printWriter.println(iterations);
                particles_right = 0;
                particles_left = 0;
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
                        else
                            particles_left += countCollisions(outDirections);
//                        printOutputToFile(printWriter, cells, j, i);
                    }
                }
                updateCellsWithNewDirections(cells);
                String aux = printCells(cells);
                printWriter.println(aux);
                iterations++;
                if(particles_right % 100 == 0 || particles_right > 2000) {
                    System.out.println("Particles right: " + particles_right);
                    System.out.println("Particles left: " + particles_left);
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
            distanceTrueThree(inDirections, outDirections);
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

    private static void distanceTrueThree(boolean[] inDirections, boolean[] outDirections) {
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
        int spectator = -1;
        int random = -1;
        if(distance1 == 3) {
            spectator = third;
            random = inDirections[(third + 1) % 6] ? 0 : 1;
        } else if(distance2 == 3) {
            spectator = first;
            random = inDirections[(first + 1) % 6] ? 0 : 1;
        } else if(distance3 == 3) {
            spectator = second;
            random = inDirections[(second + 1) % 6] ? 0 : 1;
        }
        if(spectator != -1) {
            inDirections[spectator] = false;
            headOn(inDirections, outDirections, random);
            outDirections[spectator] = true;
        }
        else {
            for (int i = 0; i < outDirections.length; i++) {
                outDirections[i] = !inDirections[i];
            }
        }
    }

    static String printCells(Cell[][] cells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                StringBuilder sb_line = new StringBuilder();
                for (int k = 0; k < cells[i][j].getDirections().length; k++) {
                    if(cells[i][j].getDirections()[k])
                        sb_line.append(k);
                }
                if(sb_line.length() == 0)
                    sb_line.append("-");
                if(cells[i][j].isSolid())
                    sb_line = new StringBuilder("||");
                sb.append(sb_line).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
