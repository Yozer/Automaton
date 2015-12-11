package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Dominik on 2015-12-09.
 */
public class OneDimensionalNeighborhoodTest
{
    @Test
    public void testCellNeighbors_r_equal_0() throws Exception
    {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(0, false, 100);
        List<CellCoordinates> result = neighborhood.cellNeighbors(new Coords1D(5));

        assertEquals(0, result.size());
    }
    @Test
    public void testCellNeighbors_r_equal_1() throws Exception
    {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(1, false, 100);
        List<CellCoordinates> result = neighborhood.cellNeighbors(new Coords1D(0));

        ArrayList<Coords1D> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords1D(1)
                ));

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }
    @Test
    public void testCellNeighbors_r_equal_3_wrap() throws Exception
    {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(3, true, 7);
        List<CellCoordinates> result = neighborhood.cellNeighbors(new Coords1D(2));

        ArrayList<Coords1D> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords1D(0),
                        new Coords1D(1),
                        new Coords1D(3),
                        new Coords1D(4),
                        new Coords1D(5),
                        new Coords1D(6)
                ));

        assertEquals(6, result.size());
        assertTrue(result.containsAll(expected));
    }
}