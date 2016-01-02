package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton1Dim;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.neighborhoods.NeighborhoodList;
import agh.edu.pl.automaton.cells.neighborhoods.OneDimensionalNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements elementary, one dimensional automaton.
 * @author Dominik Baran
 * @see Automaton1Dim
 */
public class ElementaryAutomaton extends Automaton1Dim {
    private final List<BinaryState> ruleMapper;
    private int rule;
    private List<Integer> neighborList;

    /**
     * @param rule Automaton rule
     * @param size Automaton size
     * @param stateFactory State factory for initial state of each cell in automaton
     */
    public ElementaryAutomaton(int size, int rule, CellStateFactory stateFactory) {
        super(size, new OneDimensionalNeighborhood(false, size), stateFactory);
        this.generateNeighborList();
        ruleMapper = new ArrayList<>();
        for (int i = 0; i < this.neighborList.size(); i++) {
            ruleMapper.add(BinaryState.DEAD);
        }

        this.setRule(rule);
    }
    /** {@inheritDoc}
     */
    @Override
    protected CellState nextCellState(Cell cell, NeighborhoodList neighborsStates) {
        BinaryState[] neighborStates = new BinaryState[3];
        neighborStates[1] = (BinaryState) cell.getState();

        int x = neighborsStates.get(0);
        if (neighborsStates.getLength() == 1) {
            if (x < ((Coords1D) cell.getCoords()).getX()) {
                neighborStates[0] = (BinaryState) getCellStateByIndex(neighborsStates.get(0));
                neighborStates[2] = BinaryState.DEAD;
            } else {
                neighborStates[0] = BinaryState.DEAD;
                neighborStates[2] = (BinaryState) getCellStateByIndex(neighborsStates.get(0));
            }
        } else {
            int x2 = neighborsStates.get(1);
            if (x > x2) {
                neighborStates[0] = (BinaryState) getCellStateByIndex(neighborsStates.get(1));
                neighborStates[2] = (BinaryState) getCellStateByIndex(neighborsStates.get(0));
            } else {
                neighborStates[2] = (BinaryState) getCellStateByIndex(neighborsStates.get(1));
                neighborStates[0] = (BinaryState) getCellStateByIndex(neighborsStates.get(0));
            }
        }

        return ruleMapper.get(getHashStates(neighborStates));
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

    public int getRule() {
        return rule;
    }

    /**
     * Set rules for elementary automaton
     * @param rule Rule to set. Should be greater or equal 0 and less than 256
     */
    public void setRule(int rule) {
        if (rule < 0 || rule > 255)
            throw new IllegalArgumentException("Rule should be in range [0;255]");

        this.rule = rule;

        for (int i = 0; i < 8; i++) {
            ruleMapper.set(neighborList.get(i), getBit(rule, 7 - i) == 1 ? BinaryState.ALIVE : BinaryState.DEAD);
        }
        forceToCheckAllCellsInNextGeneration();
    }

    private int getHashStates(BinaryState[] states) {
        if (states.length != 3)
            throw new IllegalArgumentException("states has to have size 3");

        return (states[0] == BinaryState.ALIVE ? 1 : 0) +
                (states[1] == BinaryState.ALIVE ? 1 : 0) * 2 +
                (states[2] == BinaryState.ALIVE ? 1 : 0) * 4;
    }

    private void generateNeighborList() {
        neighborList = new ArrayList<>(8);
        neighborList.add(getHashStates(new BinaryState[]{BinaryState.ALIVE, BinaryState.ALIVE, BinaryState.ALIVE})); //7
        neighborList.add(getHashStates(new BinaryState[]{BinaryState.ALIVE, BinaryState.ALIVE, BinaryState.DEAD})); //3
        neighborList.add(getHashStates(new BinaryState[]{BinaryState.ALIVE, BinaryState.DEAD, BinaryState.ALIVE})); //5
        neighborList.add(getHashStates(new BinaryState[]{BinaryState.ALIVE, BinaryState.DEAD, BinaryState.DEAD})); //1
        neighborList.add(getHashStates(new BinaryState[]{BinaryState.DEAD, BinaryState.ALIVE, BinaryState.ALIVE})); //6
        neighborList.add(getHashStates(new BinaryState[]{BinaryState.DEAD, BinaryState.ALIVE, BinaryState.DEAD})); //2
        neighborList.add(getHashStates(new BinaryState[]{BinaryState.DEAD, BinaryState.DEAD, BinaryState.ALIVE})); //4
        neighborList.add(getHashStates(new BinaryState[]{BinaryState.DEAD, BinaryState.DEAD, BinaryState.DEAD})); //0
    }

    private int getBit(int n, int k) {
        return (n >> k) & 1;
    }
}
