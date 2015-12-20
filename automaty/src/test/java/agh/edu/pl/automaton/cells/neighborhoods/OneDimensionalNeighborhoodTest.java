package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Dominik on 2015-12-09.
 */
public class OneDimensionalNeighborhoodTest
{
    @Test
    public void testCellNeighbors_r_equal_1_nowrap() throws Exception
    {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(false, 100);
        NeighborhoodArray neighborhoodArray = neighborhood.createArray();
        neighborhoodArray = neighborhood.cellNeighbors(new Coords1D(0), neighborhoodArray);

        assertEquals(1, neighborhoodArray.getLength());
        assertEquals(neighborhoodArray.get(0), 1);
    }
    @Test
    public void testCellNeighbors_r_equal_1_wrap() throws Exception
    {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(true, 7);
        NeighborhoodArray neighborhoodArray = neighborhood.createArray();
        neighborhoodArray = neighborhood.cellNeighbors(new Coords1D(0), neighborhoodArray);

        assertEquals(2, neighborhoodArray.getLength());
        assertEquals(neighborhoodArray.get(0), 6);
        assertEquals(neighborhoodArray.get(1), 1);
    }
    @Test
    public void testCellNeighbors_r_equal_1() throws Exception
    {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(false, 7);
        NeighborhoodArray neighborhoodArray = neighborhood.createArray();
        neighborhoodArray = neighborhood.cellNeighbors(new Coords1D(2), neighborhoodArray);

        assertEquals(2, neighborhoodArray.getLength());
        assertEquals(neighborhoodArray.get(0), 1);
        assertEquals(neighborhoodArray.get(1), 3);
    }
}