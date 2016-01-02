package agh.edu.pl.gui.structures;


import agh.edu.pl.Main;
import agh.edu.pl.automaton.automata.langton.Ant;
import agh.edu.pl.automaton.automata.langton.AntDirection;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * Struct loader for Langton ants
 * @author Dominik Baran
 * @see StructureLoader
 */
public class LangtonAntStructureLoader extends StructureLoader {
    /** {@inheritDoc}
     */
    @Override
    protected List<Cell> loadStructureCells(int size, String path) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(Main.class.getResourceAsStream(path), Charset.forName("UTF-8"));

        try (BufferedReader reader = new BufferedReader(streamReader)) {
            String line = reader.readLine();
            if (line == null)
                throw new IOException();
            AntDirection antDirection;
            switch (line) {
                case "L":
                    antDirection = AntDirection.WEST;
                    break;
                case "U":
                    antDirection = AntDirection.NORTH;
                    break;
                case "D":
                    antDirection = AntDirection.SOUTH;
                    break;
                case "R":
                    antDirection = AntDirection.EAST;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ant states");
            }

            return Collections.singletonList(new Ant(new Coords2D(0, 0), antDirection, Color.BLACK, 0, 0, 0));
        }
    }
}
