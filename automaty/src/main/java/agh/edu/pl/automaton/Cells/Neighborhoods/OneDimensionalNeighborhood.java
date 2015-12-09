package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;

import java.util.HashSet;
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
    public Set<CellCoordinates> cellNeighbors(CellCoordinates cell)
    {
        Set<Coords1D> result = new HashSet<>();

        Coords1D initalCoords = (Coords1D)cell;
        result.add(initalCoords);

        int x = initalCoords.getX() - r;

        for(int i = 0; i < 2*r + 1; i++)
        {
            result.add(new Coords1D(x + i));
        }

        result.remove(initalCoords);
        return (Set)WrapCoordinatesHelper.fixCoords(result, wrap, width);
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
