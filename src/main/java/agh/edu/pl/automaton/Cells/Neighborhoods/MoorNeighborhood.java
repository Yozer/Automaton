package agh.edu.pl.automaton.cells.neighborhoods;


import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

/**
 * {@code MoorNeighborhood} calculates neighbors using Moore rule for given coordinates.
 * @see CellCoordinates
 * @author Dominik Baran
 * @see <a href="http://mathworld.wolfram.com/MooreNeighborhood.html">Moore Neighborhood</a>
 */
public class MoorNeighborhood implements CellNeighborhood {
    private final int radius;
    private final boolean wrap;
    private final int width;
    private final int height;

    /**
     * Creates {@code MooreNeighborhood} instance.
     * @param radius Neighborhood radius
     * @param wrap Determines if coordinates should be wrapped
     * @param width Plane width
     * @param height Plane height
     */
    public MoorNeighborhood(int radius, boolean wrap, int width, int height) {
        this.radius = radius;
        this.wrap = wrap;
        this.width = width;
        this.height = height;
    }
    /** {@inheritDoc}
     */
    @Override
    public NeighborhoodList createArray() {
        return new NeighborhoodList((2 * radius + 1) * (2 * radius + 1) - 1);
    }
    /** {@inheritDoc}
     */
    @SuppressWarnings({"SuspiciousNameCombination", "Duplicates"})
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
                if (x != xOriginal || y != yOriginal) {
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

