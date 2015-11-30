package agh.edu.pl.automaton.cells.neighborhoods;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;

import java.util.Set;

/**
 * Created by Dominik on 2015-11-29.
 */
public interface CellNeighborhood
{
    Set<CellCoordinates> cellNeighbors(CellCoordinates cell);
}

