package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public abstract class Automaton implements Iterable<Cell>
{
    private Map<CellCoordinates, CellState> cells;
    private CellNeighborhood neighborhoodStrategy;
    private CellStateFactory stateFactory;

    public Automaton nextState()
    {
        return null;
    }
    public void insertStructure(Map<? extends CellCoordinates, ? extends CellState> structure)
    {
        cells.putAll(structure);
    }

    protected abstract Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood);
    protected abstract boolean hasNextCoordinates(CellCoordinates coords);
    protected abstract CellCoordinates initialCoordinates();
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

    @Override
    public Iterator<Cell> iterator()
    {
        return new CellIterator();
    }

    private class CellIterator implements java.util.Iterator<Cell>
    {
        private CellCoordinates currentCoord;

        public CellIterator()
        {
            currentCoord = initialCoordinates();
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

