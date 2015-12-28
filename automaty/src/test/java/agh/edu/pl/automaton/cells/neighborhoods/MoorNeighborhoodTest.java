package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class MoorNeighborhoodTest {

    @Test
    public void testCellNeighbors_r_equal_0() {
        CellNeighborhood neighborhood = new MoorNeighborhood(0, false, 500, 500);
        NeighborhoodArray neighborhoodArray = neighborhood.createArray();
        neighborhoodArray = neighborhood.cellNeighbors(new Coords2D(10, 10), neighborhoodArray);

        assertEquals(0, neighborhoodArray.getLength());
    }

    @Test
    public void testCellNeighbors_r_equal_1() {
        MoorNeighborhood neighborhood = new MoorNeighborhood(1, false, 500, 500);
        NeighborhoodArray neighborhoodArray = neighborhood.createArray();
        neighborhoodArray = neighborhood.cellNeighbors(new Coords2D(10, 10), neighborhoodArray);

        List<Integer> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(9, 10),
                        new Coords2D(9, 9),
                        new Coords2D(9, 11),
                        new Coords2D(10, 11),
                        new Coords2D(10, 9),
                        new Coords2D(11, 11),
                        new Coords2D(11, 10),
                        new Coords2D(11, 9)
                )).stream().map(t -> t.getY() * neighborhood.getWidth() + t.getX()).collect(Collectors.toList());

        assertEquals(8, neighborhoodArray.getLength());
        for (int i = 0; i < neighborhoodArray.getLength(); i++)
            assertTrue(expected.contains(neighborhoodArray.get(i)));
    }

    @Test
    public void testCellNeighbors_r_equal_2_outOfPlane() {
        MoorNeighborhood neighborhood = new MoorNeighborhood(2, false, 500, 500);
        NeighborhoodArray neighborhoodArray = neighborhood.createArray();
        neighborhoodArray = neighborhood.cellNeighbors(new Coords2D(0, 0), neighborhoodArray);

        List<Integer> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(1, 0),
                        new Coords2D(2, 0),
                        new Coords2D(0, 1),
                        new Coords2D(1, 1),
                        new Coords2D(2, 1),
                        new Coords2D(0, 2),
                        new Coords2D(1, 2),
                        new Coords2D(2, 2)
                )).stream().map(t -> t.getY() * neighborhood.getWidth() + t.getX()).collect(Collectors.toList());

        assertEquals(8, neighborhoodArray.getLength());
        for (int i = 0; i < neighborhoodArray.getLength(); i++)
            assertTrue(expected.contains(neighborhoodArray.get(i)));
    }

    @Test
    public void testCellNeighbors_r_equal_2_outOfPlane_wrap() {
        MoorNeighborhood neighborhood = new MoorNeighborhood(1, true, 5, 5);
        NeighborhoodArray neighborhoodArray = neighborhood.createArray();
        neighborhoodArray = neighborhood.cellNeighbors(new Coords2D(0, 0), neighborhoodArray);

        List<Integer> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(1, 0),
                        new Coords2D(0, 1),
                        new Coords2D(1, 1),
                        new Coords2D(0, 4),
                        new Coords2D(1, 4),
                        new Coords2D(4, 4),
                        new Coords2D(4, 0),
                        new Coords2D(4, 1)
                )).stream().map(t -> t.getY() * neighborhood.getWidth() + t.getX()).collect(Collectors.toList());

        assertEquals(8, neighborhoodArray.getLength());
        for (int i = 0; i < neighborhoodArray.getLength(); i++)
            assertTrue(expected.contains(neighborhoodArray.get(i)));
    }
}