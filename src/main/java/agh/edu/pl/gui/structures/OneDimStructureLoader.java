package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.states.BinaryState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Struct loader for Elementary automaton structures
 * @author Dominik Baran
 * @see StructureLoader
 */
public class OneDimStructureLoader extends StructureLoader {
    /** {@inheritDoc}
     */
    @Override
    protected List<Cell> loadStructureCells(int size, String path) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(Main.class.getResourceAsStream(path), Charset.forName("UTF-8"));
        List<Cell> result = new ArrayList<>(size);

        try (BufferedReader reader = new BufferedReader(streamReader)) {
            String line = reader.readLine();
            if (line == null)
                throw new IOException();
            if (line.equals("b"))
                result.add(new Cell(BinaryState.DEAD, new Coords1D(0)));
            else
                result.add(new Cell(BinaryState.ALIVE, new Coords1D(0)));
        }

        return result;
    }
}
