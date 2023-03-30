package models;

import java.util.List;

public enum Direction {
    A(1/2.0,1/2.0),
    B (1,0),
    C(1/2.0,-1/2.0),
    D(-1/2.0,-1/2.0),
    E (-1,0),
    F(-1/2.0,1/2.0);

    private final double x;

    private final double y;


    Direction(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Direction rotate(int steps) {
        if( (steps != 1 && steps != 2 && steps != 3) )
            throw new IllegalArgumentException();

        int length = Direction.values().length;
        int index = this.ordinal();
        int newIndex = (index + steps) % length;

        return Direction.values()[newIndex];
    }


    static double momentumX(List<Direction> directions){
        double sum = 0;
        for (Direction direction : directions) {
            sum += direction.x;
        }
        return sum;
    }

    static double momentumY(List<Direction> directions){
        double sum = 0;
        for (Direction direction : directions) {
            sum += direction.y;
        }
        return sum;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
