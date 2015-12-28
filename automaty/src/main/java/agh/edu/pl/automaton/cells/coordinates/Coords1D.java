package agh.edu.pl.automaton.cells.coordinates;

/**
 * Created by Dominik on 2015-11-29.
 */
public class Coords1D implements CellCoordinates {
    private final int x;

    public Coords1D(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    @Override
    public int hashCode() {
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Coords1D && ((Coords1D) obj).getX() == x;
    }

    @Override
    public String toString() {
        return "X: " + x;
    }
}
