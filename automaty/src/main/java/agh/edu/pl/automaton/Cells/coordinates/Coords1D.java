package agh.edu.pl.automaton.cells.coordinates;

/**
 * Created by Dominik on 2015-11-29.
 */
public class Coords1D implements CellCoordinates
{
    private int x;

    public Coords1D(int x)
    {
        this.x = x;
    }

    public int getX()
    {
        return x;
    }
}
