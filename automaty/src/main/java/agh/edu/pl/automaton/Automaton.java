package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.NeighborhoodList;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class represents single Automaton.
 * @author Dominik Baran
 * @see Automaton2Dim
 * @see Automaton1Dim
 */
public abstract class Automaton implements Iterable<Cell> {
    private final NeighborhoodList[] neighborhoodLists;
    private final int cellCount;
    private final int processorsCount;
    private final ForkJoinPool threadPool;
    private final AtomicInteger currentAliveCount = new AtomicInteger(0);
    private final AtomicInteger nextGenerationAliveCount = new AtomicInteger(0);
    private final AtomicInteger nextGenerationChangeListSize = new AtomicInteger(0);
    private final CellStateFactory stateFactory;
    private Cell[] currentCells;
    private Cell[] nextGenerationCells;
    private int[] currentChangeList;
    private int[] nextGenerationChangeList;
    private AtomicBoolean[] currentSet;
    private AtomicBoolean[] nextGenerationSet;
    private int currentChangeListSize;
    private CellNeighborhood neighborhoodStrategy;
    private boolean isInitiated = false;

    /**
     * @param neighborhoodStrategy Neighborhood for automaton
     * @param stateFactory State factory for initial state of each cell in automaton
     * @param cellCount Total cell count
     */
    Automaton(CellNeighborhood neighborhoodStrategy, CellStateFactory stateFactory, int cellCount) {
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
        neighborhoodLists = new NeighborhoodList[processorsCount];
    }

    private class CellIteratorChangedOnly implements Iterator<Cell> {
        int i = -1;

        @Override
        public boolean hasNext() {
            return i < currentChangeListSize - 1;
        }

