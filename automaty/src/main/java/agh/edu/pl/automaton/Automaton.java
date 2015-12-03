package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.HashSet;
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
    public CellIterator cellIterator()
    {
        return new CellIterator();
    }

    protected abstract Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood);
    protected abstract boolean hasNextCoordinates(CellCoordinates coords);
    protected abstract CellCoordinates initialCoordinates(CellCoordinates coordinates);
    protected abstract CellCoordinates nextCoordinates(CellCoordinates coordinates);
    protected abstract CellState nextCellState(CellState currentState, Set<Cell> neighborsStates);

    private Set<Cell> mapCoordinates(Set<CellCoordinates> coordinates)
    {
        Set<Cell> cellSet = new HashSet<Cell>(coordinates.size());
        for(CellCoordinates coords : coordinates)
        {
            cellSet.add(new Cell(cells.get(coords), coords));
        }
        return cellSet;
    }

    private class CellIterator implements java.util.Iterator<Cell>
    {
        // TODO WTF? Nie powinno być CellState?
        private CellCoordinates currentCoord;

        public CellIterator()
        {
            // TODO jakie niby mam przekazać jak chcę dostać initial?
            currentCoord = initialCoordinates(null);
        }

        public boolean hasNext()
        {
            return hasNextCoordinates(currentCoord);
        }
        public Cell next()
        {
            if(!hasNext())
                return null;

            currentCoord = nextCoordinates(currentCoord);
            return new Cell(cells.get(currentCoord), currentCoord);
        }
        public void setState(CellState state)
        {
            cells.put(currentCoord, state);
        }
    }
}

