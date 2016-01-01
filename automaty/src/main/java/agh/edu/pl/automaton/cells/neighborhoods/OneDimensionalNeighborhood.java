package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;

/**
 * This class calculates left and right neighbors for given {@code CellCoordinates}
 * @author Dominik Baran
 * @see CellNeighborhood
 */
public class OneDimensionalNeighborhood implements CellNeighborhood {
    private static final int radius = 1;
    private final boolean wrap;
    private final int width;

    /**
     *
     * @param wrap Determines if coordinates should be wrapped
     * @param width Plane width
     */
    public OneDimensionalNeighborhood(boolean wrap, int width) {
        this.wrap = wrap;
        this.width = width;
    }
    /** {@inheritDoc}
     */
    @Override
    public NeighborhoodList cellNeighbors(CellCoordinates cell, NeighborhoodList neighborhoodList) {
        neighborhoodList.clear();
        Coords1D initialCoords = (Coords1D) cell;

        int originalX = initialCoords.getX();
        if (originalX - 1 >= 0)
            neighborhoodList.push(originalX - 1);
        else if (wrap)
            neighborhoodList.push(width - 1);

        if (originalX + 1 < width)
            neighborhoodList.push(originalX + 1);
        else if (wrap)
            neighborhoodList.push(0);

        return neighborhoodList;
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
    /** {@inheritDoc}
     */
    @Override
    public NeighborhoodList createArray() {
        return new NeighborhoodList(2 * radius);
    }
}
