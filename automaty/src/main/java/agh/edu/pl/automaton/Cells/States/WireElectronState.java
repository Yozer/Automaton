package agh.edu.pl.automaton.cells.states;

import java.awt.*;

/**
 * Created by Dominik on 2015-11-29.
 */
public enum WireElectronState implements CellState
{
    VOID
            {
                @Override
                public Color toColor()
                {
                    return Color.BLACK;
                }
            },
    WIRE
            {
                @Override
                public Color toColor()
                {
                    return new Color(255, 122, 17);
                }
            },
    ELECTRON_HEAD
            {
                @Override
                public Color toColor()
                {
                    return Color.BLUE;
                }
            },
    ELECTRON_TAIL
            {
                @Override
                public Color toColor()
                {
                    return Color.WHITE;
                }
            }
}