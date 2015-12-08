package agh.edu.pl.automaton.cells.neighborhoods;


import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.util.ArrayList;
import java.util.HashSet;
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
    public Set<CellCoordinates> cellNeighbors(CellCoordinates cell)
    {
        Set<Coords2D> result = new HashSet<>();

        Coords2D initalCoords = (Coords2D)cell;
        result.add(initalCoords);

        int x = initalCoords.getX() - r;
        int y = initalCoords.getY() - r;

        for(int i = 0; i < 2*r + 1; i++)
        {
            for(int j = 0; j < 2*r + 1; j++)
            {
                result.add(new Coords2D(x + i, y + j));
            }
        }

        return (Set)WrapCoordinatesHelper.fixCoords(result, wrap, width, height);
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
