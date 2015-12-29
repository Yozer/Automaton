package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.gui.enums.PossibleAutomaton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Dominik on 2015-12-20.
 */
public abstract class StructureLoader {
    public List<StructureInfo> getAvailableStructures(PossibleAutomaton automaton) {
        String directoryName = "/structures/";
        List<StructureInfo> structuresInfo = new ArrayList<>();

        if (automaton == PossibleAutomaton.GameOfLife || automaton == PossibleAutomaton.QuadLife)
            directoryName += "gameoflife";
        else if (automaton == PossibleAutomaton.Langton)
            directoryName += "langton";
        else if (automaton == PossibleAutomaton.WireWorld)
            directoryName += "wireworld";
        else if (automaton == PossibleAutomaton.Jednowymiarowy)
            directoryName += "onedim";
        else
            return structuresInfo;

        directoryName += "/";
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Main.class.getResourceAsStream(directoryName + "info.txt"), Charset.forName("UTF-8")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (Objects.equals(line, ""))
                    continue;
                String[] splited = line.split(":");
                String name = splited[0];
                int width = Integer.parseInt(splited[1]);
                int height = Integer.parseInt(splited[2]);
                String path = directoryName + splited[3];
                List<Cell> cells = getStructure(width * height, path);

                StructureInfo structureInfo = new StructureInfo(name, width, height, path, cells);
                structuresInfo.add(structureInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return structuresInfo;
    }

    protected abstract List<Cell> getStructure(int size, String path) throws IOException;
}
