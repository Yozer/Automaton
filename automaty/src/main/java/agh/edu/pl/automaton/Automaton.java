package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.*;


public abstract class Automaton implements Iterable<Cell>
{
    private List<Cell> cells;
    private CellNeighborhood neighborhoodStrategy;
    private CellStateFactory stateFactory;

    private int cellCount;

    protected Automaton(CellNeighborhood neighborhoodStrategy, CellStateFactory stateFactory, int cellCount)
    {
        this.neighborhoodStrategy = neighborhoodStrategy;
        this.stateFactory = stateFactory;
        this.cellCount = cellCount;
    }

    public Automaton nextState()
    {
        Automaton result = newInstance(stateFactory, neighborhoodStrategy);

        for(Cell cell : this)
        {
            List<CellCoordinates> neighbors = neighborhoodStrategy.cellNeighbors(cell.getCoords());

            CellState newState = nextCellState(cell, neighbors);
            result.setCellState(cell.getCoords(), newState);
        }

        return result;
    }

    public void insertStructure(Map<? extends CellCoordinates, ? extends CellState> structure)
    {
        for(CellCoordinates coords : structure.keySet())
            cells.set(getCoordsIndex(coords), new Cell(structure.get(coords), coords));
    }

    protected abstract Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood);
    protected abstract CellState nextCellState(Cell cell, List<CellCoordinates> neighborsStates);
    protected abstract boolean hasNextCoordinates(CellCoordinates coords);
    protected abstract CellCoordinates initialCoordinates();
    protected abstract CellCoordinates nextCoordinates();
    protected abstract int getCoordsIndex(CellCoordinates coord);

    protected CellState getCellByCoordinates(CellCoordinates coordinates)
    {
        return cells.get(getCoordsIndex(coordinates)).getState();
    }

    private void setCellState(CellCoordinates coords, CellState newState)
    {
        cells.set(getCoordsIndex(coords), new Cell(newState, coords));
    }

    protected void initAutomaton()
    {
        CellCoordinates current = initialCoordinates();
        cells = new ArrayList<>(cellCount);
        while (cells.size() < cellCount)
            cells.add(null);

        while(hasNextCoordinates(current))
        {
            current = nextCoordinates();
            CellState initialState = stateFactory.initialState(getCoordsIndex(current));
            setCellState(current, initialState);
        }
    }

    @Override
    public Iterator<Cell> iterator()
    {
        return cells.iterator();
    }

    /*private class CellIterator implements java.util.Iterator<Cell>
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
    }*/
}

