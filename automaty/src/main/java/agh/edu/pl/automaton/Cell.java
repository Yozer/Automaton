package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.states.CellState;

/**
 * {@code Cell} represents one cell in automaton.
 * It has two fields {@code CellState} and {@code CellCoordinates}.
 * @author Dominik Baran
 * @see CellCoordinates
 * @see CellState
 */
public class Cell {
    private final CellCoordinates coords;
    private CellState state;
    private boolean hasChanged;

    public Cell(CellState state, CellCoordinates coords) {
        this.state = state;
        this.coords = coords;
        this.hasChanged = true;
    }
    
    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public CellCoordinates getCoords() {
        return coords;
    }

    @Override
    public int hashCode() {
        return coords.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Cell && ((Cell) obj).getCoords().equals(this.getCoords());
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    void isChanged(boolean value) {
        this.hasChanged = value;
    }
}
