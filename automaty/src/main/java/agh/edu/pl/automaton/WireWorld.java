package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.Set;

public class WireWorld extends Automaton2Dim
{
    protected WireWorld(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(width, height, cellStateFactory, cellNeighborhood);
    }

    @Override
    protected Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        return new WireWorld(getWidth(), getHeight(), cellStateFactory, cellNeighborhood);
    }

    @Override
    protected CellState nextCellState(Cell cell, Set<Cell> neighborsStates)
    {
        WireElectronState state = ((WireElectronState)cell.getState());

        if(state == WireElectronState.ELECTRON_HEAD)
        {
            return WireElectronState.ELECTRON_TAIL;
        }
        else if(state == WireElectronState.ELECTRON_TAIL)
        {
            return WireElectronState.WIRE;
        }
        else if(state == WireElectronState.WIRE)
        {
            int headCount = (int) neighborsStates.stream().
                    filter(t -> t.getState() == WireElectronState.ELECTRON_HEAD).
                    count();
            if(headCount == 1 || headCount == 2)
                return WireElectronState.ELECTRON_HEAD;
        }

        return cell.getState();
    }
}
