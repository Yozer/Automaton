package agh.edu.pl.automaton.cells.states;

import java.awt.*;

/**
 * {@code BinaryState} represents two possible states: DEAD or ALIVE
 * @author Dominik Baran
 * @see CellState
 * @see agh.edu.pl.automaton.automata.GameOfLife
 * @see agh.edu.pl.automaton.automata.ElementaryAutomaton
 */
public enum BinaryState implements CellState {
    DEAD {
        @Override
        public Color toColor() {
            return Color.BLACK;
        }
    },
    ALIVE {
        @Override
        public Color toColor() {
            return Color.WHITE;
        }
    }
}
