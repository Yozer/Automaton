package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton1Dim;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.*;
import agh.edu.pl.automaton.cells.neighborhoods.ArrayWrapper;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.OneDimensionalNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.*;

public class ElementaryAutomaton extends Automaton1Dim
{
    private int rule;
    private List<Integer> neighborList;
    private List<BinaryState> ruleMapper;

    public ElementaryAutomaton(int rule, int size, CellStateFactory stateFactory)
    {
        super(size, new OneDimensionalNeighborhood(false, size), stateFactory);
        this.generateNeighborList();
        ruleMapper = new ArrayList<>();
        for(int i = 0; i < this.neighborList.size(); i++)
        {
            ruleMapper.add(BinaryState.DEAD);
        }

        this.setRule(rule);
    }

    @Override
    protected CellState nextCellState(Cell cell, ArrayWrapper neighborsStates)
    {
        List<Coords1D> coords1Ds = (List)neighborsStates;
        BinaryState[] neighborStates = new BinaryState[3];
        neighborStates[1] = (BinaryState) cell.getState();

        if(coords1Ds.size() == 1)
        {
            if(coords1Ds.get(0).getX() < ((Coords1D) cell.getCoords()).getX())
            {
                neighborStates[0] = (BinaryState) getCellStateByIndex(neighborsStates.get(0));
                neighborStates[2] = BinaryState.DEAD;
            }
            else
            {
                neighborStates[0] = BinaryState.DEAD;
                neighborStates[2] = (BinaryState) getCellStateByIndex(neighborsStates.get(0));
            }
        }
        else
        {
            if(coords1Ds.get(0).getX() > coords1Ds.get(1).getX())
            {
                neighborStates[0] = (BinaryState) getCellStateByIndex(neighborsStates.get(1));
                neighborStates[2] = (BinaryState) getCellStateByIndex(neighborsStates.get(0));
            }
            else
            {
                neighborStates[2] = (BinaryState) getCellStateByIndex(neighborsStates.get(1));
                neighborStates[0] = (BinaryState) getCellStateByIndex(neighborsStates.get(0));
            }
        }

        return ruleMapper.get(getHashStates(neighborStates));
    }

    @Override
    protected boolean cellIsAlive(CellState state)
    {
        return state == BinaryState.ALIVE;
    }

    @Override
    protected boolean cellChangedToAlive(CellState newState, CellState oldState)
    {
        return newState == BinaryState.ALIVE;
    }

    @Override
    protected boolean cellChangedToDead(CellState newState, CellState oldState)
    {
        return newState == BinaryState.DEAD;
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

        for(int i = 0; i < 8; i++)
        {
            ruleMapper.set(neighborList.get(i), getBit(rule, 7 - i) == 1 ? BinaryState.ALIVE : BinaryState.DEAD);
        }

    }

    private int getHashStates(BinaryState[] states)
    {
        if(states.length != 3)
            throw new IllegalArgumentException("states has have size 3");

        return (states[0] == BinaryState.ALIVE ? 1 : 0) +
                (states[1] == BinaryState.ALIVE ? 1 : 0) * 2 +
                (states[2] == BinaryState.ALIVE ? 1 : 0) * 4;
    }

    private void generateNeighborList()
    {
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
    private int getBit(int n, int k)
    {
        return (n >> k) & 1;
    }
}
