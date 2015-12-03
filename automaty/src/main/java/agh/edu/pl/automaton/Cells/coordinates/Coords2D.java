package agh.edu.pl.automaton.cells.coordinates;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 31)
                .append(x)
                .append(y)
                .toHashCode();
    }
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Coords2D))
            return false;
        if(obj == this)
            return true;

        Coords2D coords2D = (Coords2D)obj;
        return new EqualsBuilder().
                append(x, coords2D.x).
                append(y, coords2D.y)
                .isEquals();
    }
}
