package agh.edu.pl.automaton;


import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

/**
 * Represents one dimensional automaton.
 * @author Dominik Baran
 * @see agh.edu.pl.automaton.automata.ElementaryAutomaton
 */
public abstract class Automaton1Dim extends Automaton {
    private final int size;
    private Coords1D iteratorCurrentCoordinates;
    /**
     * @param size Size of one dimensional automaton
     * @param neighborhoodStrategy Neighborhood for automaton
     * @param stateFactory State factory for initial state of each cell in automaton
     */
    protected Automaton1Dim(int size, CellNeighborhood neighborhoodStrategy, CellStateFactory stateFactory) {
        super(neighborhoodStrategy, stateFactory, size);
        this.size = size;
    }
    /** {@inheritDoc}
     */
    @Override
    protected CellCoordinates initialCoordinates() {
        iteratorCurrentCoordinates = new Coords1D(-1);
        return iteratorCurrentCoordinates;
    }
    /** {@inheritDoc}
     */
    @Override
    protected boolean hasNextCoordinates(CellCoordinates coords) {
        Coords1D coords2D = (Coords1D) coords;
        return coords2D.getX() < size - 1;
    }
    /** {@inheritDoc}
     */
    @Override
    protected CellCoordinates nextCoordinates() {
        int x = iteratorCurrentCoordinates.getX() + 1;
        iteratorCurrentCoordinates = new Coords1D(x);
        return iteratorCurrentCoordinates;
    }
    /** {@inheritDoc}
     */
    @Override
    protected int getCoordsUniqueIndex(CellCoordinates coord) {
        Coords1D coords1D = ((Coords1D) coord);
        return coords1D.getX();
    }

}

