package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Automaton1Dim;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class ElementaryAutomaton extends Automaton1Dim
{
    private int rule;
    private List<OneDimensionalNeighbors> neighborList;
    private Map<OneDimensionalNeighbors, BinaryState> ruleMapper;

    public ElementaryAutomaton(int rule, int size, CellStateFactory stateFactory, CellNeighborhood neighborhoodStrategy)
    {
        super(size, neighborhoodStrategy, stateFactory);
        this.generateNeighborList();
        this.setRule(rule);
    }

    @Override
    protected CellState nextCellState(Cell cell, List<CellCoordinates> neighborsStates)
    {
        OneDimensionalNeighbors states = new OneDimensionalNeighbors(neighborsStates, (BinaryState) cell.getState());
        return ruleMapper.get(states);
    }

    @Override
    protected boolean cellIsAlive(CellState state)
    {
        return state == BinaryState.ALIVE;
    }

    public int getRule()
    {
        return rule;
    }

    public void setRule(int rule)
    {
        if(rule < 0 || rule > 255)
            throw new IllegalArgumentException("Rule shoud be in range [0;255]");
        this.rule = rule;

        ruleMapper = new HashMap<>();
        for(int i = 0; i < 8; i++)
        {
            ruleMapper.put(neighborList.get(i), getBit(rule, 7 - i) == 1 ? BinaryState.ALIVE : BinaryState.DEAD);
        }

    }

    private void generateNeighborList()
    {
        neighborList = new ArrayList<>(8);
        neighborList.add(new OneDimensionalNeighbors(new BinaryState[]{BinaryState.ALIVE, BinaryState.ALIVE, BinaryState.ALIVE}));
        neighborList.add(new OneDimensionalNeighbors(new BinaryState[]{BinaryState.ALIVE, BinaryState.ALIVE, BinaryState.DEAD}));
        neighborList.add(new OneDimensionalNeighbors(new BinaryState[]{BinaryState.ALIVE, BinaryState.DEAD, BinaryState.ALIVE}));
        neighborList.add(new OneDimensionalNeighbors(new BinaryState[]{BinaryState.ALIVE, BinaryState.DEAD, BinaryState.DEAD}));
        neighborList.add(new OneDimensionalNeighbors(new BinaryState[]{BinaryState.DEAD, BinaryState.ALIVE, BinaryState.ALIVE}));
        neighborList.add(new OneDimensionalNeighbors(new BinaryState[]{BinaryState.DEAD, BinaryState.ALIVE, BinaryState.DEAD}));
        neighborList.add(new OneDimensionalNeighbors(new BinaryState[]{BinaryState.DEAD, BinaryState.DEAD, BinaryState.ALIVE}));
        neighborList.add(new OneDimensionalNeighbors(new BinaryState[]{BinaryState.DEAD, BinaryState.DEAD, BinaryState.DEAD}));
    }
    private int getBit(int n, int k)
    {
        return (n >> k) & 1;
    }

    private class OneDimensionalNeighbors
    {
        private BinaryState[] states;

        public OneDimensionalNeighbors(BinaryState[] states)
        {
            this.states = states;
        }
        public OneDimensionalNeighbors(List<CellCoordinates> cells, BinaryState middleState)
        {
            List<BinaryState> sortedStates =  cells.stream().
                    sorted((a, b) -> -Integer.compare(((Coords1D) b).getX(), ((Coords1D) a).getX()))
                    .map(t -> (BinaryState) getCellStateByCoordinates(t))
                    .collect(Collectors.toList());

            states = new BinaryState[]{ sortedStates.get(0), middleState, sortedStates.get(1)};
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder(17, 31)
                    .append(states[0])
                    .append(states[1])
                    .append(states[2])
                    .toHashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if(!(obj instanceof OneDimensionalNeighbors))
                return false;
            if(obj == this)
                return true;

            OneDimensionalNeighbors neighbors = (OneDimensionalNeighbors)obj;
            return new EqualsBuilder().
                    append(neighbors.states[0], states[0]).
                    append(neighbors.states[1], states[1]).
                    append(neighbors.states[2], states[2])
                    .isEquals();
        }
    }
}
