package agh.edu.pl.automaton.satefactory;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.states.CellState;

public class UniformStateFactory implements CellStateFactory {
    private final CellState state;

    public UniformStateFactory(CellState state) {
        this.state = state;
    }

    public CellState initialState(CellCoordinates coordinates) {
        return state;
    }
}