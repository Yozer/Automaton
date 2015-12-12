package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

public abstract class Automaton2Dim extends Automaton
{
    private int width;
    private int height;
    private Coords2D iteratorCurrentCoordinates;

    protected Automaton2Dim(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(cellNeighborhood, cellStateFactory, width*height);
        this.width = width;
        this.height = height;
        initAutomaton();
    }

    @Override
    protected CellCoordinates initialCoordinates()
    {
        iteratorCurrentCoordinates = new Coords2D(-1, 0);
        return iteratorCurrentCoordinates;
    }

    @Override
    protected boolean hasNextCoordinates(CellCoordinates coords)
    {
        Coords2D coords2D = (Coords2D)coords;
        return coords2D.getX() < width - 1 || coords2D.getY() < height - 1;
    }

    @Override
    protected CellCoordinates nextCoordinates()
    {
        int x = iteratorCurrentCoordinates.getX() + 1;
        int y = iteratorCurrentCoordinates.getY();

        if(x >= width)
        {
            y++;
            x = 0;
        }

        iteratorCurrentCoordinates = new Coords2D(x, y);
        return iteratorCurrentCoordinates;
    }
    @Override
    protected int getCoordsIndex(CellCoordinates coord)
    {
        Coords2D coords2D = ((Coords2D) coord);
        return coords2D.getY() * getWidth() + coords2D.getX();
    }

    public int getWidth()
    {
        return width;
    }
    public int getHeight()
    {
        return height;
    }
}

