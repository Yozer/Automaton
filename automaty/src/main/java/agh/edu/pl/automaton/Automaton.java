package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.Map;
import java.util.Set;

/**
 * Created by Dominik on 2015-11-25.
 */
public abstract class Automaton
{
    private Map<CellCoordinates, CellState> cells;
    private CellNeighborhood neighborhoodStrategy;
    private CellStateFactory stateFactory;

    public Automaton nextState();
    public void insertStructure(Map<? extends CellCoordinates, ? extends CellState> structure);
    public Cell.CellIterator cellIterator()
    {
        return new Cell.CellIterator(cells);
    }

    protected abstract Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood);
    protected abstract CellCoordinates initialCoordinates(CellCoordinates coordinates);
    protected abstract CellCoordinates nextCoordinates(CellCoordinates coordinates);
    protected abstract CellState nextCellState(CellState currentState, Set<Cell> neighborsStates);

    private Set<Cell> mapCoordinates(Set<CellCoordinates> coordinates)
    {
        return null;
    }
}

