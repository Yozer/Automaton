package agh.edu.pl.automaton;


import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;

public abstract class Automaton2Dim extends Automaton
{
    private int width;
    private int height;

    @Override
    protected boolean hasNextCoordinates(CellCoordinates coords)
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

