package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.Set;

public class LangtonAnt extends Automaton2Dim
{
    @Override
    protected Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        return null;
    }

    @Override
    protected CellState nextCellState(CellState currentState, Set<Cell> neighborsStates)
    {
        return null;
    }
}
