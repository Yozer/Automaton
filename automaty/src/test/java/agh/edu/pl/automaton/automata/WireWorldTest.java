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

import java.util.HashSet;
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
        CellNeighborhood cellNeighborhood = new MoorNeighborhood(1, false, 100, 100);
        CellStateFactory cellStateFactory = new UniformStateFactory(WireElectronState.VOID);
        automaton = new WireWorld(100, 100, cellStateFactory, cellNeighborhood);
    }
    @Test
    public void testNextCellState_voidRemainsVoid()
    {
        Set<Cell> neighbors = new HashSet<>();
        neighbors.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(5, 4)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 5)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(5, 6)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 7)));

        CellState newState = automaton.nextCellState(new Cell(WireElectronState.VOID, new Coords2D(3, 4)), neighbors);
        assertEquals(newState, WireElectronState.VOID);
    }
    @Test
    public void testNextCellState_headBecomsTail()
    {
        Set<Cell> neighbors = new HashSet<>();
        neighbors.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(5, 4)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 5)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(5, 6)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 7)));

        CellState newState = automaton.nextCellState(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(3, 4)), neighbors);
        assertEquals(newState, WireElectronState.ELECTRON_TAIL);
    }
    @Test
    public void testNextCellState_tailBecomesWire()
    {
        Set<Cell> neighbors = new HashSet<>();
        neighbors.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(5, 4)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 5)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(5, 6)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 7)));

        CellState newState = automaton.nextCellState(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(3, 4)), neighbors);
        assertEquals(newState, WireElectronState.WIRE);
    }
    @Test
    public void testNextCellState_conductorRemainsConductor_threeHeads()
    {
        Set<Cell> neighbors = new HashSet<>();
        neighbors.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(5, 4)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 5)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 6)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 7)));

        CellState newState = automaton.nextCellState(new Cell(WireElectronState.WIRE, new Coords2D(3, 4)), neighbors);
        assertEquals(newState, WireElectronState.WIRE);
    }
    @Test
    public void testNextCellState_conductorRemainsConductor_zeroHeads()
    {
        Set<Cell> neighbors = new HashSet<>();
        neighbors.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(5, 4)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(5, 5)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(5, 6)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(5, 7)));

        CellState newState = automaton.nextCellState(new Cell(WireElectronState.WIRE, new Coords2D(3, 4)), neighbors);
        assertEquals(newState, WireElectronState.WIRE);
    }
    @Test
    public void testNextCellState_conductorRemainsConductor_oneHead()
    {
        Set<Cell> neighbors = new HashSet<>();
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 4)));
        neighbors.add(new Cell(WireElectronState.WIRE, new Coords2D(5, 5)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 6)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(5, 7)));

        CellState newState = automaton.nextCellState(new Cell(WireElectronState.WIRE, new Coords2D(3, 4)), neighbors);
        assertEquals(newState, WireElectronState.ELECTRON_HEAD);
    }
    @Test
    public void testNextCellState_conductorRemainsConductor_twoeads()
    {
        Set<Cell> neighbors = new HashSet<>();
        neighbors.add(new Cell(WireElectronState.VOID, new Coords2D(5, 4)));
        neighbors.add(new Cell(WireElectronState.VOID, new Coords2D(5, 5)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 6)));
        neighbors.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(5, 7)));

        CellState newState = automaton.nextCellState(new Cell(WireElectronState.WIRE, new Coords2D(3, 4)), neighbors);
        assertEquals(newState, WireElectronState.ELECTRON_HEAD);
    }
}