        @Override
        public Cell next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return currentCells[currentChangeList[++i]];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove method not implemented");
        }

    }

    private class CellIterator implements java.util.Iterator<Cell> {
        private int cellIndex;

        public CellIterator() {
            cellIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return cellIndex < cellCount - 1;
        }

        @Override
        public Cell next() {
            if (!hasNext()) {
                throw new NoSuchElementException("There is no next cell");
            }

            return currentCells[++cellIndex];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove method not implemented");
        }
    }

    /**
     * Changes neighborhood for automaton.
     * @param neighborhood New neighborhood
     */
    public void setNeighborhood(CellNeighborhood neighborhood) {
        this.neighborhoodStrategy = neighborhood;

        initIfNotInitiated();
        for (int i = 0; i < neighborhoodLists.length; ++i)
            neighborhoodLists[i] = neighborhood.createArray();
        forceToCheckAllCellsInNextGeneration();
    }

    /**
     * @return Number of cells which are alive
     * @see Automaton#cellIsAlive(CellState)
     * @see Automaton#cellChangedStateFromAliveToDead(CellState, CellState)
     * @see Automaton#cellChangedStateFromDeadToAlive(CellState, CellState)
     */
    public int getAliveCount() {
        initIfNotInitiated();
        return currentAliveCount.get();
    }

    /**
     * This method calculates next state for all cells in automaton. It's similar to {@code calculateNextState} method
     * but after this method is done - iterating over cells in automaton will return old state for each cell!
     * To present calculated state you should call {@code endCalculatingNextState}.
     * It's useful when you want to begin calculating next state (which can take a while) and in the same time you want to analyze old state.
     * @see Automaton#endCalculatingNextState()
     * @see Automaton#calculateNextState()
     */
    public void beginCalculatingNextState() {
        initIfNotInitiated();

        int step = getStep(processorsCount, currentChangeListSize);
        nextGenerationAliveCount.set(currentAliveCount.get());

        for (int i = 0; i < processorsCount; ++i) {
            int from = i * step;
            int to = i == processorsCount - 1 ? currentChangeListSize : (i + 1) * step;
            if (from >= currentChangeListSize || to > currentChangeListSize)
                break;

            final int finalI = i;
            threadPool.execute(() ->
            {
                simulateSlice(from, to, finalI);
            });
        }

        threadPool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    /**
     * This method should be always called after {@code beginCalculatingNextState} method to present calculated state.
     */
    public void endCalculatingNextState() {
        swapBuffers();
    }

    /**
     * @return Returns iterator which iterates over all cells in automaton.
     */
    @Override
    public Iterator<Cell> iterator() {
        initIfNotInitiated();
        return new CellIterator();
    }

    /**
     * @return Returns iterator which iterates over all cells which has changed state in last generation.
     * @see CellState
     */
    public Iterator<Cell> iteratorChangedOnly() {
        initIfNotInitiated();
        return new CellIteratorChangedOnly();
    }

    /**
     * This method calculates next state of automaton.
     * It iterates over all cells and use {@code nextCellState} method to determine new state.
     * @see CellState
     * @see Automaton#nextCellState(Cell, NeighborhoodList)
     * @see Automaton#beginCalculatingNextState()
     * @see Automaton#endCalculatingNextState()
     */
    public void calculateNextState() {
        beginCalculatingNextState();
        endCalculatingNextState();
    }

    /**
     * This method inserts list of cells to current automaton state
     * @param structure List of {@code Cell} to insert
     * @see Cell
     */
    public void insertStructure(List<Cell> structure) {
        initIfNotInitiated();

        NeighborhoodList neighborhoodList = neighborhoodLists[0];
        for (Cell cell : structure) {
            int index = getCoordsUniqueIndex(cell.getCoords());

            if (currentCells[index].getState() != cell.getState()) {
                if (cellChangedStateFromDeadToAlive(cell.getState(), currentCells[index].getState()))
                    currentAliveCount.incrementAndGet();
                else if (cellChangedStateFromAliveToDead(cell.getState(), currentCells[index].getState()))
                    currentAliveCount.decrementAndGet();
            }
            currentCells[index].setState(cell.getState());

            if (!currentSet[index].getAndSet(true)) {
                currentChangeList[currentChangeListSize++] = index;
            }
            neighborhoodStrategy.cellNeighbors(cell.getCoords(), neighborhoodList);

            for (int i = 0; i < neighborhoodList.getLength(); ++i) {
                int indexN = neighborhoodList.get(i);
                if (!currentSet[indexN].getAndSet(true)) {
                    currentChangeList[currentChangeListSize++] = indexN;
                }
            }
        }
    }

    private void initIfNotInitiated() {
        if (!isInitiated) {
            initAutomaton();
            isInitiated = true;
        }
    }
    private void simulateSlice(int from, int to, int procId) {
        NeighborhoodList neighborhoodList = neighborhoodLists[procId];
        for (int j = from; j < to; j++) {
            int cellIndex = currentChangeList[j];
            Cell cell = currentCells[cellIndex];

            neighborhoodStrategy.cellNeighbors(cell.getCoords(), neighborhoodList);
            CellState newState = nextCellState(cell, neighborhoodList);

            Cell nextGenerationCell = setNextGenerationCellState(cell, newState);
            if (nextGenerationCell.hasChanged()) {
                if (cellChangedStateFromDeadToAlive(newState, cell.getState()))
                    nextGenerationAliveCount.incrementAndGet();
                else if (cellChangedStateFromAliveToDead(newState, cell.getState()))
                    nextGenerationAliveCount.decrementAndGet();

                if (!nextGenerationSet[cellIndex].getAndSet(true)) {
                    nextGenerationChangeList[nextGenerationChangeListSize.getAndIncrement()] = cellIndex;
                }

                for (int i = 0; i < neighborhoodList.getLength(); i++) {
                    int index = neighborhoodList.get(i);

                    if (!nextGenerationSet[index].getAndSet(true)) {
                        nextGenerationChangeList[nextGenerationChangeListSize.getAndIncrement()] = index;
                    }

                }
            }
        }
    }

    private int getStep(int processorsCount, int arraySize) {
        if (arraySize < 500)
            return arraySize;

        return (int) (arraySize / ((float) processorsCount));
    }

    private void swapBuffers() {
        for (int i = 0; i < currentChangeListSize; ++i) {
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

    private Cell setNextGenerationCellState(Cell cell, CellState newState) {
        Cell backBufferCell = nextGenerationCells[getCoordsUniqueIndex(cell.getCoords())];
        backBufferCell.isChanged(cell.getState() != newState);
        backBufferCell.setState(newState);
        return backBufferCell;
    }

    private void initAutomaton() {
        CellCoordinates current = initialCoordinates();

        for (int i = 0; i < cellCount; i++) {
            currentSet[i] = new AtomicBoolean(false);
            nextGenerationSet[i] = new AtomicBoolean(false);
        }

        // get buffers from neighborhood
        for (int i = 0; i < processorsCount; i++) {
            neighborhoodLists[i] = neighborhoodStrategy.createArray();
        }

        // iterate over all coordinates and get initial state for each cell
        currentChangeListSize = 0;
        while (hasNextCoordinates(current)) {
            current = nextCoordinates();
            CellState initialState = stateFactory.initialState(current);
            if (cellIsAlive(initialState)) {
                currentAliveCount.incrementAndGet();
            }

            int cellIndex = getCoordsUniqueIndex(current);
            currentCells[cellIndex] = new Cell(initialState, current);
            nextGenerationCells[cellIndex] = new Cell(initialState, current);

            currentChangeList[currentChangeListSize] = cellIndex;
            currentSet[currentChangeListSize++].set(true);
        }
    }

    /**
     * @param i {@code Cell} index obtained by {@code getCoordsUniqueIndex} method
     * @return CellState for given cell index
     * @see Automaton#getCoordsUniqueIndex(CellCoordinates)
     */
    protected CellState getCellStateByIndex(int i) {
        return currentCells[i].getState();
    }

    /**
     * Forces all cells to be checked in next generation.
     * Useful only when you changed simulation parameters.
     */
    protected void forceToCheckAllCellsInNextGeneration() {
        initIfNotInitiated();
        for (int i = 0; i < cellCount; ++i) {
            if (!currentSet[i].getAndSet(true)) {
                currentChangeList[currentChangeListSize++] = i;
            }
        }
    }

    /**
     * This method determines next state of {@code Cell} and it's {@code NeighborhoodList}.
     * @param cell Cell for which next state will be calculated
     * @param neighborsStates List of neighbors
     * @return New state of {@code Cell}
     * @see CellState
     * @see NeighborhoodList
     */
    protected abstract CellState nextCellState(Cell cell, NeighborhoodList neighborsStates);

    /**
     * This method is used to iterate over all cells in automaton.
     * It's used to determine if we can move forward with {@code CellIterator}
     * @param coords CellCoordinates to check
     * @return True if invoking {@code nextCoordinates} will return valid {@code CellCoordinates}
     * @see Automaton#nextCoordinates()
     * @see Automaton#initialCoordinates()
     * @see CellCoordinates
     * @see CellIterator
     */
    protected abstract boolean hasNextCoordinates(CellCoordinates coords);

    /**
     * This method is used to iterate over all cells in automaton.
     * @return Initial cell coordinates. Should point on coordinates that are before starting coordinates.
     * @see Automaton#nextCoordinates()
     * @see Automaton#hasNextCoordinates(CellCoordinates) ()
     * @see CellCoordinates
     * @see CellIterator
     */
    protected abstract CellCoordinates initialCoordinates();

    /**
     * This method is used to iterate over all cells in automaton.
     * @return Next cell coordinates
     * @see Automaton#hasNextCoordinates(CellCoordinates)
     * @see Automaton#initialCoordinates()
     * @see CellCoordinates
     * @see CellIterator
     */
    protected abstract CellCoordinates nextCoordinates();

    /**
     * This method should return unique index for given {@code CellCoordinates} in range &#60;0;width*height].
     * Where width is width of automaton and height is height of automaton.
     * The same CellCoordinates (x == x and y == y) should return the same index.
     * @param coord CellCoordinates to map
     * @return Unique index
     */
    protected abstract int getCoordsUniqueIndex(CellCoordinates coord);

    /**
     * This method is used to determine if cell state is alive or not.
     * Thanks that {@code Automaton} can keep track on alive cells count.
     * @param state State to check
     * @return True if alive false otherwise
     * @see Automaton#getAliveCount()
     */
    protected abstract boolean cellIsAlive(CellState state);

    /**
     * This method is used to determine if cell state has changed from dead to alive.
     * Thanks that {@code Automaton} can keep track on alive cells count.
     * @param newState New cell state
     * @param oldState Old cell state
     * @return True if cell chas changed state from alive to dead. False otherwise.
     */
    protected abstract boolean cellChangedStateFromDeadToAlive(CellState newState, CellState oldState);
    /**
     * This method is used to determine if cell state has changed from alive to dead.
     * Thanks that {@code Automaton} can keep track on alive cells count.
     * @param newState New cell state
     * @param oldState Old cell state
     * @return True if cell chas changed state from dead to alive. False otherwise.
     */
    protected abstract boolean cellChangedStateFromAliveToDead(CellState newState, CellState oldState);

}

