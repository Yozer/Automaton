package agh.edu.pl.automaton.cells;

import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Created by Dominik on 2015-11-29.
 */
public class Cell
{
    private CellState state;
    private CellCoordinates coords;

    public Cell(CellState state, CellCoordinates coords)
    {
        this.state = state;
        this.coords = coords;
    }


    public CellState getState()
    {
        return state;
    }

    public CellCoordinates getCoords()
    {
        return coords;
    }

    @Override
    public int hashCode()
    {
        return coords.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Cell))
            return false;
        if(obj == this)
            return true;
        Cell cell = (Cell)obj;

        return new EqualsBuilder()
                .append(this.coords, cell.coords)
                .isEquals();
    }
}
