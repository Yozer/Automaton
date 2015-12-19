package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

public class ArrayWrapper
{
    private int[] array;
    private int length = 0;

    public ArrayWrapper(int size)
    {
        array = new int[size];
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public void push(int coords)
    {
        array[length++] = coords;
    }

    public int get(int i)
    {
        return array[i];
    }
}
