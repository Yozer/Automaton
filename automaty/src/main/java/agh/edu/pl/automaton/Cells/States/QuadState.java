package agh.edu.pl.automaton.cells.states;

import java.awt.*;

/**
 * Created by Dominik on 2015-11-29.
 */
public enum QuadState implements CellState
{
    DEAD
            {
                @Override
                public Color toColor()
                {
                    return Color.WHITE;
                }
            },
    RED
            {
                @Override
                public Color toColor()
                {
                    return Color.RED;
                }
            },
    YELLOW
            {
                @Override
                public Color toColor()
                {
                    return Color.YELLOW;
                }
            },
    BLUE
            {
                @Override
                public Color toColor()
                {
                    return Color.BLUE;
                }
            },
    GREEN
            {
                @Override
                public Color toColor()
                {
                    return Color.GREEN;
                }
            }
}