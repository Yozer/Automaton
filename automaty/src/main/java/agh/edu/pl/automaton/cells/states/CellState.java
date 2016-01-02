package agh.edu.pl.automaton.cells.states;

import java.awt.*;

/**
 * Represents state of a cell in automaton.
 * @author Dominik Baran
 * @see BinaryState
 * @see QuadState
 * @see WireElectronState
 * @see BinaryAntState
 */
public interface CellState {
    /**
     * This method is used to obtain Color for give {@code CellState}
     * @return Returned color will be used to draw cell
     */
    Color toColor();
}
