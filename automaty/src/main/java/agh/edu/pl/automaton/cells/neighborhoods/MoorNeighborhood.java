package agh.edu.pl.automaton.cells.neighborhoods;


import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

public class MoorNeighborhood implements CellNeighborhood {
    private final int radius;
    private final boolean wrap;
    private final int width;
    private final int height;

    public MoorNeighborhood(int radius, boolean wrap, int width, int height) {
        this.radius = radius;
        this.wrap = wrap;
        this.width = width;
        this.height = height;
    }

    @Override
    public NeighborhoodArray createArray() {
        return new NeighborhoodArray((2 * radius + 1) * (2 * radius + 1) - 1);
    }

    @SuppressWarnings({"SuspiciousNameCombination", "Duplicates"})
    @Override
    public NeighborhoodArray cellNeighbors(CellCoordinates cell, NeighborhoodArray result) {
        result.clear();
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
                        result.push(yN * width + xN);
                    } else if (x >= 0 && x < width && y >= 0 && y < height) {
                        result.push(y * width + x);
                    }
                }
            }
        }

        return result;
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

