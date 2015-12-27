package agh.edu.pl.automaton.cells.neighborhoods;


import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

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
    public NeighborhoodArray createArray()
    {
        return new NeighborhoodArray((2*r + 1)*(2*r + 1) - 1);
    }

    @Override
    public NeighborhoodArray cellNeighbors(CellCoordinates cell, NeighborhoodArray result)
    {
        result.setLength(0);
        Coords2D initalCoords = (Coords2D)cell;

        int xOriginal = initalCoords.getX();
        int yOriginal = initalCoords.getY();
        int limitX = xOriginal + r + 1;
        int limitY = yOriginal + r + 1;
        int xN;
        int yN;

        for(int x = xOriginal - r; x < limitX; x++)
        {
            for(int y = yOriginal - r; y < limitY; y++)
            {
                if(x != xOriginal || y != yOriginal)
                {
                    if(wrap)
                    {
                        xN = x;
                        yN = y;
                        if (xN < 0 || xN >= width)
                            xN = Math.floorMod(xN, width);
                        if (yN < 0 || yN >= height)
                            yN = Math.floorMod(yN, height);
                        result.push(yN * width + xN);
                    }
                    else if(x < 0 || x >= width || y < 0 || y >= height)
                    {
                        continue;
                    }
                    else
                    {
                        result.push(y * width + x);
                    }
                }
            }
        }

        return result;
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

