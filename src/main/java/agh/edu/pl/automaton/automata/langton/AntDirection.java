package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.cells.states.CellState;

import java.awt.*;

/**
 * Represent a direction in which ant is directed
 * @author Dominik Baran
 * @see Ant
 */
public enum AntDirection implements CellState {
    NORTH {
        @Override
        public Color toColor() {
            throw new UnsupportedOperationException();
        }
    },
    SOUTH {
        @Override
        public Color toColor() {
            throw new UnsupportedOperationException();
        }
    },
    EAST {
        @Override
        public Color toColor() {
            throw new UnsupportedOperationException();
        }
    },
    WEST {
        @Override
        public Color toColor() {
            throw new UnsupportedOperationException();
        }
    }
}
