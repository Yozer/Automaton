package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.NeighborhoodArray;
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

    private final NeighborhoodArray[] neighborhoodArrays;

    private int currentChangeListSize;
    private AtomicInteger nextGenerationChangeListSize = new AtomicInteger(0);

    private CellNeighborhood neighborhoodStrategy;
    private CellStateFactory stateFactory;

    private final int cellCount;
    private final int processorsCount;
    private final ForkJoinPool threadPool;
    private final AtomicInteger currentAliveCount = new AtomicInteger(0);
    private final AtomicInteger nextGenerationAliveCount = new AtomicInteger(0);

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
        neighborhoodArrays = new NeighborhoodArray[processorsCount];
    }

    public int getAliveCount()
    {
        return currentAliveCount.get();
    }

    public void beginCalculatingNextState()
    {
        if(!isInitiated)
        {
            initAutomaton();
            isInitiated = true;
        }

        int step = getStep(processorsCount, currentChangeListSize);
        nextGenerationAliveCount.set(currentAliveCount.get());

        for(int i = 0; i < processorsCount; ++i)
        {
            int from = i * step;
            int to = i == processorsCount - 1 ? currentChangeListSize : (i + 1) * step;
            if(from >= currentChangeListSize || to > currentChangeListSize)
                break;

            final int finalI = i;
            threadPool.execute(() ->
            {
                simulateSlice(from, to, finalI);
            });
        }

        threadPool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
    public void endCalculatingNextState()
    {
        swapBuffers();
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
    public Iterator<Cell> iteratorChangedOnly()
    {
        if(!isInitiated)
        {
            initAutomaton();
            isInitiated = true;
        }
        return new CellIteratorChangedOnly();
    }

    public void calculateNextState()
    {
        beginCalculatingNextState();
        endCalculatingNextState();
    }
    public void insertStructure(Map<? extends CellCoordinates, ? extends CellState> structure)
    {
        if (!isInitiated)
        {
            initAutomaton();
            isInitiated = true;
        }
        NeighborhoodArray neighborhoodArray = neighborhoodArrays[0];

        for (CellCoordinates coords : structure.keySet())
        {
            int index = getCoordsIndex(coords);
            CellState newState = structure.get(coords);

            if (currentCells[index].getState() != newState)
            {
                if(cellChangedToAlive(newState, currentCells[index].getState()))
                    currentAliveCount.incrementAndGet();
                else if(cellChangedToDead(newState, currentCells[index].getState()))
                    currentAliveCount.decrementAndGet();
            }
            currentCells[index].setState(newState);

            if (!currentSet[index].getAndSet(true))
            {
                currentChangeList[currentChangeListSize++] = index;
            }
            neighborhoodStrategy.cellNeighbors(coords, neighborhoodArray);
            for (int i = 0; i < neighborhoodArray.getLength(); ++i)
            {
                int indexN = neighborhoodArray.get(i);
                if (!currentSet[indexN].getAndSet(true))
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
        System.out.println("Wstawiam: " + currentChangeListSize);
        NeighborhoodArray neighborhoodArray = neighborhoodArrays[0];
        for (Cell cell : structure)
        {
            int index = getCoordsIndex(cell.getCoords());

            if (currentCells[index].getState() != cell.getState())
            {
                if(cellChangedToAlive(cell.getState(), currentCells[index].getState()))
                    currentAliveCount.incrementAndGet();
                else if(cellChangedToDead(cell.getState(), currentCells[index].getState()))
                    currentAliveCount.decrementAndGet();
            }
            currentCells[index].setState(cell.getState());

            if (!currentSet[index].getAndSet(true))
            {
                currentChangeList[currentChangeListSize++] = index;
            }
            neighborhoodStrategy.cellNeighbors(cell.getCoords(), neighborhoodArray);

            for (int i = 0; i < neighborhoodArray.getLength(); ++i)
            {
                int indexN = neighborhoodArray.get(i);
                if (!currentSet[indexN].getAndSet(true))
                {
                    currentChangeList[currentChangeListSize++] = indexN;
                }
            }
        }

        System.out.println("Wstawiłem: " + currentChangeListSize);
    }

    private void simulateSlice(int from, int to, int procId)
    {
        NeighborhoodArray neighborhoodArray = neighborhoodArrays[procId];
        for(int j = from; j < to; j++)
        {
            int cellIndex = currentChangeList[j];
            Cell cell = currentCells[cellIndex];

            neighborhoodStrategy.cellNeighbors(cell.getCoords(), neighborhoodArray);
            CellState newState = nextCellState(cell, neighborhoodArray);

            Cell nextGenerationCell = setNextGenerationCellState(cell, newState);
            if(nextGenerationCell.hasChanged())
            {
                if(cellChangedToAlive(newState, cell.getState()))
                    nextGenerationAliveCount.incrementAndGet();
                else if(cellChangedToDead(newState, cell.getState()))
                    nextGenerationAliveCount.decrementAndGet();

                if (!nextGenerationSet[cellIndex].getAndSet(true))
                {
                    nextGenerationChangeList[nextGenerationChangeListSize.getAndIncrement()] = cellIndex;
                }

                for(int i = 0; i < neighborhoodArray.getLength(); i++)
                {
                    int index = neighborhoodArray.get(i);

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
        if(arraySize < 500)
            return arraySize;

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

        currentAliveCount.set(nextGenerationAliveCount.get());
    }

    private Cell setNextGenerationCellState(Cell cell, CellState newState)
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

        // get buffers from neighborhood
        for(int i = 0; i < processorsCount; i++)
        {
            neighborhoodArrays[i] = neighborhoodStrategy.createArray();
        }

        // iterate over all coordinates and get initial state for each cell
        currentChangeListSize = 0;
        while(hasNextCoordinates(current))
        {
            current = nextCoordinates();
            CellState initialState = stateFactory.initialState(current);
            if(cellIsAlive(initialState))
                currentAliveCount.incrementAndGet();

            int cellIndex = getCoordsIndex(current);
            currentCells[cellIndex] = new Cell(initialState, current);
            nextGenerationCells[cellIndex] = new Cell(initialState, current);

            currentChangeList[currentChangeListSize] = cellIndex;
            currentSet[currentChangeListSize++].set(true);
        }
    }

    protected abstract CellState nextCellState(Cell cell, NeighborhoodArray neighborsStates);
    protected abstract boolean hasNextCoordinates(CellCoordinates coords);
    protected abstract CellCoordinates initialCoordinates();
    protected abstract CellCoordinates nextCoordinates();
    protected abstract int getCoordsIndex(CellCoordinates coord);
    protected abstract boolean cellIsAlive(CellState state);
    protected abstract boolean cellChangedToAlive(CellState newState, CellState oldState);
    protected abstract boolean cellChangedToDead(CellState newState, CellState oldState);

    protected CellState getCellStateByCoordinates(CellCoordinates coordinates)
    {
        return currentCells[getCoordsIndex(coordinates)].getState();
    }
    protected CellState getCellStateByIndex(int i)
    {
        return currentCells[i].getState();
    }

    private class CellIteratorChangedOnly implements Iterator<Cell>
    {
        int i = -1;
        @Override
        public boolean hasNext()
        {
            return i < currentChangeListSize - 1;
        }

        @Override
        public Cell next()
        {
            return currentCells[currentChangeList[++i]];
        }
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("remove method not implemented");
        }

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

