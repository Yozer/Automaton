package agh.edu.pl.automaton.cells.coordinates;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Dominik Baran on 2016-01-03.
 */
public class Coords1DTest {

    @Test
    public void testHashCode_Different() throws Exception {
        Coords1D coords1D = new Coords1D(5);
        Coords1D coords1D2 = new Coords1D(6);
        assertTrue(coords1D.hashCode() != coords1D2.hashCode());
    }
    @Test
    public void testHashCode_TheSame() throws Exception {
        Coords1D coords1D = new Coords1D(6);
        Coords1D coords1D2 = new Coords1D(6);
        assertTrue(coords1D.hashCode() == coords1D2.hashCode());
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void testEquals_null() throws Exception {
        Coords1D coords1D = new Coords1D(6);
        assertEquals(false, coords1D.equals(null));
    }

    @Test
    public void testEquals_TheSameReference() throws Exception {
        Coords1D coords1D = new Coords1D(6);
        Coords1D coords1D2 = coords1D;
        assertEquals(coords1D, coords1D2);
    }

    @Test
    public void testEquals_ShouldBeEqual() throws Exception {
        Coords1D coords1D = new Coords1D(6);
        Coords1D coords1D2 = new Coords1D(6);
        assertEquals(coords1D, coords1D2);
    }
    @Test
    public void testEquals_ShouldBeDifferent() throws Exception {
        Coords1D coords1D = new Coords1D(6);
        Coords1D coords1D2 = new Coords1D(8);
        assertNotEquals(coords1D, coords1D2);
    }

    @Test
    public void testToString() throws Exception {
        Coords1D coords1D = new Coords1D(6);
        assertEquals("X: 6", coords1D.toString());
    }
    @Test
    public void testToString_Minus() throws Exception {
        Coords1D coords1D = new Coords1D(-2);
        assertEquals("X: -2", coords1D.toString());
    }
}