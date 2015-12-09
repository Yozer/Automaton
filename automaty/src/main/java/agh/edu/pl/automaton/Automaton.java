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

    @Override
    public Iterator<Cell> iterator()
    {
        return new CellIterator(cells.keySet().iterator());
    }

    private class CellIterator implements java.util.Iterator<Cell>
    {
        private Iterator<CellCoordinates> iterator;

        public CellIterator(Iterator<CellCoordinates> iterator)
        {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        @Override
        public Cell next()
        {
            if(!hasNext())
            {
                throw new NoSuchElementException("There is no next cell");
            }

            CellCoordinates currentCoord = iterator.next();
            return new Cell(cells.get(currentCoord), currentCoord);
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("remove method not implemented");
        }
    }
}

