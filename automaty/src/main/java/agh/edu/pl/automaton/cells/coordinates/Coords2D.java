package agh.edu.pl.automaton.cells.coordinates;

/**
 * Created by Dominik on 2015-11-29.
 */
public class Coords2D implements CellCoordinates {
    private final int x;
    private final int y;

    public Coords2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Coords2D))
            return false;
        Coords2D c = ((Coords2D) obj);
        return x == c.getX() && y == c.getY();
    }

    @Override
    public String toString() {
        return "X: " + x + " Y: " + y;
    }

}
