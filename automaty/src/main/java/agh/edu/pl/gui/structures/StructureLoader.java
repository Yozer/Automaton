package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.automata.langton.Ant;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.gui.enums.PossibleAutomaton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * This abstract class is used to load list of available structures for given automaton.
 * @author Dominik Baran
 */
public abstract class StructureLoader {
    /**
     * @param automaton Automaton for which method should load list of structures
     * @return List of loaded structures
     */
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
                List<Cell> cells = loadStructureCells(width * height, path);

                StructureInfo structureInfo;
                if (automaton == PossibleAutomaton.Langton) {
                    Ant ant = ((Ant) cells.get(0));
                    structureInfo = new AntStructureInfo(name, ant.getAntState());
                } else {
                    structureInfo = new StructureInfo(name, width, height, cells);
                }
                structuresInfo.add(structureInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return structuresInfo;
    }

    /**
     * This method should return list of loaded cells from given file
     * @param size Structure size
     * @param path Structure absolute path
     * @return List of loaded cell
     * @throws IOException
     */
    protected abstract List<Cell> loadStructureCells(int size, String path) throws IOException;
}
