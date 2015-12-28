package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;

/**
 * Created by Dominik on 2015-11-29.
 */
public interface CellNeighborhood {
    NeighborhoodArray cellNeighbors(CellCoordinates cell, NeighborhoodArray result);

    NeighborhoodArray createArray();
}

