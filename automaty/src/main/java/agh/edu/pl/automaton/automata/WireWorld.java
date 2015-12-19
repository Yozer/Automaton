package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.ArrayWrapper;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.List;

public class WireWorld extends Automaton2Dim
{
    public WireWorld(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(width, height, cellStateFactory, cellNeighborhood);
    }

    @Override
    protected CellState nextCellState(Cell cell, ArrayWrapper neighborsStates)
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
            int headCount = 0;
            for(int i = 0; i < neighborsStates.getLength(); ++i)
            {
                if(getCellStateByIndex(neighborsStates.get(i)) == WireElectronState.ELECTRON_HEAD)
                    headCount++;
            }
            if(headCount == 1 || headCount == 2)
                return WireElectronState.ELECTRON_HEAD;
        }

        return cell.getState();
    }

    @Override
    protected boolean cellIsAlive(CellState state)
    {
        return state == WireElectronState.ELECTRON_HEAD;
    }

    @Override
    protected boolean cellChangedToAlive(CellState newState, CellState oldState)
    {
        return newState == WireElectronState.ELECTRON_HEAD;
    }

    @Override
    protected boolean cellChangedToDead(CellState newState, CellState oldState)
    {
        return oldState == WireElectronState.ELECTRON_HEAD;
    }
}
