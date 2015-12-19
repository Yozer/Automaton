package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;

import java.util.List;
import java.util.Set;

/**
 * Created by Dominik on 2015-11-29.
 */
public interface CellNeighborhood
{
    void cellNeighbors(CellCoordinates cell, ArrayWrapper result);
    ArrayWrapper createArray();
}

