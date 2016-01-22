package agh.edu.pl.automaton.cells.states;

import java.awt.*;

/**
 * {@code WireElectronState} represents all possible states for {@code WireWorld} automaton.
 * @author Dominik Baran
 * @see CellState
 * @see agh.edu.pl.automaton.automata.WireWorld
 */
public enum WireElectronState implements CellState {
    VOID {
        @Override
        public Color toColor() {
            return Color.BLACK;
        }
    },
    WIRE {
        @Override
        public Color toColor() {
            return new Color(255, 122, 17);
        }
    },
    ELECTRON_HEAD {
        @Override
        public Color toColor() {
            return Color.BLUE;
        }
    },
    ELECTRON_TAIL {
        @Override
        public Color toColor() {
            return Color.WHITE;
        }
    }
}