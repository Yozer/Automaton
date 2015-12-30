package agh.edu.pl.gui.structures;


import agh.edu.pl.Main;
import agh.edu.pl.automaton.automata.langton.Ant;
import agh.edu.pl.automaton.automata.langton.AntState;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dominik on 2015-12-26.
 */
public class LangtonAntStructureLoader extends StructureLoader {
    @Override
    protected List<Cell> loadStructureCells(int size, String path) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(Main.class.getResourceAsStream(path), Charset.forName("UTF-8"));

        try (BufferedReader reader = new BufferedReader(streamReader)) {
            String line = reader.readLine();
            if (line == null)
                throw new IOException();
            AntState antState = null;
            switch (line) {
                case "L":
                    antState = AntState.WEST;
                    break;
                case "U":
                    antState = AntState.NORTH;
                    break;
                case "D":
                    antState = AntState.SOUTH;
                    break;
                case "R":
                    antState = AntState.EAST;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ant states");
            }

            return Collections.singletonList(new Ant(new Coords2D(0, 0), antState, Color.BLACK, 0, 0, 0));
        }
    }
}
