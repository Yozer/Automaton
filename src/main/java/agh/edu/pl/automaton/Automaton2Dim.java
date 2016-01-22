package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

/**
 * Represents two dimensional automaton.
 * @author Dominik Baran
 * @see agh.edu.pl.automaton.automata.GameOfLife
 * @see agh.edu.pl.automaton.automata.WireWorld
 * @see agh.edu.pl.automaton.automata.langton.LangtonAnt
 * @see agh.edu.pl.automaton.automata.QuadLife
 */
public abstract class Automaton2Dim extends Automaton {
    private final int width;
    private final int height;
    private Coords2D iteratorCurrentCoordinates;
    /**
     * @param width Automaton width
     * @param height Automaton height
     * @param cellNeighborhood Neighborhood for automaton
     * @param cellStateFactory State factory for initial state of each cell in automaton
     */
    protected Automaton2Dim(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood) {
        super(cellNeighborhood, cellStateFactory, width * height);
        this.width = width;
        this.height = height;
    }
    /** {@inheritDoc}
     */
    @Override
    protected CellCoordinates initialCoordinates() {
        iteratorCurrentCoordinates = new Coords2D(-1, 0);
        return iteratorCurrentCoordinates;
    }
    /** {@inheritDoc}
     */
    @Override
    protected boolean hasNextCoordinates(CellCoordinates coords) {
        Coords2D coords2D = (Coords2D) coords;
        return coords2D.getX() < width - 1 || coords2D.getY() < height - 1;
    }
    /** {@inheritDoc}
     */
    @Override
    protected CellCoordinates nextCoordinates() {
        int x = iteratorCurrentCoordinates.getX() + 1;
        int y = iteratorCurrentCoordinates.getY();

        if (x >= width) {
            y++;
            x = 0;
        }

        iteratorCurrentCoordinates = new Coords2D(x, y);
        return iteratorCurrentCoordinates;
    }
    /** {@inheritDoc}
     */
    @Override
    protected int getCoordsUniqueIndex(CellCoordinates coord) {
        Coords2D coords2D = ((Coords2D) coord);
        return coords2D.getY() * getWidth() + coords2D.getX();
    }

    protected int getWidth() {
        return width;
    }

    protected int getHeight() {
        return height;
    }
}

