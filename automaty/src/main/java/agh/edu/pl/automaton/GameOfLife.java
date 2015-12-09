package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;


import java.util.Set;

public class GameOfLife extends Automaton2Dim
{
    private Set<Integer> surviveFactors;
    private Set<Integer> comeAliveFactors;

    public GameOfLife(Set<Integer> surviveFactors, Set<Integer> comeAliveFactors, int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(width, height, cellStateFactory, cellNeighborhood);
        this.surviveFactors = surviveFactors;
        this.comeAliveFactors = comeAliveFactors;
    }

    @Override
    protected Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        return  new GameOfLife(getSurviveFactors(), getComeAliveFactors(), this.getWidth(), this.getHeight(), cellStateFactory, cellNeighborhood);
    }

    @Override
    protected CellState nextCellState(CellState currentState, Set<Cell> neighborsStates)
    {
        int countAlive = (int) neighborsStates.stream().filter(t -> t.getState() == BinaryState.ALIVE).count();

        if(currentState == BinaryState.DEAD && comeAliveFactors.contains(countAlive))
            return BinaryState.ALIVE;
        else if(currentState == BinaryState.ALIVE && ! !surviveFactors.contains(countAlive))
            return BinaryState.DEAD;

        return currentState;
    }

    public Set<Integer> getSurviveFactors()
    {
        return surviveFactors;
    }

    public void setSurviveFactors(Set<Integer> surviveFactors)
    {
        this.surviveFactors = surviveFactors;
    }

    public Set<Integer> getComeAliveFactors()
    {
        return comeAliveFactors;
    }

    public void setComeAliveFactors(Set<Integer> comeAliveFactors)
    {
        this.comeAliveFactors = comeAliveFactors;
    }
}
