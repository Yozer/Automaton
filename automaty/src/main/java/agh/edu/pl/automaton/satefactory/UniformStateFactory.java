package agh.edu.pl.automaton.satefactory;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.states.CellState;

public class UniformStateFactory implements CellStateFactory
{
    private CellState state;
    public CellState initialState(CellCoordinates coordinates)
    {
        return null;
    }
}
