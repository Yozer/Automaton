package agh.edu.pl.automaton.cells;

import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.BinaryState;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dominik on 2015-12-09.
 */
public class CellTest
{

    @Test
    public void testEquals_differentType()
    {
        Cell cell = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        assertTrue(!cell.equals(new Long(5)));
    }

    @Test
    public void testEquals_sameReference()
    {
        Cell cell = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        assertTrue(cell.equals(cell));
    }
    @Test
    public void testEquals_differentReference_theSameObjects()
    {
        Cell cell1 = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        Cell cell2 = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        assertTrue(cell1.equals(cell2));
    }
}