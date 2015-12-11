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
    }

    public Automaton nextState()
    {
        Automaton result = newInstance(stateFactory, neighborhoodStrategy);

        for(Cell cell : this)
        {
            List<CellCoordinates> neighbors = neighborhoodStrategy.cellNeighbors(cell.getCoords());
            List<Cell> mappedNeighbors = mapCoordinates(neighbors);

            CellState newState = nextCellState(cell, mappedNeighbors);
            result.setCellState(cell.getCoords(), newState);
        }

        return result;
    }

    public void insertStructure(Map<? extends CellCoordinates, ? extends CellState> structure)
    {
        cells.putAll(structure);
    }

    protected abstract Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood);
    protected abstract CellState nextCellState(Cell cell, List<Cell> neighborsStates);
    protected abstract boolean hasNextCoordinates(CellCoordinates coords);
    protected abstract CellCoordinates initialCoordinates();
    protected abstract CellCoordinates nextCoordinates();

    private void setCellState(CellCoordinates coords, CellState newState)
    {
        cells.put(coords, newState);
    }
    private List<Cell> mapCoordinates(List<CellCoordinates> coordinates)
    {
        List<Cell> cellSet = new ArrayList<>(coordinates.size());
        for(CellCoordinates coords : coordinates)
        {
            cellSet.add(new Cell(cells.get(coords), coords));
        }
        return cellSet;
    }
    protected void initAutomaton()
    {
        CellCoordinates current = initialCoordinates();
        cells = new HashMap<>();
        while(hasNextCoordinates(current))
        {
            current = nextCoordinates();
            CellState initialState = stateFactory.initialState(current);
            setCellState(current, initialState);
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

            currentCoords = nextCoordinates();
            return new Cell(cells.get(currentCoords), currentCoords);
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("remove method not implemented");
        }
    }
}

