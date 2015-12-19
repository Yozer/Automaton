package agh.edu.pl.automaton.cells.neighborhoods;


import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoorNeighborhood implements CellNeighborhood
{
    private int r;
    private boolean wrap;
    private int width, height;

    public MoorNeighborhood(int r, boolean wrap, int width, int height)
    {
        this.r = r;
        this.wrap = wrap;
        this.width = width;
        this.height = height;
    }

    @Override
    public ArrayWrapper createArray()
    {
        return new ArrayWrapper((2*r + 1)*(2*r + 1));
    }

    @Override
    public void cellNeighbors(CellCoordinates cell, ArrayWrapper result)
    {
        result.setLength(0);
        Coords2D initalCoords = (Coords2D)cell;

        int x = initalCoords.getX() - r;
        int y = initalCoords.getY() - r;

        for(int i = 0; i < 2*r + 1; i++)
        {
            for(int j = 0; j < 2*r + 1; j++)
            {
                if(x + i != initalCoords.getX() || y + j != initalCoords.getY())
                {
                    Coords2D coords2D = WrapCoordinatesHelper.fixCoord(new Coords2D(x + i, y + j), wrap, width, height);
                    if(coords2D != null)
                        result.push(coords2D);
                }
            }
        }
    }

    public int getHeight()
    {
        return height;
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
}

