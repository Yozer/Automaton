package agh.edu.pl.automaton.cells.coordinates;

/**
 * Represents coordinates in one dimensional space.
 * {@code Coords1D} is immutable.
 * @author Dominik Baran
 * @see CellCoordinates
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
    /**
     * @return Returns formatted coordinates.
     */
    @Override
    public String toString() {
        return "X: " + x;
    }
}
