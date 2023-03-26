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
        newDirections[direction] = true;
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


}
