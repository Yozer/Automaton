package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

/**
 * {@code VonNeumannNeighborhood} calculates neighbors using von Neumann rule for given coordinates.
 * @see CellCoordinates
 * @author Dominik Baran
 * @see <a href="http://mathworld.wolfram.com/vonNeumannNeighborhood.html">von Neumann Neighborhood</a>
 */
public class VonNeumannNeighborhood implements CellNeighborhood {
    private final int radius;
    private final boolean wrap;
    private final int width;
    private final int height;

    /**
     * Creates {@code VonNeumannNeighborhood} instance.
     * @param radius Neighborhood radius
     * @param wrap Determines if coordinates should be wrapped
     * @param width Plane width
     * @param height Plane height
     */
    public VonNeumannNeighborhood(int radius, boolean wrap, int width, int height) {
        this.radius = radius;
        this.wrap = wrap;
        this.width = width;
        this.height = height;
    }
    /** {@inheritDoc}
     */
    @Override
    public NeighborhoodList createArray() {
        return new NeighborhoodList(2 * radius * (radius + 1));
    }
    /** {@inheritDoc}
     */
    @SuppressWarnings({"Duplicates", "SuspiciousNameCombination"})
    @Override
    public NeighborhoodList cellNeighbors(CellCoordinates cell, NeighborhoodList neighborhoodList) {
        neighborhoodList.clear();
        Coords2D initialCoords = (Coords2D) cell;

        int xOriginal = initialCoords.getX();
        int yOriginal = initialCoords.getY();
        int limitX = xOriginal + radius + 1;
        int limitY = yOriginal + radius + 1;
        int xN;
        int yN;

        for (int x = xOriginal - radius; x < limitX; x++) {
            for (int y = yOriginal - radius; y < limitY; y++) {
                if (Math.abs(x - xOriginal) + Math.abs(y - yOriginal) <= radius && (x != xOriginal || y != yOriginal)) {
                    if (wrap) {
                        xN = x;
                        yN = y;
                        if (xN < 0 || xN >= width)
                            xN = Math.floorMod(xN, width);
                        if (yN < 0 || yN >= height)
                            yN = Math.floorMod(yN, height);
                        neighborhoodList.push(yN * width + xN);
                    } else if (x >= 0 && x < width && y >= 0 && y < height) {
                        neighborhoodList.push(y * width + x);
                    }
                }
            }
        }

        return neighborhoodList;
    }

    public int getHeight() {
        return height;
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

}
