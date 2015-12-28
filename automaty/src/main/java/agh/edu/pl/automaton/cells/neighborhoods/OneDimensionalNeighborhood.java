package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;


public class OneDimensionalNeighborhood implements CellNeighborhood {
    private static final int radius = 1;
    private final boolean wrap;
    private final int width;

    public OneDimensionalNeighborhood(boolean wrap, int width) {
        this.wrap = wrap;
        this.width = width;
    }

    @Override
    public NeighborhoodArray cellNeighbors(CellCoordinates cell, NeighborhoodArray result) {
        result.clear();
        Coords1D initialCoords = (Coords1D) cell;

        int originalX = initialCoords.getX();
        if (originalX - 1 >= 0)
            result.push(originalX - 1);
        else if (wrap)
            result.push(width - 1);

        if (originalX + 1 < width)
            result.push(originalX + 1);
        else if (wrap)
            result.push(0);

        return result;
    }

    public int getWidth() {
        return width;
    }

    public boolean isWrap() {
        return wrap;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public NeighborhoodArray createArray() {
        return new NeighborhoodArray(2 * radius);
    }
}
