package agh.edu.pl.automaton;


import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

public abstract class Automaton1Dim extends Automaton
{
    private int size;
    private Coords1D iteratorCurrentCoordinates;

    protected Automaton1Dim(int size, CellNeighborhood neighborhoodStrategy, CellStateFactory stateFactory)
    {
        super(neighborhoodStrategy, stateFactory, size);
        this.size = size;
    }

    @Override
    protected CellCoordinates initialCoordinates()
    {
        iteratorCurrentCoordinates = new Coords1D(-1);
        return iteratorCurrentCoordinates;
    }

    @Override
    protected boolean hasNextCoordinates(CellCoordinates coords)
    {
        Coords1D coords2D = (Coords1D)coords;
        return coords2D.getX() < size;
    }

    @Override
    protected CellCoordinates nextCoordinates()
    {
        int x = iteratorCurrentCoordinates.getX() + 1;
        iteratorCurrentCoordinates = new Coords1D(x);
        return iteratorCurrentCoordinates;
    }

    public int getSize()
    {
        return size;
    }
}

