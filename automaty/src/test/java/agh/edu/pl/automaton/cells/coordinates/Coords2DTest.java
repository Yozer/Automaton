package agh.edu.pl.automaton.cells.coordinates;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dominik Baran on 2016-01-03.
 */
public class Coords2DTest {
    @Test
    public void testHashCode_Different() throws Exception {
        Coords2D Coords2D = new Coords2D(5, 2);
        Coords2D Coords2D2 = new Coords2D(6, 2);
        assertTrue(Coords2D.hashCode() != Coords2D2.hashCode());
    }
    @Test
    public void testHashCode_TheSame() throws Exception {
        Coords2D Coords2D = new Coords2D(7, 4);
        Coords2D Coords2D2 = new Coords2D(7, 4);
        assertTrue(Coords2D.hashCode() == Coords2D2.hashCode());
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void testEquals_null() throws Exception {
        Coords2D Coords2D = new Coords2D(6, 2);
        assertEquals(false, Coords2D.equals(null));
    }

    @Test
    public void testEquals_TheSameReference() throws Exception {
        Coords2D Coords2D = new Coords2D(2, 3);
        Coords2D Coords2D2 = Coords2D;
        assertEquals(Coords2D, Coords2D2);
    }

    @Test
    public void testEquals_ShouldBeEqual() throws Exception {
        Coords2D Coords2D = new Coords2D(6, 1);
        Coords2D Coords2D2 = new Coords2D(6, 1);
        assertEquals(Coords2D, Coords2D2);
    }
    @Test
    public void testEquals_ShouldBeDifferentByX() throws Exception {
        Coords2D Coords2D = new Coords2D(6, 3);
        Coords2D Coords2D2 = new Coords2D(8, 3);
        assertNotEquals(Coords2D, Coords2D2);
    }
    @Test
    public void testEquals_ShouldBeDifferentByY() throws Exception {
        Coords2D Coords2D = new Coords2D(6, 4);
        Coords2D Coords2D2 = new Coords2D(6, 2);
        assertNotEquals(Coords2D, Coords2D2);
    }

    @Test
    public void testToString() throws Exception {
        Coords2D Coords2D = new Coords2D(6, 2);
        assertEquals("X: 6 Y: 2", Coords2D.toString());
    }
    @Test
    public void testToString_Minus() throws Exception {
        Coords2D Coords2D = new Coords2D(-2, -1);
        assertEquals("X: -2 Y: -1", Coords2D.toString());
    }

}