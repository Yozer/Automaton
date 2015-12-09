package agh.edu.pl.automaton;

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
import org.junit.Test;

import javax.naming.spi.StateFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;


public class GameOfLifeTest
{

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

        for(int p = 0; p < 50; p++)
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

}