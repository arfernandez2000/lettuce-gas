package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Cell {

    private Particle[] particles = new Particle[6];
    private Particle[] nextParticles = new Particle[6];

    private boolean[] directions = new boolean[6];
    private boolean[] newDirections = new boolean[6];
    private boolean solid;
    private int random;

    public Cell(boolean solid) {
        this.solid = solid;
        this.random = (int) Math.round(Math.random());
    }

    public void createParticle(Particle particle) {
        particles[particle.getDirection().ordinal()] = particle;
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

    public int getRandom() {
        return random;
    }

    public void setRandom(int random) {
        this.random = random;
    }

    public Particle[] getParticles() {
        return particles;
    }

    public void setParticles(Particle[] particles) {
        this.particles = particles;
    }

    public Particle[] getNextParticles() {
        return nextParticles;
    }

    public void setNextParticles(Particle[] nextParticles) {
        this.nextParticles = nextParticles;
    }

    public static Cell[][] initializeCells(int horizontal, int vertical, int d, String dynamicFile) throws FileNotFoundException {

        Cell[][] cells = createGrid(horizontal, vertical, d);
        readDynamic(cells, dynamicFile);

        return cells;
    }

    public static Cell[][] createGrid (int horizontal, int vertical, int d){

        Cell[][] cells = new Cell[vertical][horizontal];
        for (int i = 0; i < vertical; i++) {
            for (int j = 0; j < horizontal; j++) {
                // Create a solid border
                if(i == 0 || j == 0 || i == vertical - 1 || j == horizontal - 1)
                    cells[i][j] = new Cell(true);
                    // Create a wall in the middle
                else if(j == horizontal / 2 && (i < vertical / 2 - d/2 || i >= vertical / 2 + d/2))
                    cells[i][j] = new Cell(true);
                else
                    cells[i][j] = new Cell(false);
            }
        }

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
            Particle particle = new Particle(Direction.values()[sc.nextInt()]);
            cells[i][j].createParticle(particle);
        }

        sc.close();
    }

    public Particle[] collision() {
        if(this.solid) {
            return rotate(3);
        }
        List<Direction> directions = new ArrayList<>();
        for (Particle particle: particles) {
            if(particle != null)
                directions.add(particle.getDirection());
        }

        double xMomentum = Direction.momentumX(directions);
        double yMomentum = Direction.momentumY(directions);
        int size = Arrays.stream(this.particles).filter(Objects::nonNull).toArray().length;

        if((xMomentum != 0 || yMomentum != 0) || size == 6 || size == 0) {
            return this.particles;
        } else {
            int random = size == 3 ? 0 : this.random;
            return rotate(1 + random);
        }
    }

    private Particle[] rotate(int random) {
        Particle[] rotatedParticles = new Particle[6];
        for (int i = 0; i < rotatedParticles.length; i++) {
            if(particles[i] != null)
                rotatedParticles[i] = new Particle(particles[i].getDirection().rotate(random));
        }
        return rotatedParticles;
    }

}
