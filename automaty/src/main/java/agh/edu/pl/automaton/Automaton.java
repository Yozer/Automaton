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


public abstract class Automaton implements Iterable<Cell>
{
    private Cell[] cells;
    private Cell[] cellsBackBuffer;
    private int[] changeList;
    private int[] changeListBackBuffer;
    private byte[] changeListSet;
    private byte[] changeListSetBackBuffer;

    private int changeListSize;
    private int changeListBackbufferSize;

    private CellNeighborhood neighborhoodStrategy;
    private CellStateFactory stateFactory;

    private final int cellCount;
    private final int processorsCount;
    private final ForkJoinPool threadPool;
    private final AtomicInteger aliveCount = new AtomicInteger(0);

    private boolean isInitiated = false;


    protected Automaton(CellNeighborhood neighborhoodStrategy, CellStateFactory stateFactory, int cellCount)
    {
        this.neighborhoodStrategy = neighborhoodStrategy;
        this.stateFactory = stateFactory;
        this.cellCount = cellCount;

        cells = new Cell[cellCount];
        cellsBackBuffer = new Cell[cellCount];
        changeList = new int[cellCount];
        changeListBackBuffer = new int[cellCount];
        changeListSet = new byte[cellCount];
        changeListSetBackBuffer = new byte[cellCount];

        processorsCount = Runtime.getRuntime().availableProcessors() == 1 ? 1 : Runtime.getRuntime().availableProcessors() - 1;
        threadPool = new ForkJoinPool(processorsCount);
    }

    public int getAliveCount()
    {
        return aliveCount.get();
    }

