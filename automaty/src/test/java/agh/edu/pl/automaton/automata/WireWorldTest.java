package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.WireElectronState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Dominik on 2015-12-09.
 */
public class WireWorldTest
{
    WireWorld automaton;
    @Before
    public void init()
    {
        CellNeighborhood cellNeighborhood = new MoorNeighborhood(1, false, 3, 2);
        CellStateFactory cellStateFactory = new UniformStateFactory(WireElectronState.VOID);
        automaton = new WireWorld(3, 2, cellStateFactory, cellNeighborhood);
    }
    @Test
    public void testNextCellState_1()
    {
        List<Cell> neighbors = new ArrayList<>();
        neighbors.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(0, 0)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(1, 0)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(2, 0)));

        automaton.insertStructure(neighbors);
        assertEquals(1, automaton.getAliveCount());
        automaton.beginCalculatingNextState();
        assertEquals(1, automaton.getAliveCount());
        automaton.endCalculatingNextState();
        assertEquals(1, automaton.getAliveCount());

        List<Cell> cells = new ArrayList<>();
        for(Cell cell : automaton)
        {
            cells.add(cell);
        }

        assertEquals(WireElectronState.WIRE, cells.get(0).getState());
        assertEquals(WireElectronState.ELECTRON_TAIL, cells.get(1).getState());
        assertEquals(WireElectronState.ELECTRON_HEAD, cells.get(2).getState());
        assertEquals(WireElectronState.VOID, cells.get(3).getState());
        assertEquals(WireElectronState.VOID, cells.get(4).getState());
        assertEquals(WireElectronState.VOID, cells.get(5).getState());
    }
    @Test
    public void testNextCellState_2()
    {
        List<Cell> neighbors = new ArrayList<>();
        for(int x = 0; x < 3; x++)
        {
            for(int y = 0; y < 2; y++)
                neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(x, y)));
        }

        automaton.insertStructure(neighbors);
        assertEquals(0, automaton.getAliveCount());
        automaton.beginCalculatingNextState();
        assertEquals(0, automaton.getAliveCount());
        automaton.endCalculatingNextState();
        assertEquals(0, automaton.getAliveCount());

        for(Cell cell : automaton)
        {
            assertEquals(WireElectronState.WIRE, cell.getState());
        }
    }
    @Test
    public void testNextCellState_3()
    {
        List<Cell> neighbors = new ArrayList<>();
        neighbors.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(0, 0)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(1, 0)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(0, 1)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(1, 1)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(2, 1)));

        automaton.insertStructure(neighbors);
        assertEquals(1, automaton.getAliveCount());
        automaton.beginCalculatingNextState();
        assertEquals(1, automaton.getAliveCount());
        automaton.endCalculatingNextState();
        assertEquals(3, automaton.getAliveCount());

        List<Cell> cells = new ArrayList<>();
        for(Cell cell : automaton)
        {
            cells.add(cell);
        }

        assertEquals(WireElectronState.WIRE, cells.get(0).getState());
        assertEquals(WireElectronState.ELECTRON_TAIL, cells.get(1).getState());
        assertEquals(WireElectronState.VOID, cells.get(2).getState());
        assertEquals(WireElectronState.ELECTRON_HEAD, cells.get(3).getState());
        assertEquals(WireElectronState.ELECTRON_HEAD, cells.get(4).getState());
        assertEquals(WireElectronState.ELECTRON_HEAD, cells.get(5).getState());
    }
}