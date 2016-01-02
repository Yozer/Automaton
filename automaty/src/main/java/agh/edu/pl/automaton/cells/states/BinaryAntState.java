package agh.edu.pl.automaton.cells.states;

import agh.edu.pl.automaton.automata.langton.LangtonAnt;

import java.awt.*;

/**
 * This class represents {@code CellState} for {@code LangtonAnt} automaton.
 * @author Dominik Baran
 * @see CellState
 * @see LangtonAnt
 */
public class BinaryAntState implements CellState {
    private final BinaryState binaryState;
    private final Color cellColor;

    /**
     * Creates {@code BinaryAntState} from {@code BinaryState}.
     * This constructor sets color for this state as BLACK.
     * @param binaryState Initial state
     */
    public BinaryAntState(BinaryState binaryState) {
        this(binaryState, Color.BLACK);
    }

    /**
     *
     * @param binaryState Initial state
     * @param cellColor Color for this state
     */
    public BinaryAntState(BinaryState binaryState, Color cellColor) {
        this.binaryState = binaryState;
        this.cellColor = cellColor;
    }

    /**
     * @return Returns {@code BinaryState}
     * @see BinaryState
     * @see CellState
     */
    public BinaryState getBinaryState() {
        return binaryState;
    }

    /** {@inheritDoc}
     */
    @Override
    public Color toColor() {
        return cellColor;
    }
}
