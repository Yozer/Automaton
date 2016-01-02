package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.WireElectronState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Struct loader for WireWorld structures
 * @author Dominik Baran
 * @see StructureLoader
 */
public class WireWorldStructureLoader extends StructureLoader {
    /** {@inheritDoc}
     */
    @Override
    public List<Cell> loadStructureCells(int size, String path) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(Main.class.getResourceAsStream(path), Charset.forName("UTF-8"));
        List<Cell> result = new ArrayList<>(size);
        int startY = 0;
        int startX = 0;

        try (BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            int y = startY;

            while ((line = reader.readLine()) != null) {
                int x = startX;
                for (int i = 0; i < line.length(); i++, x++) {
                    if (line.charAt(i) == ' ')
                        continue;
                    result.add(new Cell(line.charAt(i) == '.' ? WireElectronState.VOID :
                            line.charAt(i) == '#' ? WireElectronState.WIRE :
                                    line.charAt(i) == '@' ? WireElectronState.ELECTRON_HEAD : WireElectronState.ELECTRON_TAIL,
                            new Coords2D(x, y)));
                }
                y++;
            }

        }

        return result;
    }
}
