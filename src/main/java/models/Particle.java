package models;

import java.util.Objects;

public class Particle {
    private final int id;

    private static int idCounter = 0;
    private final Direction direction;

    public Particle(Direction direction) {
        this.direction = direction;
        this.id = idCounter++;
    }

    public int getId() {
        return id;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return id == particle.id;
    }
}
