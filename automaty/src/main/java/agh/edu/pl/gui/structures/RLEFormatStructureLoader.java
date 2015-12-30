package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.CellState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dominik on 2015-12-20.
 */
public class RLEFormatStructureLoader extends StructureLoader {
    private static final Pattern pattern = Pattern.compile("([0-9]*)([bo])");
    private final CellState aliveCellState;
    private final CellState deadCellState;

    public RLEFormatStructureLoader(CellState aliveCellState, CellState deadCellState) {

        this.aliveCellState = aliveCellState;
        this.deadCellState = deadCellState;
    }

    @Override
    protected List<Cell> loadStructureCells(int size, String path) throws IOException {
        StringBuilder source = new StringBuilder();
        InputStreamReader streamReader = new InputStreamReader(Main.class.getResourceAsStream(path), Charset.forName("UTF-8"));

        try (BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null && line.length() != 0)
                source.append(line);

        }

        List<Cell> result = new ArrayList<>(size);

        String[] splitedLines = source.toString().split("\\$");
        int y = 0;
        int startX = 0;
        for (String splitedLine : splitedLines) {
            if (!splitedLine.equals("")) {
                int x = startX;
                Matcher matcher = pattern.matcher(splitedLine);
                while (matcher.find()) {
                    int number = -1;
                    if(!matcher.group(1).equals(""))
                        number = Integer.parseInt(matcher.group(1));

                    if (number == -1) {
                        number = 1;
                    }

                    char c = matcher.group(2).charAt(0);
                    CellState cellState;
                    if (c == 'b')
                        cellState = deadCellState;
                    else
                        cellState = aliveCellState;

                    for (int j = 0; j < number; j++) {
                        result.add(new Cell(cellState, new Coords2D(x, y)));
                        x++;
                    }
                }
            }
            y++;
        }

        return result;
    }
}
