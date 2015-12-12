package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class Automaton implements Iterable<Cell>
{
    private List<Cell> cells;
    private List<Cell> cellsBackBuffer;

    private CellNeighborhood neighborhoodStrategy;
    private CellStateFactory stateFactory;

    private int cellCount;
    private final int processorsCount = Runtime.getRuntime().availableProcessors();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(processorsCount);

    protected Automaton(CellNeighborhood neighborhoodStrategy, CellStateFactory stateFactory, int cellCount)
    {
        this.neighborhoodStrategy = neighborhoodStrategy;
        this.stateFactory = stateFactory;
        this.cellCount = cellCount;
    }

    public int nextState()
    {
        AtomicInteger aliveCount = new AtomicInteger(0);

        for (final Cell cell : cells) {
            threadPool.submit((Runnable) () -> {
                List<CellCoordinates> neighbors = neighborhoodStrategy.cellNeighbors(cell.getCoords());
                CellState newState = nextCellState(cell, neighbors);

                if(cellIsAlive(newState))
                    aliveCount.getAndIncrement();
                setBackBufferCellState(cell, newState);
            });
        }

        swapBuffer();
        return aliveCount.get();
    }

    private void swapBuffer()
    {
        List<Cell> tmp = cellsBackBuffer;
        cellsBackBuffer = cells;
        cells = tmp;
    }
    private void setBackBufferCellState(Cell cell, CellState newState)
    {
        Cell backBufferCell = cellsBackBuffer.get(getCoordsIndex(cell.getCoords()));
        backBufferCell.isChanged(backBufferCell.getState() != newState);
        backBufferCell.setState(newState);
    }

    public void insertStructure(Map<? extends CellCoordinates, ? extends CellState> structure)
    {
        for(CellCoordinates coords : structure.keySet())
            cells.set(getCoordsIndex(coords), new Cell(structure.get(coords), coords));
    }

    protected abstract CellState nextCellState(Cell cell, List<CellCoordinates> neighborsStates);
    protected abstract boolean hasNextCoordinates(CellCoordinates coords);
    protected abstract CellCoordinates initialCoordinates();
    protected abstract CellCoordinates nextCoordinates();
    protected abstract int getCoordsIndex(CellCoordinates coord);
    protected abstract boolean cellIsAlive(CellState state);

    protected CellState getCellStateByCoordinates(CellCoordinates coordinates)
    {
        return cells.get(getCoordsIndex(coordinates)).getState();
    }

    protected void initAutomaton()
    {
        // initialize lists
        CellCoordinates current = initialCoordinates();
        cells = new ArrayList<>(cellCount);
        cellsBackBuffer = new ArrayList<>(cellCount);
        while (cells.size() < cellCount)
        {
            cells.add(null);
            cellsBackBuffer.add(null);
        }

        // iterate over all coordinates and get initial state for each cell
        while(hasNextCoordinates(current))
        {
            current = nextCoordinates();
            CellState initialState = stateFactory.initialState(current);

            int cellIndex = getCoordsIndex(current);
            cells.set(cellIndex, new Cell(initialState, current));
            cellsBackBuffer.set(cellIndex, new Cell(initialState, current));
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
            return cells.get(getCoordsIndex(currentCoords));
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("remove method not implemented");
        }
    }*/
}

