package agh.edu.pl.automaton.cells.coordinates;

/**
 * Created by Dominik on 2015-11-29.
 */
public class Coords2D implements CellCoordinates
{
    private int x;
    private int y;

    public Coords2D(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}
