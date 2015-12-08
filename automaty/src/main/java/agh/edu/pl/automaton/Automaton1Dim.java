package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;

public abstract class Automaton1Dim extends Automaton
{
    private int size;

    @Override
    protected boolean hasNextCoordinates(CellCoordinates coordinates)
    {
        return false;
    }
    @Override
    protected CellCoordinates initialCoordinates()
    {
        return null;
    }
    @Override
    protected CellCoordinates nextCoordinates(CellCoordinates coordinates)
    {
        return null;
    }
}

