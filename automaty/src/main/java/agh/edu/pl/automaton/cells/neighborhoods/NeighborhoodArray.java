package agh.edu.pl.automaton.cells.neighborhoods;

public class NeighborhoodArray {
    private final int[] array;
    private int length = 0;

    public NeighborhoodArray(int size) {
        array = new int[size];
    }

    public int getLength() {
        return length;
    }

    public void clear() {
        this.length = 0;
    }

    public void push(int coords) {
        array[length++] = coords;
    }

    public int get(int i) {
        return array[i];
    }
}
