package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.automata.GameOfLife;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.GeneralStateFactory;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


public class GameOfLifeTest
{
    GameOfLife gameOfLifeStandard;
    @Before
    public void init()
    {
        int width = 2;
        int height = 3;
        CellStateFactory stateFactory = new UniformStateFactory(BinaryState.ALIVE);
        CellNeighborhood neighborhood = new MoorNeighborhood(2, false, width, height);

        gameOfLifeStandard = new GameOfLife(Arrays.asList(2,3), Collections.singletonList(3), width, height, stateFactory, neighborhood);
    }
    @Test
    public void testNextCellState_cellIsDead_allNeigborsAreDead_remainDead()
    {
        BinaryState state = BinaryState.ALIVE;
        Set<Cell> neighborsStates = new HashSet<>();

        for(int i = 0; i < 8; i++)
            neighborsStates.add(new Cell(BinaryState.DEAD, new Coords1D(i)));

        BinaryState resultState = (BinaryState) gameOfLifeStandard.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
        assertEquals(BinaryState.DEAD, resultState);
    }
    @Test
    public void testNextCellState_cellIsAlive_twoAliveNeigbors_remainAlive()
    {
        BinaryState state = BinaryState.ALIVE;
        Set<Cell> neighborsStates = new HashSet<>();

        for(int i = 0; i < 6; i++)
            neighborsStates.add(new Cell(BinaryState.DEAD, new Coords1D(i)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords1D(6)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords1D(7)));

        BinaryState resultState = (BinaryState) gameOfLifeStandard.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
        assertEquals(BinaryState.ALIVE, resultState);
    }
    @Test
    public void testNextCellState_cellIsAlive_threeAliveNeigbors_remainAlive()
    {
        BinaryState state = BinaryState.ALIVE;
        Set<Cell> neighborsStates = new HashSet<>();

        for(int i = 0; i < 5; i++)
            neighborsStates.add(new Cell(BinaryState.DEAD, new Coords1D(i)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords1D(5)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords1D(6)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords1D(7)));

        BinaryState resultState = (BinaryState) gameOfLifeStandard.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
        assertEquals(BinaryState.ALIVE, resultState);
    }
    @Test
    public void testNextCellState_cellIsDead_threeAliveNeigbors_becomeAlive()
    {
        BinaryState state = BinaryState.DEAD;
        Set<Cell> neighborsStates = new HashSet<>();

        for(int i = 0; i < 5; i++)
            neighborsStates.add(new Cell(BinaryState.DEAD, new Coords1D(i)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords1D(5)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords1D(6)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords1D(7)));

        BinaryState resultState = (BinaryState) gameOfLifeStandard.nextCellState(new Cell(state, new Coords2D(3, 4)), neighborsStates);
        assertEquals(BinaryState.ALIVE, resultState);
    }

    @Test
    public void testNewInstance_all_alive() throws Exception
    {
        int width = 2;
        int height = 3;
        CellStateFactory stateFactory = new UniformStateFactory(BinaryState.ALIVE);
        CellNeighborhood neighborhood = new MoorNeighborhood(2, false, width, height);
        GameOfLife gameOfLife = new GameOfLife(Arrays.asList(2,3), Collections.singletonList(3), width, height, stateFactory, neighborhood);

        assertEquals(gameOfLife.getWidth(), width);
        assertEquals(gameOfLife.getHeight(), height);

        int count = 0;
        for (Cell cell : gameOfLife)
        {
            assertEquals(BinaryState.ALIVE, cell.getState());
            count++;
        }

        assertEquals(width*height, count);
    }
    @Test
    public void testNewInstance_and_nextState_block() throws Exception
    {
        int width = 4;
        int height = 4;

        HashMap<CellCoordinates, CellState> initialStates = new HashMap<>();
        // block
        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                initialStates.put(new Coords2D(x, y),
                        x == 0 || y == 0 || x == width - 1 || y == height - 1 ? BinaryState.DEAD : BinaryState.ALIVE);
            }
        }

        CellStateFactory stateFactory = new GeneralStateFactory(initialStates);
        CellNeighborhood neighborhood = new MoorNeighborhood(1, false, width, height);
        Automaton gameOfLife = new GameOfLife(Arrays.asList(2,3), Collections.singletonList(3), width, height, stateFactory, neighborhood);

        for(int p = 0; p < 5; p++)
        {
            int count = 0;
            int countAlive = 0;
            for (Cell cell : gameOfLife)
            {
                count++;
                if(cell.getState() == BinaryState.ALIVE)
                    countAlive++;
            }

            assertEquals(16, count);
            assertEquals(4, countAlive);

            gameOfLife = gameOfLife.nextState();  // shoudn't change
        }
    }

    @Test
    public void testNextState_blinker() throws Exception
    {
        int width = 50;
        int height = 50;

        HashMap<CellCoordinates, CellState> blinker = new HashMap<>();
        // blinker
        blinker.put(new Coords2D(1, 0), BinaryState.ALIVE);
        blinker.put(new Coords2D(1, 1), BinaryState.ALIVE);
        blinker.put(new Coords2D(1, 2), BinaryState.ALIVE);

        HashMap<CellCoordinates, CellState> blinker2 = new HashMap<>();
        // blinker2
        blinker2.put(new Coords2D(0, 1), BinaryState.ALIVE);
        blinker2.put(new Coords2D(1, 1), BinaryState.ALIVE);
        blinker2.put(new Coords2D(2, 1), BinaryState.ALIVE);

        CellStateFactory stateFactory = new UniformStateFactory(BinaryState.DEAD);
        CellNeighborhood neighborhood = new MoorNeighborhood(1, true, width, height);
        Automaton gameOfLife = new GameOfLife(Arrays.asList(2,3), Collections.singletonList(3), width, height, stateFactory, neighborhood);

        gameOfLife.insertStructure(blinker);

        for(int p = 0; p < 5; p++)
        {
            int count = 0;
            int countAlive = 0;
            for (Cell cell : gameOfLife)
            {
                count++;
                if(cell.getState() == BinaryState.ALIVE)
                {
                    if(Math.floorMod(p, 2) == 0 && blinker.containsKey(cell.getCoords()))
                    {
                        countAlive++;
                    }
                    else if(blinker2.containsKey(cell.getCoords()))
                    {
                        countAlive++;
                    }

                }
            }

            assertEquals(width*height, count);
            assertEquals(3, countAlive);

            gameOfLife = gameOfLife.nextState();
        }
    }

    @Test
    public void testSetSurviveFactors() throws Exception
    {
        gameOfLifeStandard.setSurviveFactors(Arrays.asList(1,5,7));
        assertArrayEquals(gameOfLifeStandard.getSurviveFactors().toArray() , Arrays.asList(1,5,7).toArray());
    }

    @Test
    public void testSetComeAliveFactors() throws Exception
    {
        gameOfLifeStandard.setComeAliveFactors(Arrays.asList(1,5,7));
        assertArrayEquals(gameOfLifeStandard.getComeAliveFactors().toArray(), Arrays.asList(1,5,7).toArray());
    }
}