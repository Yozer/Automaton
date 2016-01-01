package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dominik on 2015-12-09.
 */
public class OneDimensionalNeighborhoodTest {
    @Test
    public void testCellNeighbors_r_equal_1_nowrap() throws Exception {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(false, 100);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords1D(0), neighborhoodList);

        assertEquals(1, neighborhoodList.getLength());
        assertEquals(neighborhoodList.get(0), 1);
    }

    @Test
    public void testCellNeighbors_r_equal_1_wrap() throws Exception {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(true, 7);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords1D(0), neighborhoodList);

        assertEquals(2, neighborhoodList.getLength());
        assertEquals(neighborhoodList.get(0), 6);
        assertEquals(neighborhoodList.get(1), 1);
    }

    @Test
    public void testCellNeighbors_r_equal_1() throws Exception {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(false, 7);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords1D(2), neighborhoodList);

        assertEquals(2, neighborhoodList.getLength());
        assertEquals(neighborhoodList.get(0), 1);
        assertEquals(neighborhoodList.get(1), 3);
    }
}