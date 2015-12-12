package agh.edu.pl.automaton.satefactory;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.CellState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dominik on 2015-11-29.
 */
public class GeneralStateFactory implements CellStateFactory
{
    private List<CellState> states;

    public GeneralStateFactory(Map<CellCoordinates, CellState> states, int width)
    {
        this.states = new ArrayList<>(states.size());
        while (this.states.size() < states.size())
            this.states.add(null);

        for(CellCoordinates cellCoordinates : states.keySet())
        {
            if(cellCoordinates instanceof Coords2D)
            {
                Coords2D coords2D = ((Coords2D) cellCoordinates);
                this.states.set(coords2D.getY() * width + coords2D.getX(), states.get(cellCoordinates));
            }
        }
    }

    public CellState initialState(int index)
    {
        return states.get(index);
    }
}
