package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Cell {
    private boolean[] directions = new boolean[6];
    private boolean[] newDirections = new boolean[6];
    private boolean solid;
    private long random;

    public Cell(boolean solid) {
        this.solid = solid;
        this.random = Math.round(Math.random());
    }

    public void createParticle(int direction) {
        directions[direction] = true;
    }

    public boolean[] getDirections() {
        return directions;
    }

    public boolean[] getNewDirections() {
        return newDirections;
    }

    public void setNewDirections(boolean[] newDirections) {
        this.newDirections = newDirections;
    }

    public void setDirections(boolean[] directions) {
        this.directions = directions;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public long getRandom() {
        return random;
    }

    public void setRandom(long random) {
        this.random = random;
    }

    public static Cell[][] initializeCells(int horizontal, int vertical, String dynamicFile) throws FileNotFoundException {

        Cell[][] cells = new Cell[vertical][horizontal];
        for (int i = 0; i < vertical; i++) {
            for (int j = 0; j < horizontal; j++) {
                // Create a solid border
                if(i == 0 || j == 0 || i == vertical - 1 || j == horizontal - 1)
                    cells[i][j] = new Cell(true);
                // Create a wall in the middle
                else if(j == horizontal / 2 && (i < vertical / 2 - 25 || i >= vertical / 2 + 25))
                    cells[i][j] = new Cell(true);
                else
                    cells[i][j] = new Cell(false);
            }
        }
        readDynamic(cells, dynamicFile);

        return cells;
    }

    public static void readDynamic(Cell[][] cells, String dynamicFile) throws FileNotFoundException {
        File file = new File(dynamicFile);
        Scanner sc = new Scanner(file);

        //Skip first line
        sc.nextLine();
        while (sc.hasNextLine()){
            int i = sc.nextInt();
            int j = sc.nextInt();
            cells[i][j].createParticle(sc.nextInt());
        }

        sc.close();
    }


}
