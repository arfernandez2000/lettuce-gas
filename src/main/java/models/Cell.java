package models;

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

    public static Cell[][] initializeCells(int horizontal, int vertical) {
        Cell[][] cells = new Cell[vertical][horizontal];
        for (int i = 0; i < vertical; i++) {
            for (int j = 0; j < horizontal; j++) {
                cells[i][j] = new Cell(false);
            }
        }

        cells[0][0].createParticle(1);
        cells[0][2].createParticle(4);
        cells[9][0].createParticle(0);
        cells[8][2].createParticle(0);
        cells[8][1].createParticle(0);
        cells[6][5].createParticle(0);
        cells[5][5].createParticle(0);

        return cells;
    }


}
