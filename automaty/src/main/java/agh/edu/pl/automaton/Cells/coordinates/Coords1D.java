package agh.edu.pl.automaton.cells.coordinates;

import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public int hashCode()
    {
        return x;
    }
    @Override
    public boolean equals(Object obj)
    {
        Coords1D c = ((Coords1D) obj);
        return c.getX() == x;
    }

    @Override
    public String toString()
    {
        return "X: " + x;
    }
}
