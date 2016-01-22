package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.BinaryState;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Created by Dominik Baran on 2016-01-03.
 */
public class AntTest {
    @Test
    @SuppressFBWarnings({"DM_NUMBER_CTOR", "EC_UNRELATED_TYPES"})
    public void testEquals_differentType() {
        Ant cell = new Ant(new Coords2D(0, 0), AntDirection.EAST, Color.BLACK, 50, 50, 0);
        //noinspection UnnecessaryBoxing,EqualsBetweenInconvertibleTypes
        assertTrue(!cell.equals(new Cell(BinaryState.DEAD, new Coords2D(5, 5))));
    }

    @Test
    public void testEquals_sameReference() {
        Ant cell = new Ant(new Coords2D(0, 0), AntDirection.EAST, Color.BLACK, 50, 50, 0);
        //noinspection EqualsWithItself
        assertTrue(cell.equals(cell));
    }

    @Test
    public void testEquals_differentReference_theSameObjects() {
        Ant cell1 = new Ant(new Coords2D(0, 0), AntDirection.EAST, Color.BLACK, 50, 50, 0);
        Ant cell2 = new Ant(new Coords2D(0, 0), AntDirection.EAST, Color.BLACK, 50, 50, 0);
        assertTrue(cell1.equals(cell2));
    }
    @Test
    public void testEquals_differentReference_differentObjects() {
        Ant cell1 = new Ant(new Coords2D(0, 3), AntDirection.EAST, Color.BLACK, 52, 50, 0);
        Ant cell2 = new Ant(new Coords2D(0, 0), AntDirection.EAST, Color.BLACK, 50, 50, 0);
        assertTrue(!cell1.equals(cell2));
    }

    @Test
    public void testHashCode_Equal() throws Exception {
        Ant cell1 = new Ant(new Coords2D(0, 0), AntDirection.EAST, Color.BLACK, 50, 50, 0);
        Ant cell2 = new Ant(new Coords2D(0, 0), AntDirection.EAST, Color.BLACK, 50, 50, 0);
        assertTrue(cell1.hashCode() == cell2.hashCode());
    }
    @Test
    public void testHashCode_NotEqual() throws Exception {
        Ant cell1 = new Ant(new Coords2D(0, 2), AntDirection.EAST, Color.BLACK, 50, 53, 0);
        Ant cell2 = new Ant(new Coords2D(0, 0), AntDirection.EAST, Color.BLACK, 50, 50, 0);
        assertTrue(cell1.hashCode() != cell2.hashCode());
    }
}