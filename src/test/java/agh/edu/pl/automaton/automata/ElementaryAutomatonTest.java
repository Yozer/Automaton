package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dominik on 2015-12-09.
 */
public class ElementaryAutomatonTest {
    CellStateFactory cellStateFactory;

    @Before
    public void init() { cellStateFactory = new UniformStateFactory(BinaryState.DEAD); }

    @Test
    public void testNextCellState_rule30_cellAlive_neighborsAlive() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 30, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(0)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(1)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(3, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(1, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule30_cellDead_neighborsAlive() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 30, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(0)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(1)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(2, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(2, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule30_cellAlive_leftAlive() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 30, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(0)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(1)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(2, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(2, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule30_cellAlive_neighborsDead() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 30, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(0)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(1)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(1, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(3, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule30_cellAlive_rightAlive() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 30, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(0)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(1)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(2, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(2, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule0_cellAlive_neighborsAlive() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 0, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(0)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(1)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));

        assertEquals(0, automaton.getAliveCount());
        automaton.insertStructure(cells);
        assertEquals(3, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(0, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule1_cellAlive_neighborsAlive() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 1, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(0)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(1)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(3, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(0, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule1_cellDead_neighborsDead() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 1, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(0)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(1)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(0, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(3, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule1_cellDead_leftAlive() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 1, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(0)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(1)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(1, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(1, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule254_cellDead_neighborsAlive() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 254, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(0)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(1)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(2, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(3, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule254_cellDead_neighborsDead() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 254, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(0)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(1)));
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(0, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(0, automaton.getAliveCount());
    }

    @Test
    public void testNextCellState_rule114_cellAlive_rightAlive() throws Exception {
        Automaton automaton = new ElementaryAutomaton(3, 114, cellStateFactory);
        List<Cell> cells = new ArrayList<>(3);
        cells.add(new Cell(BinaryState.DEAD, new Coords1D(0)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(1)));
        cells.add(new Cell(BinaryState.ALIVE, new Coords1D(2)));

        automaton.insertStructure(cells);
        assertEquals(2, automaton.getAliveCount());
        automaton.calculateNextState();

        Iterator<Cell> cellIterator = automaton.iterator();
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(BinaryState.DEAD, cellIterator.next().getState());
        assertEquals(BinaryState.ALIVE, cellIterator.next().getState());
        assertEquals(2, automaton.getAliveCount());
    }

    @Test
    public void testGetRule() throws Exception {
        ElementaryAutomaton automaton = new ElementaryAutomaton(3, 114, cellStateFactory);
        assertEquals(114, automaton.getRule());
        automaton.setRule(5);
        assertEquals(5, automaton.getRule());
    }

    @Test(expected  = IllegalArgumentException.class)
    public void testSetRule_ruleBiggerThan255() throws Exception {
        ElementaryAutomaton automaton = new ElementaryAutomaton(3, 114, cellStateFactory);
        automaton.setRule(268);
    }
    @Test(expected  = IllegalArgumentException.class)
    public void testSetRule_ruleBiggerLessThan0() throws Exception {
        ElementaryAutomaton automaton = new ElementaryAutomaton(3, 114, cellStateFactory);
        automaton.setRule(-1);
    }
}