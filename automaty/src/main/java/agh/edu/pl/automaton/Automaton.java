package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class Automaton implements Iterable<Cell>
{
    private Cell[] currentCells;
    private Cell[] nextGenerationCells;
    private int[] currentChangeList;
    private int[] nextGenerationChangeList;
    
    private AtomicBoolean[] currentSet;
    private AtomicBoolean[] nextGenerationSet;

    private int currentChangeListSize;
    private AtomicInteger nextGenerationChangeListSize = new AtomicInteger(0);

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

        currentCells = new Cell[cellCount];
        nextGenerationCells = new Cell[cellCount];
        currentChangeList = new int[cellCount];
        nextGenerationChangeList = new int[cellCount];
        currentSet = new AtomicBoolean[cellCount];
        nextGenerationSet = new AtomicBoolean[cellCount];

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

        int step = getStep(processorsCount, currentChangeListSize);

        for(int i = 0; i < processorsCount; ++i)
        {
            int from = i * step;
            int to = i == processorsCount - 1 ? currentChangeListSize : (i + 1) * step;

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

            if (currentCells[index].getState() != newState)
            {
                if (cellIsAlive(newState))
                    aliveCount.incrementAndGet();
                else
                    aliveCount.decrementAndGet();
            }
            currentCells[index].setState(newState);

            if (!currentSet[index].getAndSet(true))
            {
                currentChangeList[currentChangeListSize++] = index;
            }
            for (CellCoordinates coordinates : neighborhoodStrategy.cellNeighbors(coords))
            {
                int indexN = getCoordsIndex(coordinates);
                if (!currentSet[index].getAndSet(true))
                {
                    currentChangeList[currentChangeListSize++] = indexN;
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

            if (currentCells[index].getState() != cell.getState())
            {
                if (cellIsAlive(cell.getState()))
                    aliveCount.incrementAndGet();
                else
                    aliveCount.decrementAndGet();
            }
            currentCells[index].setState(cell.getState());

            if (!currentSet[index].getAndSet(true))
            {
                currentChangeList[currentChangeListSize++] = index;
            }
            for (CellCoordinates coordinates : neighborhoodStrategy.cellNeighbors(cell.getCoords()))
            {
                int indexN = getCoordsIndex(coordinates);
                if (!currentSet[index].getAndSet(true))
                {
                    currentChangeList[currentChangeListSize++] = indexN;
                }
            }
        }
    }

    private void simulateSlice(int from, int to)
    {
        for(int j = from; j < to; j++)
        {
            int cellIndex = currentChangeList[j];
            Cell cell = currentCells[cellIndex];

            List<CellCoordinates> neighbors = neighborhoodStrategy.cellNeighbors(cell.getCoords());
            CellState newState = nextCellState(cell, neighbors);

            Cell backbufferCell = setBackBufferCellState(cell, newState);
            if(backbufferCell.hasChanged())
            {
                if(cellIsAlive(newState))
                    aliveCount.incrementAndGet();
                else
                    aliveCount.decrementAndGet();


                if (!nextGenerationSet[cellIndex].getAndSet(true))
                {
                    nextGenerationChangeList[nextGenerationChangeListSize.getAndIncrement()] = cellIndex;
                }

                for(CellCoordinates coordinates : neighbors)
                {
                    int index = getCoordsIndex(coordinates);

                    if (!nextGenerationSet[index].getAndSet(true))
                    {
                        nextGenerationChangeList[nextGenerationChangeListSize.getAndIncrement()] = index;
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

        for(int i = 0; i < currentChangeListSize; ++i)
        {
            currentSet[currentChangeList[i]].set(false);
        }
        Cell[] tmp = nextGenerationCells;
        nextGenerationCells = currentCells;
        currentCells = tmp;

        currentChangeListSize = nextGenerationChangeListSize.get();
        nextGenerationChangeListSize.set(0);

        int[] x = currentChangeList;
        currentChangeList = nextGenerationChangeList;
        nextGenerationChangeList = x;

        AtomicBoolean[] y = currentSet;
        currentSet = nextGenerationSet;
        nextGenerationSet = y;
                /*
        for(int i = 0; i < changeListSetBackBuffer.length; i++)
        {
            changeListSetBackBuffer[i].set(false);
        }*/

    }

    private Cell setBackBufferCellState(Cell cell, CellState newState)
    {
        Cell backBufferCell = nextGenerationCells[getCoordsIndex(cell.getCoords())];
        backBufferCell.isChanged(cell.getState() != newState);
        backBufferCell.setState(newState);
        return backBufferCell;
    }

    private void initAutomaton()
    {
        CellCoordinates current = initialCoordinates();

        for(int i = 0; i < cellCount; i++)
        {
            currentSet[i] = new AtomicBoolean(false);
            nextGenerationSet[i] = new AtomicBoolean(false);
        }

        // iterate over all coordinates and get initial state for each cell
        currentChangeListSize = 0;
        while(hasNextCoordinates(current))
        {
            current = nextCoordinates();
            CellState initialState = stateFactory.initialState(current);
            if(cellIsAlive(initialState))
                aliveCount.incrementAndGet();

            int cellIndex = getCoordsIndex(current);
            currentCells[cellIndex] = new Cell(initialState, current);
            nextGenerationCells[cellIndex] = new Cell(initialState, current);

            currentChangeList[currentChangeListSize] = cellIndex;
            currentSet[currentChangeListSize++].set(true);
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
        return currentCells[getCoordsIndex(coordinates)].getState();
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

            return currentCells[++cellIndex];
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("remove method not implemented");
        }
    }
}

