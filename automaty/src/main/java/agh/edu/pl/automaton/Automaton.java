package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.*;


public abstract class Automaton implements Iterable<Cell>
{
    private Map<CellCoordinates, CellState> cells;
    private CellNeighborhood neighborhoodStrategy;
    private CellStateFactory stateFactory;

    protected Automaton(CellNeighborhood neighborhoodStrategy, CellStateFactory stateFactory)
    {
        this.neighborhoodStrategy = neighborhoodStrategy;
        this.stateFactory = stateFactory;

        initAutomaton();
    }

    public Automaton nextState()
    {
        Automaton result = newInstance(stateFactory, neighborhoodStrategy);

        for(Cell cell : this)
        {
            Set<CellCoordinates> neighbors = neighborhoodStrategy.cellNeighbors(cell.getCoords());
            Set<Cell> mappedNeighbors = mapCoordinates(neighbors);

            CellState newState = nextCellState(cell.getState(), mappedNeighbors);
            result.setCellState(cell.getCoords(), newState);
        }

        return result;
    }

    public void insertStructure(Map<? extends CellCoordinates, ? extends CellState> structure)
    {
        cells.putAll(structure);
    }

    protected abstract Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood);
    protected abstract CellState nextCellState(CellState currentState, Set<Cell> neighborsStates);
    protected abstract boolean hasNextCoordinates(CellCoordinates coords);
    protected abstract CellCoordinates initialCoordinates();
    protected abstract CellCoordinates nextCoordinates();

    private void setCellState(CellCoordinates coords, CellState newState)
    {
        cells.put(coords, newState);
    }
    private Set<Cell> mapCoordinates(Set<CellCoordinates> coordinates)
    {
        Set<Cell> cellSet = new HashSet<Cell>(coordinates.size());
        for(CellCoordinates coords : coordinates)
        {
            cellSet.add(new Cell(cells.get(coords), coords));
        }
        return cellSet;
    }
    private void initAutomaton()
    {
        for(Cell cell : this)
        {
            CellState initialState = stateFactory.initialState(cell.getCoords());
            setCellState(cell.getCoords(), initialState);
        }
    }

    @Override
    public Iterator<Cell> iterator()
    {
        return new CellIterator();
    }

    private class CellIterator implements java.util.Iterator<Cell>
    {
        private CellCoordinates currentCoords;

        public CellIterator()
        {
            currentCoords = initialCoordinates();
        }

        @Override
        public boolean hasNext()
        {
            return hasNextCoordinates(currentCoords);
        }

        @Override
        public Cell next()
        {
            if(!hasNextCoordinates(currentCoords))
            {
                throw new NoSuchElementException("There is no next cell");
            }

            CellCoordinates currentCoord = nextCoordinates();
            return new Cell(cells.get(currentCoord), currentCoord);
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("remove method not implemented");
        }
    }
}

