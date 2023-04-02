import models.Cell;
import models.Direction;
import models.Particle;
import utils.ConsoleParser;
import utils.Properties;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class LatticeGas {

    private static final double EPSILON = 0.1;
    private static final int H = 200;
    private static final int V = 200;

    private static final Properties properties = new Properties();

    public static void main(String[] args) {


        try {
            ConsoleParser.parseArguments(args, properties);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("\n");

        try {
            FileWriter myWriter = new FileWriter("src/main/resources/" + properties.getOutFileName() + ".txt");
            PrintWriter printWriter = new PrintWriter(myWriter);

            FileWriter densityWriter = null;
            PrintWriter densityPrint = null;
            if(properties.isPrintDensity()) {
                densityWriter = new FileWriter("src/main/resources/" + "density_" + properties.getOutFileName() + ".txt");
                densityPrint = new PrintWriter(densityWriter);
                densityPrint.println(properties.getN());
                densityPrint.println(EPSILON);
            }

            FileWriter velocityWriter = new FileWriter("src/main/resources/" + "velocity_" + properties.getOutFileName() + ".txt");
            PrintWriter velocityPrint = new PrintWriter(velocityWriter);

            printWriter.println(properties.getN());
            printWriter.println(properties.getD());
            printWriter.println(H + "\t" + V);

            velocityPrint.println(properties.getN());
            velocityPrint.println(properties.getD());


            for (int r = 0; r < properties.getRuns(); r++) {

                Cell[][] cells = Cell.initializeCells(H, V, properties.getD(), ConsoleParser.dynamicFile);
                int particles_right = 0;
                int particles_left;
                int iterations = 0;

                while (particles_right <= ((int) (properties.getN() / 2) * (1 - EPSILON))) {
                    printWriter.println(iterations);
                    particles_right = 0;
                    particles_left = 0;

                    for (int i = 0; i < cells.length; i++) {
                        for (int j = 0; j < cells[i].length; j++) {
                            cells[i][j].setNextParticles(cells[i][j].collision());
                            if (j > 100)
                                particles_right += Arrays.stream(cells[i][j].getParticles()).filter(Objects::nonNull).toArray().length;
                            else
                                particles_left += Arrays.stream(cells[i][j].getParticles()).filter(Objects::nonNull).toArray().length;
                        }
                    }
                    cells = updateCellsWithNewDirections(cells);
                    if(r == 0)
                        printOutput(printWriter, cells);

                    if (properties.isPrintDensity() && densityPrint != null) {
                        densityPrint.println(iterations);
                        densityPrint.println(particles_left + "\t" + particles_right);
                    }

                    iterations++;
                }

                velocityPrint.println(iterations);

                if(r == properties.getRuns() - 1) {
                    myWriter.close();
                    if (densityWriter != null)
                        densityWriter.close();
                    velocityWriter.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printOutput(PrintWriter printWriter, Cell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                printOutputToFile(printWriter, cells, j, i);
            }
        }
    }

    private static void printOutputToFile(PrintWriter printWriter, Cell[][] cells, int j, int i) {
        long averageDirection = 0;
        for (Particle particle : cells[i][j].getParticles()) {
            if (particle == null) {
                 continue;
            }
            averageDirection = (averageDirection + particle.getDirection().ordinal()) % 6;
        }
        if (Arrays.stream(cells[i][j].getParticles()).filter(Objects::nonNull).count() > 0) {
            printWriter.printf("%d\t%d\t%s\n", j, i, particlesToBits(cells[i][j].getParticles()));
        }
    }

    private static List<Integer> particlesToBits(Particle[] particles) {
        List<Integer> bits = new ArrayList<>();
        for (Particle particle : particles) {
            if (particle == null) {
                bits.add(0);
            } else {
                bits.add(1);
            }
        }
        return bits;
    }

    private static Cell[][] updateCellsWithNewDirections(Cell[][] cells) {
        Cell[][] newCell = Cell.createGrid(cells.length, cells[0].length, properties.getD());
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                for (Particle particle : cells[i][j].getNextParticles()) {
                    if(particle == null)
                        continue;
                    int newRow = getNewRow(i,particle.getDirection());
                    int newCol = getNewCol(i,j,particle.getDirection());
                    if( (0 <= newCol && newCol < cells.length) && (0 <= newRow && newRow < cells.length) )
                        newCell[newRow][newCol].createParticle(particle);
                }

            }
        }
        return newCell;
    }

    private static int getNewRow(int row, Direction direction){
        if(direction.equals(Direction.B) || direction.equals(Direction.E))
            return row;
        else if(direction.equals(Direction.D) || direction.equals(Direction.C))
            return row + 1 ;
        else return row - 1;
    }

    private static int getNewCol(int row, int col, Direction direction){
        boolean par = row % 2 == 0;
        if (par) {
            switch (direction) {
                case F:
                case D:
                case E:
                    return col - 1;
                case B:
                    return col + 1;
                case A:
                case C:
                    return col;
            }
        } else {
            switch (direction) {
                case A:
                case C:
                case B:
                    return col + 1;
                case F:
                case D:
                    return col;
                case E:
                    return col - 1;
            }
        }
        throw new RuntimeException("Invalid combination");
    }

//    static String printCells(Cell[][] cells) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < cells.length; i++) {
//            for (int j = 0; j < cells[i].length; j++) {
//                StringBuilder sb_line = new StringBuilder();
//                for (int k = 0; k < cells[i][j].getParticles().length; k++) {
//                    if(cells[i][j].getParticles()[k] != null)
//                        sb_line.append(cells[i][j].getParticles()[k].getDirection().ordinal());
//                }
//                if(sb_line.length() == 0)
//                    sb_line.append("-");
//                if(cells[i][j].isSolid())
//                    sb_line = new StringBuilder("||");
//                sb.append(sb_line).append("\t");
//            }
//            sb.append("\n");
//        }
//        return sb.toString();
//    }
}
