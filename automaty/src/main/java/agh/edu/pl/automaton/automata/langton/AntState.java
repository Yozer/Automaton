package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.cells.states.CellState;

import java.awt.*;

public enum AntState implements CellState {
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
