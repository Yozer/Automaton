package agh.edu.pl.automaton.automata.langton;


import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryAntState;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Dominik on 2015-12-10.
 */
public class LangtonAntTest
{
    LangtonAnt automaton;
    @Before
    public void init() throws Exception
    {
        MoorNeighborhood moorNeighborhood = new MoorNeighborhood(0, true, 200, 200);
        CellStateFactory cellStateFactory = new UniformStateFactory(new BinaryAntState(BinaryState.DEAD));
        automaton = new LangtonAnt(200, 200, cellStateFactory, moorNeighborhood);
    }

    @Test
    public void testAddAnt() throws Exception
    {
        automaton.addAnt(new Coords2D(3, 3), Color.blue, AntState.NORTH);
        List<Ant> ants = automaton.getAnts();

        assertEquals(1, ants.size());
        assertEquals(new Coords2D(3, 3), ants.get(0).getCoordinates());
        assertEquals(Color.blue, ants.get(0).getAntColor());
        assertEquals(AntState.NORTH, ants.get(0).getAntState());
    }
    @Test(expected = IllegalArgumentException.class )
    public void testAddAnt_fail() throws Exception
    {
        automaton.addAnt(new Coords2D(3323, 33232), Color.blue, AntState.NORTH);
    }


    @Test
    public void testNextCellState_antOnDead_shouldMoveDown() throws Exception
    {
        Ant ant = automaton.addAnt(new Coords2D(3, 3), Color.blue, AntState.EAST);
        BinaryAntState result = (BinaryAntState) automaton.nextCellState(new Cell(new BinaryAntState(BinaryState.DEAD), ant.getCoordinates()), new HashSet<>());

        assertEquals(result.getBinaryState(), BinaryState.ALIVE);
        assertEquals(ant.getAntState(), AntState.SOUTH);
        assertEquals(result.getCellColor(), ant.getAntColor());
        assertEquals(ant.getCoordinates(), new Coords2D(3, 4));
    }
    @Test
    public void testNextCellState_antOnDead_shouldMoveLeft() throws Exception
    {
        Ant ant = automaton.addAnt(new Coords2D(0, 5), Color.blue, AntState.SOUTH);
        BinaryAntState result = (BinaryAntState) automaton.nextCellState(new Cell(new BinaryAntState(BinaryState.DEAD), ant.getCoordinates()), new HashSet<>());

        assertEquals(result.getBinaryState(), BinaryState.ALIVE);
        assertEquals(result.getCellColor(), ant.getAntColor());
        assertEquals(ant.getAntState(), AntState.WEST);
        assertEquals(ant.getCoordinates(), new Coords2D(199, 5));
    }
    @Test
    public void testNextCellState_antOnAlive_shouldMoveDown() throws Exception
    {
        Ant ant = automaton.addAnt(new Coords2D(55, 199), Color.blue, AntState.WEST);
        BinaryAntState result = (BinaryAntState) automaton.nextCellState(new Cell(new BinaryAntState(BinaryState.ALIVE), ant.getCoordinates()), new HashSet<>());

        assertEquals(result.getBinaryState(), BinaryState.DEAD);
        assertEquals(result.getCellColor(), Color.BLACK);
        assertEquals(ant.getAntState(), AntState.SOUTH);
        assertEquals(ant.getCoordinates(), new Coords2D(55, 0));
    }

    @Test
    public void testNextCellState_twoAntsOnTheSameCell_cellIsAlive() throws Exception
    {
        Ant ant1 = automaton.addAnt(new Coords2D(199, 61), Color.blue, AntState.WEST);
        Ant ant2 = automaton.addAnt(new Coords2D(199, 61), Color.blue, AntState.SOUTH);
        BinaryAntState result1 = (BinaryAntState) automaton.nextCellState(new Cell(new BinaryAntState(BinaryState.ALIVE), ant1.getCoordinates()), new HashSet<>());
        BinaryAntState result2 = (BinaryAntState) automaton.nextCellState(new Cell(new BinaryAntState(BinaryState.ALIVE), ant2.getCoordinates()), new HashSet<>());

        assertEquals(result1.getBinaryState(), BinaryState.DEAD);
        assertEquals(result1.getCellColor(), Color.BLACK);
        assertEquals(ant1.getAntState(), AntState.SOUTH);
        assertEquals(ant1.getCoordinates(), new Coords2D(199, 62));

        assertEquals(result2.getBinaryState(), BinaryState.DEAD);
        assertEquals(result2.getCellColor(), Color.BLACK);
        assertEquals(ant2.getAntState(), AntState.EAST);
        assertEquals(ant2.getCoordinates(), new Coords2D(0, 61));
    }
}