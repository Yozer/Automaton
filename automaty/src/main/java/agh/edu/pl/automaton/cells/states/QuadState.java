package agh.edu.pl.automaton.cells.states;

import java.awt.*;

/**
 * {@code QuadState} represents all possible states for {@code QuadLife} automaton.
 * @author Dominik Baran
 * @see CellState
 * @see agh.edu.pl.automaton.automata.QuadLife
 */
public enum QuadState implements CellState {
    DEAD {
        @Override
        public Color toColor() {
            return Color.BLACK;
        }
    },
    RED {
        @Override
        public Color toColor() {
            return Color.RED;
        }
    },
    YELLOW {
        @Override
        public Color toColor() {
            return Color.YELLOW;
        }
    },
    BLUE {
        @Override
        public Color toColor() {
            return Color.BLUE;
        }
    },
    GREEN {
        @Override
        public Color toColor() {
            return Color.GREEN;
        }
    }
}