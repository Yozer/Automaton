package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class VonNeumanNeighborhood implements CellNeighborhood
{
    private int r;
    private boolean wrap;
    private int width, height;

    public VonNeumanNeighborhood(int r, boolean wrap, int width, int height)
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

        int x = initalCoords.getX();
        int y = initalCoords.getY();

        for(int i = x - r; i <= x + 2*r; i++)
        {
            for(int j = y - r; j <= y + 2*r; j++)
            {
                if(Math.abs(i - x) + Math.abs(j - y) <= r)
                    result.add(new Coords2D(i, j));
            }
        }

        result.remove(initalCoords);
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
