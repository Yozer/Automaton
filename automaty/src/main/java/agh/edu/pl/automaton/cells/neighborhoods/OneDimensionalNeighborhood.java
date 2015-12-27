package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;

import java.util.ArrayList;
import java.util.List;


public class OneDimensionalNeighborhood implements CellNeighborhood
{
    private boolean wrap;
    private int width = 2;
    private static final int r = 1;

    public OneDimensionalNeighborhood(boolean wrap, int width)
    {
        this.wrap = wrap;
        this.width = width;
    }
    @Override
    public NeighborhoodArray cellNeighbors(CellCoordinates cell, NeighborhoodArray result)
    {
        result.setLength(0);
        Coords1D initalCoords = (Coords1D)cell;

        int originalX = initalCoords.getX();
        if(originalX - 1 >= 0)
            result.push(originalX - 1);
        else if(wrap)
            result.push(width - 1);

        if(originalX + 1 < width)
            result.push(originalX + 1);
        else if(wrap)
            result.push(0);

        return result;
    }

    public int getWidth()
    {
        return width;
    }

    public boolean isWrap()
    {
        return wrap;
    }

    public int getR()
    {
        return r;
    }

    @Override
    public NeighborhoodArray createArray()
    {
        return new NeighborhoodArray(2*r);
    }
}
