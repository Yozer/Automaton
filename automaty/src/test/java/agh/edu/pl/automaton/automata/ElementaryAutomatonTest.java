package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.neighborhoods.*;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Dominik on 2015-12-09.
 */
public class ElementaryAutomatonTest
{
    ElementaryAutomaton automaton;
    @Before
    public void init()
    {
        CellNeighborhood cellNeighborhood = new OneDimensionalNeighborhood(true, 100);
        CellStateFactory cellStateFactory = new UniformStateFactory(BinaryState.DEAD);
        automaton = new ElementaryAutomaton(30, 100, cellNeighborhood, cellStateFactory);
    }

    @Test
    public void testNextCellState_rule30_cellAlive_neighborsAlive() throws Exception
    {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.ALIVE, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.DEAD);
    }

    @Test
    public void testNextCellState_rule30_cellDead_neighborsAlive() throws Exception
    {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.DEAD, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.DEAD);
    }

    @Test
    public void testNextCellState_rule30_cellAlive_leftAlive() throws Exception
    {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.ALIVE, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule30_cellAlive_neighborsDead() throws Exception
    {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.ALIVE, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.ALIVE);
    }
    @Test
    public void testNextCellState_rule30_cellAlive_rightAlive() throws Exception
    {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.ALIVE, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.ALIVE);
    }
    @Test
    public void testNextCellState_rule0_cellAlive_neighborsAlive() throws Exception
    {
        automaton.setRule(0);
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.ALIVE, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule1_cellAlive_neighborsAlive() throws Exception
    {
        automaton.setRule(1);
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.ALIVE, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule1_cellDead_neighborsDead() throws Exception
    {
        automaton.setRule(1);
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.DEAD, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.ALIVE);
    }

    @Test
    public void testNextCellState_rule1_cellDead_leftAlive() throws Exception
    {
        automaton.setRule(1);
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.DEAD, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule254_cellDead_neighborsAlive() throws Exception
    {
        automaton.setRule(254);
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.DEAD, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.ALIVE);
    }
    @Test
    public void testNextCellState_rule254_cellDead_neighborsDead() throws Exception
    {
        automaton.setRule(254);
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.DEAD, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule114_cellAlive_rightAlive() throws Exception
    {
        automaton.setRule(114);
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(new Cell(BinaryState.ALIVE, new Coords1D(3)), cells);
        assertEquals(newState, BinaryState.DEAD);
    }

    @Test
    public void testGetRule() throws Exception
    {
        automaton.setRule(30);
        assertEquals(30, automaton.getRule());
    }
}