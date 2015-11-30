package agh.edu.pl.automaton.cells;

import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;

import java.util.Map;

/**
 * Created by Dominik on 2015-11-29.
 */
public class Cell
{
    private CellState state;
    private CellCoordinates coords;


    public CellState getState()
    {
        return state;
    }

    public CellCoordinates getCoords()
    {
        return coords;
    }

    public static class CellIterator implements java.util.Iterator<Cell>
    {
        // TODO WTF? Nie powinno być CellState?
        private CellCoordinates currentCoord;
        private Map<CellCoordinates, CellState> cells;

        public CellIterator(Map<CellCoordinates, CellState> cells)
        {
            this.cells = cells;
        }

        public boolean hasNext()
        {

        }
        public Cell next()
        {

        }
        public void setState(CellState state)
        {

        }
    }
}
