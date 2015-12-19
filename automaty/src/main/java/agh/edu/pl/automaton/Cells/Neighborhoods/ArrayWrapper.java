package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

public class ArrayWrapper
{
    private CellCoordinates[] array;
    private int length = 0;

    public ArrayWrapper(int size)
    {
        array = new CellCoordinates[size];
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public CellCoordinates[] getArray()
    {
        return array;
    }

    public void push(CellCoordinates coords)
    {
        array[length++] = coords;
    }

    public CellCoordinates get(int i)
    {
        return array[i];
    }
}
