package agh.edu.pl.automaton.satefactory;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.states.CellState;

import java.util.Map;

/**
 * Created by Dominik on 2015-11-29.
 */
public class GeneralStateFactory implements CellStateFactory
{
    private Map<CellCoordinates, CellState> states;

    public GeneralStateFactory(Map<CellCoordinates, CellState> states)
    {
        this.states = states;
    }

    public CellState initialState(CellCoordinates coordinates)
    {
        return states.get(coordinates);
    }
}
