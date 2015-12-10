package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.automata.QuadLife;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Dominik on 2015-12-09.
 */
public class QuadLifeTest
{
    QuadLife quadLife;
    @Before
    public void init()
    {
        quadLife = new QuadLife(500, 500, new UniformStateFactory(QuadState.DEAD), new MoorNeighborhood(1, false, 500, 500));
    }

    @Test
    public void testNextCellState_cellIsDead_allNeigborsAreDead_remainDead()
    {
        QuadState state = QuadState.DEAD;
        Set<Cell> neighborsStates = new HashSet<>();

        for(int i = 0; i < 8; i++)
            neighborsStates.add(new Cell(QuadState.DEAD, new Coords1D(i)));

        QuadState resultState = (QuadState) quadLife.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
        assertEquals(QuadState.DEAD, resultState);
    }

    @Test
    public void testNextCellState_cellIsDead_overpopulated_remainDead()
    {
        QuadState state = QuadState.DEAD;
        Set<Cell> neighborsStates = new HashSet<>();

        for(int i = 0; i < 8; i++)
            neighborsStates.add(new Cell(QuadState.GREEN, new Coords1D(i)));

        QuadState resultState = (QuadState) quadLife.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
        assertEquals(QuadState.DEAD, resultState);
    }
    @Test
    public void testNextCellState_cellIsDead_threeTheSameColor_shouldBeAliveWithTheSameColor()
    {
        List<QuadState> states = Arrays.asList(QuadState.BLUE, QuadState.RED, QuadState.YELLOW, QuadState.GREEN);

        for(QuadState testState : states)
        {
            QuadState state = QuadState.DEAD;
            Set<Cell> neighborsStates = new HashSet<>();

            for (int i = 0; i < 3; i++)
                neighborsStates.add(new Cell(testState, new Coords1D(i)));
            for (int i = 0; i < 5; i++)
                neighborsStates.add(new Cell(QuadState.DEAD, new Coords1D(i)));

            QuadState resultState = (QuadState) quadLife.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
            assertEquals(testState, resultState);
        }
    }
    @Test
    public void testNextCellState_cellIsDead_threeDiffrentNeighbors_shouldHaveDiffrentColor()
    {
        QuadState state = QuadState.DEAD;
        Set<Cell> neighborsStates = new HashSet<>();

        for(int i = 0; i < 5; i++)
            neighborsStates.add(new Cell(QuadState.DEAD, new Coords1D(i)));
        neighborsStates.add(new Cell(QuadState.RED, new Coords1D(6)));
        neighborsStates.add(new Cell(QuadState.GREEN, new Coords1D(7)));
        neighborsStates.add(new Cell(QuadState.YELLOW, new Coords1D(8)));

        QuadState resultState = (QuadState) quadLife.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
        assertEquals(QuadState.BLUE, resultState);
    }
    @Test
    public void testNextCellState_cellIsAlive_overpopulated_shoudDie()
    {
        QuadState state = QuadState.GREEN;
        Set<Cell> neighborsStates = new HashSet<>();

        for(int i = 0; i < 8; i++)
            neighborsStates.add(new Cell(QuadState.GREEN, new Coords1D(i)));

        QuadState resultState = (QuadState) quadLife.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
        assertEquals(QuadState.DEAD, resultState);
    }
    @Test
    public void testNextCellState_cellIsAlive_threeNeighbors_shoudRemainAlive()
    {
        QuadState state = QuadState.GREEN;
        Set<Cell> neighborsStates = new HashSet<>();

        for(int i = 0; i < 5; i++)
            neighborsStates.add(new Cell(QuadState.DEAD, new Coords1D(i)));
        neighborsStates.add(new Cell(QuadState.RED, new Coords1D(6)));
        neighborsStates.add(new Cell(QuadState.GREEN, new Coords1D(7)));
        neighborsStates.add(new Cell(QuadState.YELLOW, new Coords1D(8)));

        QuadState resultState = (QuadState) quadLife.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
        assertEquals(QuadState.GREEN, resultState);
    }
}