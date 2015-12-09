package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.OneDimensionalNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
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
    Automaton automaton;
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
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.ALIVE, cells);
        assertEquals(newState, BinaryState.DEAD);
    }

    @Test
    public void testNextCellState_rule30_cellDead_neighborsAlive() throws Exception
    {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.DEAD, cells);
        assertEquals(newState, BinaryState.DEAD);
    }

    @Test
    public void testNextCellState_rule30_cellAlive_leftAlive() throws Exception
    {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.ALIVE, cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule30_cellAlive_neighborsDead() throws Exception
    {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.ALIVE, cells);
        assertEquals(newState, BinaryState.ALIVE);
    }
    @Test
    public void testNextCellState_rule30_cellAlive_rightAlive() throws Exception
    {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.ALIVE, cells);
        assertEquals(newState, BinaryState.ALIVE);
    }
    @Test
    public void testNextCellState_rule0_cellAlive_neighborsAlive() throws Exception
    {
        ((ElementaryAutomaton) automaton).setRule(0);
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.ALIVE, cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule1_cellAlive_neighborsAlive() throws Exception
    {
        ((ElementaryAutomaton) automaton).setRule(1);
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.ALIVE, cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule1_cellDead_neighborsDead() throws Exception
    {
        ((ElementaryAutomaton) automaton).setRule(1);
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.DEAD, cells);
        assertEquals(newState, BinaryState.ALIVE);
    }

    @Test
    public void testNextCellState_rule1_cellDead_leftAlive() throws Exception
    {
        ((ElementaryAutomaton) automaton).setRule(1);
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.DEAD, cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule254_cellDead_neighborsAlive() throws Exception
    {
        ((ElementaryAutomaton) automaton).setRule(254);
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.DEAD, cells);
        assertEquals(newState, BinaryState.ALIVE);
    }
    @Test
    public void testNextCellState_rule254_cellDead_neighborsDead() throws Exception
    {
        ((ElementaryAutomaton) automaton).setRule(254);
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.DEAD, cells);
        assertEquals(newState, BinaryState.DEAD);
    }
    @Test
    public void testNextCellState_rule114_cellAlive_rightAlive() throws Exception
    {
        ((ElementaryAutomaton) automaton).setRule(114);
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(4)));

        BinaryState newState = (BinaryState) automaton.nextCellState(BinaryState.ALIVE, cells);
        assertEquals(newState, BinaryState.DEAD);
    }

    @Test
    public void testGetRule() throws Exception
    {
        ((ElementaryAutomaton) automaton).setRule(30);
        assertEquals(30, ((ElementaryAutomaton) automaton).getRule());
    }
}