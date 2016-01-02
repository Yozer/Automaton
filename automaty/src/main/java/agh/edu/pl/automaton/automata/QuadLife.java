package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.NeighborhoodList;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class QuadLife extends GameOfLife {

    public QuadLife(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood) {
        super(new HashSet<>(Arrays.asList(2, 3)), new HashSet<>(Collections.singletonList(3)), width, height, cellStateFactory, cellNeighborhood);
    }

    @Override
    protected CellState nextCellState(Cell cell, NeighborhoodList neighborsStates) {
        int countAlive = 0;
        int[][] tab = new int[][]{{0, 0}, {0, 1}, {0, 2}, {0, 3}};
        int length = neighborsStates.getLength();
        for (int i = 0; i < length; ++i) {
            CellState state = getCellStateByIndex(neighborsStates.get(i));
            if (state != QuadState.DEAD) {
                if (state == QuadState.RED)
                    tab[0][0]++;
                else if (state == QuadState.GREEN)
                    tab[1][0]++;
                else if (state == QuadState.YELLOW)
                    tab[2][0]++;
                else if (state == QuadState.BLUE)
                    tab[3][0]++;

                countAlive++;
            }
        }

        CellState currentState = cell.getState();

        if (currentState == QuadState.DEAD && super.comeAliveFactors.contains(countAlive)) {
            int[] t;
            if (tab[0][0] < tab[1][0]) {
                t = tab[0];
                tab[0] = tab[1];
                tab[1] = t;
            }
            if (tab[0][0] < tab[2][0]) {
                t = tab[0];
                tab[0] = tab[2];
                tab[2] = t;
            }
            if (tab[1][0] < tab[2][0]) {
                t = tab[1];
                tab[1] = tab[2];
                tab[2] = t;
            }

            int[] result;
            if (tab[0][0] == tab[1][0] && tab[0][0] == tab[2][0]) {
                result = tab[3];
            } else {
                result = tab[0];
            }

            if (result[1] == 0)
                return QuadState.RED;
            else if (result[1] == 1)
                return QuadState.GREEN;
            else if (result[1] == 2)
                return QuadState.YELLOW;
            else if (result[1] == 3)
                return QuadState.BLUE;
        } else if (currentState != QuadState.DEAD && !super.surviveFactors.contains(countAlive)) {
            return QuadState.DEAD;
        }

        return currentState;
    }

    @Override
    protected boolean cellIsAlive(CellState state) {
        return state != QuadState.DEAD;
    }

    @Override
    protected boolean cellChangedToAlive(CellState newState, CellState oldState) {
        return newState != QuadState.DEAD;
    }

    @Override
    protected boolean cellChangedToDead(CellState newState, CellState oldState) {
        return newState == QuadState.DEAD;
    }
}
