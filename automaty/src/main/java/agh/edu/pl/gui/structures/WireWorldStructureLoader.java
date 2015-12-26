package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.WireElectronState;

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
public class WireWorldStructureLoader extends StructureLoader
{
    @Override
    public List<Cell> getStructure(StructureInfo structureInfo, CellCoordinates startPoint)
    {
        InputStreamReader streamReader = new InputStreamReader(Main.class.getClassLoader().getResourceAsStream(structureInfo.getPath()), Charset.forName("UTF-8"));
        List<Cell> result = new ArrayList<>(structureInfo.getWidth() * structureInfo.getHeight());
        int startY = ((Coords2D) startPoint).getY();
        int startX = ((Coords2D) startPoint).getX();

        try(BufferedReader reader = new BufferedReader(streamReader))
        {
            String line = null;
            int y = startY;

            while((line = reader.readLine())!= null)
            {
                int x = startX;
                for(int i = 0; i < line.length(); i++, x++)
                {
                    if(line.charAt(i) ==  ' ')
                        continue;
                    result.add(new Cell(line.charAt(i) == '.' ? WireElectronState.VOID :
                            line.charAt(i) == '#' ? WireElectronState.WIRE :
                            line.charAt(i) == '@' ? WireElectronState.ELECTRON_HEAD : WireElectronState.ELECTRON_TAIL,
                            new Coords2D(x, y)));
                }
                y++;
            }

        } catch (IOException e)
        {

        }

        return result;
    }
}
