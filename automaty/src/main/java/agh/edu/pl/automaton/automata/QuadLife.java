package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.lang.reflect.Array;
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
        int countAlive = 0;
        int[][] tab = new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, 3}};
        for(CellCoordinates coords : neighborsStates)
        {
            CellState state = getCellStateByCoordinates(coords);
            if(state != QuadState.DEAD)
            {
                if(state == QuadState.RED)
                    tab[0][0]++;
                else if(state == QuadState.GREEN)
                    tab[1][0]++;
                else if(state == QuadState.YELLOW)
                    tab[2][0]++;
                else if(state == QuadState.BLUE)
                    tab[3][0]++;

                countAlive++;
            }
        }

        CellState currentState = cell.getState();

        if(currentState == QuadState.DEAD && super.comeAliveFactors.contains(countAlive))
        {
            Arrays.sort(tab, (entry1, entry2) -> -Integer.compare(entry1[0], entry2[0]));
            int[] result;
            if(tab[0][0] == tab[1][0] && tab[0][0] == tab[2][0])
            {
                result = tab[3];
            }
            else
            {
                result = tab[0];
            }

            if(result[1] == 0)
                return QuadState.RED;
            else if(result[1] == 1)
                return QuadState.GREEN;
            else if(result[1] == 2)
                return QuadState.YELLOW;
            else if(result[1] == 3)
                return QuadState.BLUE;
        }
        else if(currentState != QuadState.DEAD && !super.surviveFactors.contains(countAlive))
        {
            return QuadState.DEAD;
        }

        return currentState;
    }
}
