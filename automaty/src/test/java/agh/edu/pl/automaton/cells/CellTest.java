package agh.edu.pl.automaton.cells;

import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.BinaryState;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Dominik on 2015-12-09.
 */
public class CellTest {

    @Test
    @SuppressFBWarnings({"DM_NUMBER_CTOR", "EC_UNRELATED_TYPES"})
    public void testEquals_differentType() {
        Cell cell = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        //noinspection UnnecessaryBoxing,EqualsBetweenInconvertibleTypes
        assertTrue(!cell.equals(new Long(5)));
    }

    @Test
    public void testEquals_sameReference() {
        Cell cell = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        //noinspection EqualsWithItself
        assertTrue(cell.equals(cell));
    }

    @Test
    public void testEquals_differentReference_theSameObjects() {
        Cell cell1 = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        Cell cell2 = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        assertTrue(cell1.equals(cell2));
    }

    @Test
    public void testHashCode_Equal() throws Exception {
        Cell cell1 = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        Cell cell2 = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        assertTrue(cell1.hashCode() == cell2.hashCode());
    }
    @Test
    public void testHashCode_NotEqual() throws Exception {
        Cell cell1 = new Cell(BinaryState.DEAD, new Coords2D(5, 5));
        Cell cell2 = new Cell(BinaryState.DEAD, new Coords2D(4, 5));
        assertTrue(cell1.hashCode() != cell2.hashCode());
    }
}