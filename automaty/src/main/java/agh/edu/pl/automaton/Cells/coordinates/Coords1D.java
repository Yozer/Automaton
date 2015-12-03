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
        return new HashCodeBuilder(17, 31)
                .append(x)
                .toHashCode();
    }
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Coords1D))
            return false;
        if(obj == this)
            return true;

        Coords1D coords1D = (Coords1D)obj;
        return coords1D.getX() == this.getX();
    }
}
