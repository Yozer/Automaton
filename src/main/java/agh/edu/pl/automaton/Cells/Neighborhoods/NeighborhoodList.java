package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;

/**
 * This class is used as light-weight {@code List} in {@code CellNeighborhood}.
 * @author Dominik Baran
 * @see CellNeighborhood#createArray()
 * @see CellNeighborhood#cellNeighbors(CellCoordinates, NeighborhoodList)
 */
public class NeighborhoodList {
    private final int[] array;
    private int length = 0;

    /**
     *
     * @param size Maximum size of list
     */
    public NeighborhoodList(int size) {
        array = new int[size];
    }

    public int getLength() {
        return length;
    }

    void clear() {
        this.length = 0;
    }

    /**
     *
     * @param value Inserts value to list
     */
    public void push(int value) {
        array[length++] = value;
    }

    public int get(int i) {
        return array[i];
    }
}
