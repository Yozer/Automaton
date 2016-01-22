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
        OneDimensionalNeighborhood neighborhood = new OneDimensionalNeighborhood(false, 100);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords1D(0), neighborhoodList);

        assertEquals(1, neighborhoodList.getLength());
        assertEquals(neighborhoodList.get(0), 1);
        assertEquals(false, neighborhood.isWrap());
        assertEquals(100, neighborhood.getWidth());
    }

    @Test
    public void testCellNeighbors_r_equal_1_wrapLeft() throws Exception {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(true, 7);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords1D(0), neighborhoodList);

        assertEquals(2, neighborhoodList.getLength());
        assertEquals(6, neighborhoodList.get(0));
        assertEquals(1, neighborhoodList.get(1));
    }
    @Test
    public void testCellNeighbors_r_equal_1_wrapRight() throws Exception {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(true, 15);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords1D(14), neighborhoodList);

        assertEquals(2, neighborhoodList.getLength());
        assertEquals(13, neighborhoodList.get(0));
        assertEquals(0, neighborhoodList.get(1));
    }

    @Test
    public void testCellNeighbors_r_equal_1() throws Exception {
        CellNeighborhood neighborhood = new OneDimensionalNeighborhood(false, 7);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords1D(2), neighborhoodList);

        assertEquals(2, neighborhoodList.getLength());
        assertEquals(1, neighborhoodList.get(0));
        assertEquals(3, neighborhoodList.get(1));
    }
}