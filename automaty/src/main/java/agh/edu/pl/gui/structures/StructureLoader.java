package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.gui.enums.PossibleAutomaton;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Dominik on 2015-12-20.
 */
public abstract class StructureLoader
{
    public static List<StructureInfo> getAvalibleStructures(PossibleAutomaton automaton)
    {
        String directoryName = "structures" + File.separator;
        List<StructureInfo> structureInfos = new ArrayList<>();

        if(automaton == PossibleAutomaton.GameOfLive || automaton == PossibleAutomaton.QuadLife)
            directoryName += "gameoflive";
        else if(automaton == PossibleAutomaton.Langton)
            directoryName += "langton";
        else if(automaton == PossibleAutomaton.WireWorld)
            directoryName += "wireworld";


        directoryName += File.separator;

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(Main.class.getClassLoader().getResourceAsStream(directoryName + "info.txt"), Charset.forName("UTF-8"))))
        {
            String line = null;
            while((line = reader.readLine())!= null)
            {
                if(Objects.equals(line, ""))
                    continue;
                String[] splited = line.split(":");
                StructureInfo structureInfo = new StructureInfo(splited[0], Integer.parseInt(splited[1]), Integer.parseInt(splited[2]),
                        directoryName + splited[3]);
                structureInfos.add(structureInfo);
            }

        } catch (FileNotFoundException e)
        {

        } catch (IOException e)
        {

        }

        return structureInfos;
    }
    public abstract List<Cell> getStructure(StructureInfo structureInfo);
}
