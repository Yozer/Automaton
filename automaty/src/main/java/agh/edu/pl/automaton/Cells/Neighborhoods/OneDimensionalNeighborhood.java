package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class OneDimensionalNeighborhood implements CellNeighborhood
{
    private int r;
    private boolean wrap;
    private int width;

    public OneDimensionalNeighborhood(boolean wrap, int width)
    {
        this(1, wrap, width);
    }
    public OneDimensionalNeighborhood(int r, boolean wrap, int width)
    {
        this.r = r;
        this.wrap = wrap;
        this.width = width;
    }
    @Override
    public List<CellCoordinates> cellNeighbors(CellCoordinates cell)
    {
        List<CellCoordinates> result = new ArrayList<>(2*r + 1);

        Coords1D initalCoords = (Coords1D)cell;

        int x = initalCoords.getX() - r;

        for(int i = 0; i < 2*r + 1; i++)
        {
            if(x + i != initalCoords.getX())
            {
                Coords1D coords1D = WrapCoordinatesHelper.fixCoord(new Coords1D(x + i), wrap, width);
                if(coords1D != null)
                    result.add(coords1D);
            }
        }

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
}
