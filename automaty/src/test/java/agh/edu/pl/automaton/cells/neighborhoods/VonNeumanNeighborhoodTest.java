package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Dominik on 2015-12-08.
 */
public class VonNeumanNeighborhoodTest
{

    @Test
    public void testCellNeighbors_r_equal_0()
    {
        CellNeighborhood neighborhood = new VonNeumanNeighborhood(0, false, 500, 500);
        List<CellCoordinates>  result = neighborhood.cellNeighbors(new Coords2D(4, 4));

        assertEquals(0, result.size());
    }

    @Test
    public void testCellNeighbors_r_equal_1()
    {
        CellNeighborhood neighborhood = new VonNeumanNeighborhood(1, false, 500, 500);
        List<CellCoordinates> result = neighborhood.cellNeighbors(new Coords2D(10, 10));

        ArrayList<Coords2D> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(9, 10),
                        new Coords2D(11, 10),
                        new Coords2D(10, 9),
                        new Coords2D(10, 11)
                ));

        assertEquals(4, result.size());
        assertTrue(result.containsAll(expected));
    }
    @Test
    public void testCellNeighbors_r_equal_2()
    {
        CellNeighborhood neighborhood = new VonNeumanNeighborhood(2, false, 500, 500);
        List<CellCoordinates> result = neighborhood.cellNeighbors(new Coords2D(10, 10));

        ArrayList<Coords2D> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(9, 10),
                        new Coords2D(9, 9),
                        new Coords2D(9, 11),
                        new Coords2D(10, 11),
                        new Coords2D(10, 9),
                        new Coords2D(11, 11),
                        new Coords2D(11, 10),
                        new Coords2D(11, 9),
                        new Coords2D(10, 8),
                        new Coords2D(10, 12),
                        new Coords2D(8, 10),
                        new Coords2D(12, 10)
                ));

        assertEquals(12, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    public void testCellNeighbors_r_equal_2_outOfPlane_wrap()
    {
        CellNeighborhood neighborhood = new VonNeumanNeighborhood(2, true, 100, 100);
        List<CellCoordinates> result = neighborhood.cellNeighbors(new Coords2D(1, 1));

        ArrayList<Coords2D> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(0, 1),
                        new Coords2D(0, 1),
                        new Coords2D(0, 2),
                        new Coords2D(1, 2),
                        new Coords2D(1, 0),
                        new Coords2D(2, 2),
                        new Coords2D(2, 1),
                        new Coords2D(2, 0),
                        new Coords2D(1, 99),
                        new Coords2D(1, 3),
                        new Coords2D(99, 1),
                        new Coords2D(3, 1)
                ));

        assertEquals(12, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    public void testCellNeighbors_r_equal_2_outOfPlane()
    {
        CellNeighborhood neighborhood = new VonNeumanNeighborhood(2, false, 100, 100);
        List<CellCoordinates>  result = neighborhood.cellNeighbors(new Coords2D(1, 1));

        ArrayList<Coords2D> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(0, 1),
                        new Coords2D(0, 1),
                        new Coords2D(0, 2),
                        new Coords2D(1, 2),
                        new Coords2D(1, 0),
                        new Coords2D(2, 2),
                        new Coords2D(2, 1),
                        new Coords2D(2, 0),
                        new Coords2D(1, 3),
                        new Coords2D(3, 1)
                ));

        assertEquals(10, result.size());
        assertTrue(result.containsAll(expected));
    }
}