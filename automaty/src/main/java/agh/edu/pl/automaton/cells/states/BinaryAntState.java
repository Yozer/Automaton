package agh.edu.pl.automaton.cells.states;

import java.awt.*;

/**
 * Created by Dominik on 2015-12-10.
 */
public class BinaryAntState implements CellState {
    private final BinaryState binaryState;
    private final Color cellColor;

    public BinaryAntState(BinaryState binaryState) {
        this(binaryState, Color.BLACK);
    }

    public BinaryAntState(BinaryState binaryState, Color cellColor) {
        this.binaryState = binaryState;
        this.cellColor = cellColor;
    }

    public BinaryState getBinaryState() {
        return binaryState;
    }

    @Override
    public Color toColor() {
        return cellColor;
    }
}
