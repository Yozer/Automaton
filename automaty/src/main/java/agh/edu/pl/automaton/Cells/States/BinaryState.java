package agh.edu.pl.automaton.cells.states;

import java.awt.*;

public enum BinaryState implements CellState
{
    DEAD
            {
                @Override
                public Color toColor()
                {
                    return Color.BLACK;
                }
            },
    ALIVE
            {
                @Override
                public Color toColor()
                {
                    return Color.WHITE;
                }
            };
}
