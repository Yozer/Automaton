package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.WireElectronState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.GeneralStateFactory;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GameOfLifeTest {
    Automaton gameOfLifeStandard;

    @Before
    public void init() {
        int width = 2;
        int height = 3;
        CellStateFactory stateFactory = new UniformStateFactory(BinaryState.DEAD);
        CellNeighborhood neighborhood = new MoorNeighborhood(1, false, width, height);

        gameOfLifeStandard = new GameOfLife(new HashSet<>(Arrays.asList(2, 3)), new HashSet<>(Collections.singletonList(3)), width, height, stateFactory, neighborhood);
    }

    @Test
    public void testNextCellState_cellIsDead_allNeighborsAreDead_remainDead() {
        gameOfLifeStandard.calculateNextState();
        for (Cell cell : gameOfLifeStandard)
            assertEquals(BinaryState.DEAD, cell.getState());
    }

    @Test
    public void testNextState_threeAlive() {
        List<Cell> neighborsStates = new ArrayList<>(3);

        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(0, 1)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(0, 2)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(0, 0)));

        assertEquals(0, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.insertStructure(neighborsStates);
        assertEquals(3, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.calculateNextState();
        assertEquals(2, gameOfLifeStandard.getAliveCount());

        List<Cell> result = new ArrayList<>();
        for (Cell cell : gameOfLifeStandard)
            result.add(cell);

        assertEquals(result.get(0).getState(), BinaryState.DEAD);
        assertEquals(result.get(1).getState(), BinaryState.DEAD);
        assertEquals(result.get(2).getState(), BinaryState.ALIVE);
        assertEquals(result.get(3).getState(), BinaryState.ALIVE);
        assertEquals(result.get(4).getState(), BinaryState.DEAD);
        assertEquals(result.get(5).getState(), BinaryState.DEAD);
    }

    @Test
    public void testNextState_allAlive() {
        List<Cell> neighborsStates = new ArrayList<>(6);

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 3; j++)
                neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(i, j)));

        assertEquals(0, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.insertStructure(neighborsStates);
        assertEquals(6, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.beginCalculatingNextState();
        assertEquals(6, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.endCalculatingNextState();
        assertEquals(4, gameOfLifeStandard.getAliveCount());

        List<Cell> result = new ArrayList<>();
        for (Cell cell : gameOfLifeStandard)
            result.add(cell);

        assertEquals(result.get(0).getState(), BinaryState.ALIVE);
        assertEquals(result.get(1).getState(), BinaryState.ALIVE);
        assertEquals(result.get(2).getState(), BinaryState.DEAD);
        assertEquals(result.get(3).getState(), BinaryState.DEAD);
        assertEquals(result.get(4).getState(), BinaryState.ALIVE);
        assertEquals(result.get(5).getState(), BinaryState.ALIVE);
    }
    @Test
    public void testStateColors() {
        assertEquals(Color.BLACK, BinaryState.DEAD.toColor());
        assertEquals(Color.WHITE, BinaryState.ALIVE.toColor());
    }
    @Test
    public void testNextState_500AliveMultithreaded() {
        int width = 100;
        int height = 100;
        Map<CellCoordinates, CellState> initStates = new HashMap<>();
        for(int i = 0; i < 100; i++) {
            for(int j = 0; j < 100; j++) {
                initStates.put(new Coords2D(i, j), BinaryState.DEAD);
            }
        }
        CellStateFactory stateFactory = new GeneralStateFactory(initStates);
        CellNeighborhood neighborhood = new MoorNeighborhood(1, false, width, height);

        gameOfLifeStandard = new GameOfLife(new HashSet<>(Arrays.asList(2, 3)), new HashSet<>(Collections.singletonList(3)), width, height, stateFactory, neighborhood);
        List<Cell> neighborsStates = new ArrayList<>(6);

        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(0, 0)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(0, 1)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(1, 0)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(1, 1)));

        assertEquals(0, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.insertStructure(neighborsStates);
        assertEquals(4, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.beginCalculatingNextState();
        assertEquals(4, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.endCalculatingNextState();
        assertEquals(4, gameOfLifeStandard.getAliveCount());

        for(int i = 0; i < 100; i++) {
            assertEquals(4, gameOfLifeStandard.getAliveCount());
            gameOfLifeStandard.beginCalculatingNextState();
            assertEquals(4, gameOfLifeStandard.getAliveCount());
            gameOfLifeStandard.endCalculatingNextState();
            assertEquals(4, gameOfLifeStandard.getAliveCount());

            List<Cell> result = new ArrayList<>();
            for (Cell cell : gameOfLifeStandard)
                result.add(cell);

            assertEquals(result.get(0).getState(), BinaryState.ALIVE);
            assertEquals(result.get(1).getState(), BinaryState.ALIVE);
            assertEquals(result.get(100).getState(), BinaryState.ALIVE);
            assertEquals(result.get(101).getState(), BinaryState.ALIVE);
            for(int j = 0; j < width*height; j++) {
                if(j == 0 || j == 1 || j == 100 || j == 101) continue;
                assertEquals(BinaryState.DEAD, result.get(j).getState());
            }
        }
    }
    @Test
    public void testNextState_fourAlive() {
        List<Cell> neighborsStates = new ArrayList<>(6);

        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(0, 0)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(0, 1)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(1, 0)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(1, 1)));

        assertEquals(0, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.insertStructure(neighborsStates);
        assertEquals(4, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.beginCalculatingNextState();
        assertEquals(4, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.endCalculatingNextState();
        assertEquals(4, gameOfLifeStandard.getAliveCount());

        List<Cell> result = new ArrayList<>();
        for (Cell cell : gameOfLifeStandard)
            result.add(cell);

        assertEquals(result.get(0).getState(), BinaryState.ALIVE);
        assertEquals(result.get(1).getState(), BinaryState.ALIVE);
        assertEquals(result.get(2).getState(), BinaryState.ALIVE);
        assertEquals(result.get(3).getState(), BinaryState.ALIVE);
        assertEquals(result.get(4).getState(), BinaryState.DEAD);
        assertEquals(result.get(5).getState(), BinaryState.DEAD);
    }
    @Test
    public void testNextState_changeNeighborhood() {
        List<Cell> neighborsStates = new ArrayList<>(6);

        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(0, 0)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(0, 1)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(1, 0)));
        neighborsStates.add(new Cell(BinaryState.ALIVE, new Coords2D(1, 1)));

        assertEquals(0, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.insertStructure(neighborsStates);
        assertEquals(4, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.beginCalculatingNextState();
        assertEquals(4, gameOfLifeStandard.getAliveCount());
        gameOfLifeStandard.endCalculatingNextState();
        assertEquals(4, gameOfLifeStandard.getAliveCount());

        List<Cell> result = new ArrayList<>();
        for (Cell cell : gameOfLifeStandard)
            result.add(cell);

        assertEquals(result.get(0).getState(), BinaryState.ALIVE);
        assertEquals(result.get(1).getState(), BinaryState.ALIVE);
        assertEquals(result.get(2).getState(), BinaryState.ALIVE);
        assertEquals(result.get(3).getState(), BinaryState.ALIVE);
        assertEquals(result.get(4).getState(), BinaryState.DEAD);
        assertEquals(result.get(5).getState(), BinaryState.DEAD);

        gameOfLifeStandard.setNeighborhood(new MoorNeighborhood(0, false, 2, 3));
        gameOfLifeStandard.calculateNextState();
        for (Cell cell : gameOfLifeStandard)
            assertEquals(BinaryState.DEAD, cell.getState());

        int count = 0;
        Iterator<Cell> cellIterator = gameOfLifeStandard.iteratorChangedOnly();
        while (cellIterator.hasNext()) {
            count++;
            cellIterator.next();
        }
        assertEquals(4, count);
        try {
            cellIterator.next();
            assertTrue(false);
        } catch (NoSuchElementException e) {
        }
        try {
            cellIterator.remove();
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
        }
    }
}