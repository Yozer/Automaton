package agh.edu.pl.gui.structures;

import agh.edu.pl.Main;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.BinaryState;

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
public class GameOfLiveStructureLoader extends StructureLoader
{
    private static Pattern pattern = Pattern.compile("([0-9]*)([bo])");
    @Override
    public List<Cell> getStructure(StructureInfo structureInfo, CellCoordinates startPoint)
    {
        StringBuilder source = new StringBuilder();
        InputStreamReader streamReader = new InputStreamReader(Main.class.getClassLoader().getResourceAsStream(structureInfo.getPath()), Charset.forName("UTF-8"));

        try(BufferedReader reader = new BufferedReader(streamReader))
        {
            String line = null;
            while((line = reader.readLine()) != null && line.length() != 0)
                source.append(line);

        } catch (IOException e)
        {

        }

        List<Cell> result = new ArrayList<>(structureInfo.getWidth() * structureInfo.getHeight());

        String[] splitedLines = source.toString().split("\\$");
        int y = ((Coords2D) startPoint).getY();
        int startX = ((Coords2D) startPoint).getX();
        for(int i = 0; i < splitedLines.length; i++)
        {
            if(!splitedLines[i].equals(""))
            {
                int x = startX;
                Matcher matcher = pattern.matcher(splitedLines[i]);
                while (matcher.find())
                {
                    int number = -1;
                    try
                    {
                        number = Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException e)
                    {
                    }

                    if (number == -1)
                    {
                        number = 1;
                    }

                    char c =  matcher.group(2).charAt(0);
                    BinaryState binaryState;
                    if(c == 'b')
                        binaryState = BinaryState.DEAD;
                    else
                        binaryState = BinaryState.ALIVE;

                    for(int j = 0; j < number; j++)
                    {
                        result.add(new Cell(binaryState, new Coords2D(x, y)));
                        x++;
                    }

                }
            }

            y++;
        }

        return result;
    }
}