    public void beginCalculatingNextState()
    {
        if(!isInitiated)
        {
            initAutomaton();
            isInitiated = true;
        }

        int step = getStep(processorsCount, changeListSize);

        for(int i = 0; i < processorsCount; ++i)
        {
            int from = i * step;
            int to = i == processorsCount - 1 ? changeListSize : (i + 1) * step;

            threadPool.execute(() ->
            {
                simulateSlice(from, to);
            });
        }

        threadPool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
    public void endCalculatingNextState()
    {
        swapBuffers();
    }
    public void insertStructure(Map<? extends CellCoordinates, ? extends CellState> structure)
    {
        if (!isInitiated)
        {
            initAutomaton();
            isInitiated = true;
        }

        for (CellCoordinates coords : structure.keySet())
        {
            int index = getCoordsIndex(coords);
            CellState newState = structure.get(coords);

            if (cells[index].getState() != newState)
            {
                if (cellIsAlive(newState))
                    aliveCount.incrementAndGet();
                else
                    aliveCount.decrementAndGet();
            }
            cells[index].setState(newState);

            if (changeListSet[index] == 0)
            {
                changeList[changeListSize++] = index;
                changeListSet[index] = 1;
            }
            for (CellCoordinates coordinates : neighborhoodStrategy.cellNeighbors(coords))
            {
                int indexN = getCoordsIndex(coordinates);
                if (changeListSet[indexN] == 0)
                {
                    changeList[changeListSize++] = indexN;
                    changeListSet[indexN] = 1;
                }
            }
        }
    }

    public void insertStructure(List<Cell> structure)
    {
        if (!isInitiated)
        {
            initAutomaton();
            isInitiated = true;
        }

        for (Cell cell : structure)
        {
            int index = getCoordsIndex(cell.getCoords());

            if (cells[index].getState() != cell.getState())
            {
                if (cellIsAlive(cell.getState()))
                    aliveCount.incrementAndGet();
                else
                    aliveCount.decrementAndGet();
            }
            cells[index].setState(cell.getState());

            if (changeListSet[index] == 0)
            {
                changeList[changeListSize++] = index;
                changeListSet[index] = 1;
            }
            for (CellCoordinates coordinates : neighborhoodStrategy.cellNeighbors(cell.getCoords()))
            {
                int indexN = getCoordsIndex(coordinates);
                if (changeListSet[indexN] == 0)
                {
                    changeList[changeListSize++] = indexN;
                    changeListSet[indexN] = 1;
                }
            }
        }
    }

    private void simulateSlice(int from, int to)
    {
        for(int j = from; j < to; j++)
        {
            int cellIndex = changeList[j];
            Cell cell = cells[cellIndex];

            List<CellCoordinates> neighbors = neighborhoodStrategy.cellNeighbors(cell.getCoords());
            CellState newState = nextCellState(cell, neighbors);

            Cell backbufferCell = setBackBufferCellState(cell, newState);
            if(backbufferCell.hasChanged())
            {
                if(cellIsAlive(newState))
                    aliveCount.incrementAndGet();
                else
                    aliveCount.decrementAndGet();

                synchronized (changeListSetBackBuffer)
                {
                    if (changeListSetBackBuffer[cellIndex] == 0)
                    {
                        changeListBackBuffer[changeListBackbufferSize++] = cellIndex;
                        changeListSetBackBuffer[cellIndex] = 1;
                    }
                }

                for(CellCoordinates coordinates : neighbors)
                {
                    int index = getCoordsIndex(coordinates);
                    synchronized (changeListSetBackBuffer)
                    {
                        if (changeListSetBackBuffer[index] == 0)
                        {
                            changeListBackBuffer[changeListBackbufferSize++] = index;
                            changeListSetBackBuffer[index] = 1;
                        }
                    }
                }
            }
        }
    }

    private int getStep(int processorsCount, int arraySize)
    {
        return (int) (arraySize / ((float) processorsCount));
    }

    private void swapBuffers()
    {
        Cell[] tmp = cellsBackBuffer;
        cellsBackBuffer = cells;
        cells = tmp;

        changeListSize = changeListBackbufferSize;
        changeListBackbufferSize = 0;

        int[] x = changeList;
        changeList = changeListBackBuffer;
        changeListBackBuffer = x;


        byte[] y = changeListSet;
        changeListSet = changeListSetBackBuffer;
        changeListSetBackBuffer = y;
        bytefill(changeListSetBackBuffer, (byte) 0);
    }
    private void bytefill(byte[] array, byte value)
    {
        int len = array.length;
        if (len > 0)
            array[0] = value;
        for (int i = 1; i < len; i += i)
            System.arraycopy( array, 0, array, i, ((len - i) < i) ? (len - i) : i);
    }
    private Cell setBackBufferCellState(Cell cell, CellState newState)
    {
        Cell backBufferCell = cellsBackBuffer[getCoordsIndex(cell.getCoords())];
        backBufferCell.isChanged(cell.getState() != newState);
        backBufferCell.setState(newState);
        return backBufferCell;
    }

    private void initAutomaton()
    {
        CellCoordinates current = initialCoordinates();

        // iterate over all coordinates and get initial state for each cell
        changeListSize = 0;
        while(hasNextCoordinates(current))
        {
            current = nextCoordinates();
            CellState initialState = stateFactory.initialState(current);
            if(cellIsAlive(initialState))
                aliveCount.incrementAndGet();

            int cellIndex = getCoordsIndex(current);
            cells[cellIndex] = new Cell(initialState, current);
            cellsBackBuffer[cellIndex] = new Cell(initialState, current);

            changeList[changeListSize] = cellIndex;
            changeListSet[changeListSize++] = 1;
        }
    }

    protected abstract CellState nextCellState(Cell cell, List<CellCoordinates> neighborsStates);
    protected abstract boolean hasNextCoordinates(CellCoordinates coords);
    protected abstract CellCoordinates initialCoordinates();
    protected abstract CellCoordinates nextCoordinates();
    protected abstract int getCoordsIndex(CellCoordinates coord);
    protected abstract boolean cellIsAlive(CellState state);

    protected CellState getCellStateByCoordinates(CellCoordinates coordinates)
    {
        return cells[getCoordsIndex(coordinates)].getState();
    }

    @Override
    public Iterator<Cell> iterator()
    {
        if(!isInitiated)
        {
            initAutomaton();
            isInitiated = true;
        }
        return new CellIterator();
    }

    private class CellIterator implements java.util.Iterator<Cell>
    {
        private int cellIndex;

        public CellIterator()
        {
            cellIndex = -1;
        }

        @Override
        public boolean hasNext()
        {
            return cellIndex < cellCount - 1;
        }

        @Override
        public Cell next()
        {
            if(!hasNext())
            {
                throw new NoSuchElementException("There is no next cell");
            }

            return cells[++cellIndex];
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("remove method not implemented");
        }
    }
}

