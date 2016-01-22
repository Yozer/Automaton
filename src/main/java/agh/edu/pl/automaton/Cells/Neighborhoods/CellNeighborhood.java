package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;

/**
 * This interface is responsible for calculating neighbors for given cell coordinates.
 * @author Dominik Baran
 * @see MoorNeighborhood
 * @see VonNeumannNeighborhood
 * @see OneDimensionalNeighborhood
 * @see NeighborhoodList
 * @see CellCoordinates
 */
public interface CellNeighborhood {
    /**
     * This method calculates neighbors for given {@code CellCoordinates} and uses {@code NeighborhoodList}
     * to save them.
     * @param cell Cell coordinates for which method will return his neighbors
     * @param neighborhoodList This list will be filled with {@code cell} neighbors.
     * @return Returns filled {@code neighborhoodList}
     * @see CellCoordinates
     * @see NeighborhoodList
     */
    NeighborhoodList cellNeighbors(CellCoordinates cell, NeighborhoodList neighborhoodList);

    /**
     * This method should return a list of proper size.
     * Returned list should be able to contain maximum possible number of neighbors returned by {@code cellNeighbors}
     * @return List used by {@code cellNeighbors}
     * @see CellNeighborhood#cellNeighbors(CellCoordinates, NeighborhoodList)
     */
    NeighborhoodList createArray();
}

