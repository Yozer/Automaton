package agh.edu.pl.automaton.satefactory;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.states.CellState;

/**
 * Created by Dominik on 2015-11-29.
 */
public interface CellStateFactory
{
     CellState initialState(CellCoordinates coordinates);
}
