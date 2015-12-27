package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.automata.langton.AntState;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by Dominik on 2015-12-26.
 */
public class LangtonAntStructureLoader
{
    public AntInfo loadAnt(StructureInfo structureInfo, Coords2D atPoint) throws IOException
    {
        InputStreamReader streamReader = new InputStreamReader(Main.class.getResourceAsStream(structureInfo.getPath()), Charset.forName("UTF-8"));

        try(BufferedReader reader = new BufferedReader(streamReader))
        {
            String line = reader.readLine();
            if (line == null)
                throw new IOException();
            AntState antState = null;
            if (line.equals("L"))
                antState = AntState.WEST;
            else if (line.equals("U"))
                antState = AntState.NORTH;
            else if (line.equals("D"))
                antState = AntState.SOUTH;
            else if (line.equals("R"))
                antState = AntState.EAST;

            return new AntInfo(antState, atPoint);
        }
    }

    public static class AntInfo
    {
        private final AntState antState;
        private final Coords2D antCoords;

        public AntInfo(AntState antState, Coords2D antCoords)
        {
            this.antState = antState;
            this.antCoords = antCoords;
        }

        public AntState getAntState()
        {
            return antState;
        }

        public Coords2D getAntCoords()
        {
            return antCoords;
        }

    }
}
