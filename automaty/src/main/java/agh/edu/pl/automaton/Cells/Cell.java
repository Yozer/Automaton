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
    //private boolean hasChanged;
    private final CellCoordinates coords;

    public Cell(CellState state, CellCoordinates coords)
    {
        this.state = state;
        this.coords = coords;
        //this.hasChanged = true;
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
        return ((Cell) obj).getCoords().equals(this.getCoords());
    }

    @Override
    public String toString()
    {
        return "State: " + state.toString() + " pos: " + getCoords().toString();
    }

    public void setState(CellState state)
    {
        this.state = state;
    }

    /*public boolean hasChanged()
    {
        return hasChanged;
    }
    public void isChanged(boolean value)
    {
        this.hasChanged = value;
    }*/
}
