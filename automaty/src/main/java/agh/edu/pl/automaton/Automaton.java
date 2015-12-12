package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public abstract class Automaton implements Iterable<Cell>
{
    private List<Cell> cells;
    private List<Cell> cellsBackBuffer;

    private int[] changeList;
    private int[] changeListBackBuffer;
    private byte[] changeListSet;
    private int changeListSize;
    private int changeListBackbufferSize;

    private CellNeighborhood neighborhoodStrategy;
    private CellStateFactory stateFactory;

    private final int cellCount;
    private final int processorsCount = Runtime.getRuntime().availableProcessors();
    private final ForkJoinPool threadPool = new ForkJoinPool(processorsCount);

    protected Automaton(CellNeighborhood neighborhoodStrategy, CellStateFactory stateFactory, int cellCount)
    {
        this.neighborhoodStrategy = neighborhoodStrategy;
        this.stateFactory = stateFactory;
        this.cellCount = cellCount;
    }

    public int nextState()
    {
        AtomicInteger aliveCount = new AtomicInteger(0);
        int step = (int) (changeListSize / ((float) processorsCount));

        for(int i = 0; i < processorsCount; ++i)
        {
            int from = i * step;
            int to = i == processorsCount - 1 ? changeListSize : (i + 1) * step;

            threadPool.execute(() ->
            {
                for(int j = from; j < to; j++)
                {
                    int cellIndex = changeList[j];
                    Cell cell = cells.get(cellIndex);

                    List<CellCoordinates> neighbors = neighborhoodStrategy.cellNeighbors(cell.getCoords());
                    CellState newState = nextCellState(cell, neighbors);

                    if(cellIsAlive(newState))
                        aliveCount.getAndIncrement();
                    Cell backbufferCell = setBackBufferCellState(cell, newState);
                    if(backbufferCell.hasChanged())
                    {
                        synchronized (changeListSet)
                        {
                            if(changeListSet[cellIndex] == 0)
                            {
                                changeListBackBuffer[changeListBackbufferSize++] = cellIndex;
                                changeListSet[cellIndex] = 1;
                            }
                            for(CellCoordinates coordinates : neighbors)
                            {
                                int index = getCoordsIndex(coordinates);
                                if(changeListSet[index] == 0)
                                {
                                    changeListBackBuffer[changeListBackbufferSize++] = index;
                                    changeListSet[index] = 1;
                                }
                            }
                        }
                    }
                }
            });
        }

        /*for (final Cell cell : cells)
        {
            /*threadPool.submit((Runnable) () -> {
                List<CellCoordinates> neighbors = neighborhoodStrategy.cellNeighbors(cell.getCoords());
                CellState newState = nextCellState(cell, neighbors);

                if(cellIsAlive(newState))
                    aliveCount.getAndIncrement();
                setBackBufferCellState(cell, newState);
            });*/
            /*List<CellCoordinates> neighbors = neighborhoodStrategy.cellNeighbors(cell.getCoords());
            CellState newState = nextCellState(cell, neighbors);

            if(cellIsAlive(newState))
                aliveCount.getAndIncrement();
            setBackBufferCellState(cell, newState);*/
        //}
        /*List<Integer> gunwo = new ArrayList<>();
        for(int x : changeListBackBuffer)
            if(x != 0)
                gunwo.add(x);
        gunwo = gunwo.stream().sorted().collect(Collectors.toList());*/
        threadPool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        swapBuffer();
        return aliveCount.get();
    }

    private void swapBuffer()
    {
        List<Cell> tmp = cellsBackBuffer;
        cellsBackBuffer = cells;
        cells = tmp;

        changeListSize = changeListBackbufferSize;
        changeListBackbufferSize = 0;

        int[] x = changeList;
        changeList = changeListBackBuffer;
        changeListBackBuffer = x;

        for(int i = 0; i < cellCount; i++)
            changeListSet[i] = 0;
    }
    private Cell setBackBufferCellState(Cell cell, CellState newState)
    {
        Cell backBufferCell = cellsBackBuffer.get(getCoordsIndex(cell.getCoords()));
        backBufferCell.isChanged(backBufferCell.getState() != newState);
        backBufferCell.setState(newState);
        return backBufferCell;
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
        changeList = new int[cellCount];
        changeListBackBuffer = new int[cellCount];
        changeListSet = new byte[cellCount];

        while (cells.size() < cellCount)
        {
            cells.add(null);
            cellsBackBuffer.add(null);
        }

        // iterate over all coordinates and get initial state for each cell
        changeListSize = 0;
        while(hasNextCoordinates(current))
        {
            current = nextCoordinates();
            CellState initialState = stateFactory.initialState(current);

            int cellIndex = getCoordsIndex(current);
            cells.set(cellIndex, new Cell(initialState, current));
            cellsBackBuffer.set(cellIndex, new Cell(initialState, current));

            changeList[changeListSize] = cellIndex;
            changeListSet[changeListSize++] = 0;
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

