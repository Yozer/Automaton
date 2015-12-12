package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.*;
import java.util.stream.Collectors;

public class QuadLife extends GameOfLife
{

    public QuadLife(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(new HashSet<>(Arrays.asList(2, 3)), new HashSet<>(Arrays.asList(3)), width, height, cellStateFactory, cellNeighborhood);
    }

    /*@Override
    protected Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        return new QuadLife(this.getWidth(), this.getHeight(), cellStateFactory, cellNeighborhood);
    }*/

    @Override
    protected CellState nextCellState(Cell cell, List<CellCoordinates> neighborsStates)
    {
        List<CellCoordinates> aliveCells = neighborsStates.stream().filter(t -> getCellStateByCoordinates(t) != QuadState.DEAD).collect(Collectors.toList());
        CellState currentState = cell.getState();

        if(currentState == QuadState.DEAD && super.comeAliveFactors.contains(aliveCells.size()))
        {
            EnumMap<QuadState, Long> map = aliveCells.stream().collect(Collectors.groupingBy(
                    x -> (QuadState) getCellStateByCoordinates(x), ()->new EnumMap<>(QuadState.class), Collectors.counting()));
            EnumSet.allOf(QuadState.class).forEach(c->map.putIfAbsent(c, 0L));
            map.remove(QuadState.DEAD);

            long maxValue = map.values().stream().max(Long::compare).get();
            if(maxValue == 1)
                maxValue = 0;

            final long finalMaxValue = maxValue;
            return map.keySet().stream().filter(t -> map.get(t) == finalMaxValue).findFirst().get();
        }
        else if(currentState != QuadState.DEAD && !super.surviveFactors.contains(aliveCells.size()))
            return QuadState.DEAD;

        return currentState;
    }
}
