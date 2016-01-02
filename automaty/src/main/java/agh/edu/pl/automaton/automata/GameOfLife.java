package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.NeighborhoodList;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.Set;
/**
 * Implements GameOfLife with any rules you want.
 * @author Dominik Baran
 * @see Automaton2Dim
 * @see <a href="https://en.wikipedia.org/wiki/Conway's_Game_of_Life">Game of Life</a>
 */
public class GameOfLife extends Automaton2Dim {
    protected Set<Integer> surviveFactors;
    protected Set<Integer> comeAliveFactors;

    /**
     * @param surviveFactors Set of Integers which will be used to determine if cell should survive
     * @param comeAliveFactors Set of Integers which will be used to determine if dead cell should become alive
     * @param width Automaton width
     * @param height Automaton height
     * @param cellNeighborhood Neighborhood for automaton
     * @param cellStateFactory State factory for initial state of each cell in automaton
     */
    public GameOfLife(Set<Integer> surviveFactors, Set<Integer> comeAliveFactors, int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood) {
        super(width, height, cellStateFactory, cellNeighborhood);
        this.surviveFactors = surviveFactors;
        this.comeAliveFactors = comeAliveFactors;
    }
    /** {@inheritDoc}
     */
    @Override
    protected CellState nextCellState(Cell cell, NeighborhoodList neighborsStates) {
        int countAlive = 0;
        int length = neighborsStates.getLength();
        for (int i = 0; i < length; ++i) {
            if (getCellStateByIndex(neighborsStates.get(i)) == BinaryState.ALIVE)
                countAlive++;
        }

        CellState currentState = cell.getState();

        if (currentState == BinaryState.DEAD && comeAliveFactors.contains(countAlive))
            return BinaryState.ALIVE;
        else if (currentState == BinaryState.ALIVE && !surviveFactors.contains(countAlive))
            return BinaryState.DEAD;

        return currentState;
    }
    /** {@inheritDoc}
     */
    @Override
    protected boolean cellIsAlive(CellState state) {
        return state == BinaryState.ALIVE;
    }
    /** {@inheritDoc}
     */
    @Override
    protected boolean cellChangedStateFromDeadToAlive(CellState newState, CellState oldState) {
        return newState == BinaryState.ALIVE;
    }
    /** {@inheritDoc}
     */
    @Override
    protected boolean cellChangedStateFromAliveToDead(CellState newState, CellState oldState) {
        return newState == BinaryState.DEAD;
    }

    public Set<Integer> getSurviveFactors() {
        return surviveFactors;
    }

    public void setSurviveFactors(Set<Integer> surviveFactors) {
        this.surviveFactors = surviveFactors;
        forceToCheckAllCellsInNextGeneration();
    }

    public Set<Integer> getComeAliveFactors() {
        return comeAliveFactors;
    }

    public void setComeAliveFactors(Set<Integer> comeAliveFactors) {
        this.comeAliveFactors = comeAliveFactors;
        forceToCheckAllCellsInNextGeneration();
    }
}

