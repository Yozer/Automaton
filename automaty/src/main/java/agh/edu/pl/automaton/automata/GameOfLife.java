package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameOfLife extends Automaton2Dim
{
    protected Set<Integer> surviveFactors;
    protected Set<Integer> comeAliveFactors;

    public GameOfLife(Set<Integer> surviveFactors, Set<Integer> comeAliveFactors, int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(width, height, cellStateFactory, cellNeighborhood);
        this.surviveFactors = surviveFactors;
        this.comeAliveFactors = comeAliveFactors;
    }

    /*@Override
    protected Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        return new GameOfLife(getSurviveFactors(), getComeAliveFactors(), this.getWidth(), this.getHeight(), cellStateFactory, cellNeighborhood);
    }*/

    @Override
    protected CellState nextCellState(Cell cell, List<CellCoordinates> neighborsStates)
    {
        int countAlive = 0;
        for(CellCoordinates coords : neighborsStates)
        {
            if(getCellStateByCoordinates(coords) == BinaryState.ALIVE)
                countAlive++;
        }

        CellState currentState = cell.getState();

        if(currentState == BinaryState.DEAD && comeAliveFactors.contains(countAlive))
            return BinaryState.ALIVE;
        else if(currentState == BinaryState.ALIVE && !surviveFactors.contains(countAlive))
            return BinaryState.DEAD;

        return currentState;
    }

    public Set<Integer> getSurviveFactors()
    {
        return surviveFactors;
    }

    public void setSurviveFactors(List<Integer> surviveFactors)
    {
        this.surviveFactors = new HashSet<>(surviveFactors);
    }

    public Set<Integer> getComeAliveFactors()
    {
        return comeAliveFactors;
    }

    public void setComeAliveFactors(List<Integer> comeAliveFactors)
    {
        this.comeAliveFactors = new HashSet<>(comeAliveFactors);
    }
}

