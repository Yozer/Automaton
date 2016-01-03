package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Dominik on 2015-12-08.
 */
public class VonNeumannNeighborhoodTest {

    @Test
    public void testCellNeighbors_r_equal_0() {
        VonNeumannNeighborhood neighborhood = new VonNeumannNeighborhood(0, false, 241, 5212);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords2D(4, 4), neighborhoodList);

        assertEquals(0, neighborhoodList.getLength());
        assertEquals(false, neighborhood.isWrap());
        assertEquals(241, neighborhood.getWidth());
        assertEquals(5212, neighborhood.getHeight());
        assertEquals(0, neighborhood.getRadius());
    }

    @Test
    public void testCellNeighbors_r_equal_1() {
        VonNeumannNeighborhood neighborhood = new VonNeumannNeighborhood(1, false, 500, 500);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords2D(10, 10), neighborhoodList);

        List<Integer> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(9, 10),
                        new Coords2D(11, 10),
                        new Coords2D(10, 9),
                        new Coords2D(10, 11)
                )).stream().map(t -> t.getY() * neighborhood.getWidth() + t.getX()).collect(Collectors.toList());

        assertEquals(4, neighborhoodList.getLength());
        for (int i = 0; i < neighborhoodList.getLength(); i++)
            assertTrue(expected.contains(neighborhoodList.get(i)));
    }

    @Test
    public void testCellNeighbors_r_equal_2() {
        VonNeumannNeighborhood neighborhood = new VonNeumannNeighborhood(2, false, 500, 500);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords2D(10, 10), neighborhoodList);

        List<Integer> expected = new ArrayList<>(
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
                )).stream().map(t -> t.getY() * neighborhood.getWidth() + t.getX()).collect(Collectors.toList());

        assertEquals(12, neighborhoodList.getLength());
        for (int i = 0; i < neighborhoodList.getLength(); i++)
            assertTrue(expected.contains(neighborhoodList.get(i)));
    }

    @Test
    public void testCellNeighbors_r_equal_2_outOfPlane_wrap() {
        VonNeumannNeighborhood neighborhood = new VonNeumannNeighborhood(2, true, 100, 100);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords2D(1, 1), neighborhoodList);

        List<Integer> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(0, 0),
                        new Coords2D(0, 1),
                        new Coords2D(0, 2),
                        new Coords2D(1, 2),
                        new Coords2D(1, 0),
                        new Coords2D(1, 3),
                        new Coords2D(99, 1),
                        new Coords2D(1, 99),
                        new Coords2D(2, 0),
                        new Coords2D(2, 1),
                        new Coords2D(2, 2),
                        new Coords2D(3, 1)
                )).stream().map(t -> t.getY() * neighborhood.getWidth() + t.getX()).collect(Collectors.toList());

        assertEquals(12, neighborhoodList.getLength());
        for (int i = 0; i < neighborhoodList.getLength(); i++)
            assertTrue(expected.contains(neighborhoodList.get(i)));
    }

    @Test
    public void testCellNeighbors_r_equal_2_outOfPlane() {
        VonNeumannNeighborhood neighborhood = new VonNeumannNeighborhood(2, false, 100, 100);
        NeighborhoodList neighborhoodList = neighborhood.createArray();
        neighborhoodList = neighborhood.cellNeighbors(new Coords2D(1, 1), neighborhoodList);

        List<Integer> expected = new ArrayList<>(
                Arrays.asList(
                        new Coords2D(0, 0),
                        new Coords2D(0, 1),
                        new Coords2D(0, 2),
                        new Coords2D(1, 2),
                        new Coords2D(1, 0),
                        new Coords2D(1, 3),
                        new Coords2D(2, 0),
                        new Coords2D(2, 1),
                        new Coords2D(2, 2),
                        new Coords2D(3, 1)
                )).stream().map(t -> t.getY() * neighborhood.getWidth() + t.getX()).collect(Collectors.toList());

        assertEquals(10, neighborhoodList.getLength());
        for (int i = 0; i < neighborhoodList.getLength(); i++)
            assertTrue(expected.contains(neighborhoodList.get(i)));
    }
}