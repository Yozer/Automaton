package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Dominik Baran on 2016-01-03.
 */
public class QuadLifeTest {

    QuadLife quadLife;
    @Before
    public void init() {
        quadLife = new QuadLife(3, 3, new UniformStateFactory(QuadState.DEAD), new MoorNeighborhood(1, false, 3, 3));
    }

    @Test
    public void testNextCellState_block() throws Exception {
        List<Cell> neighborsStates = new ArrayList<>(4);

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                neighborsStates.add(new Cell(QuadState.RED, new Coords2D(i, j)));
        quadLife.insertStructure(neighborsStates);
        assertEquals(4, quadLife.getAliveCount());
        quadLife.calculateNextState();
        for (Cell cell : quadLife) {
            if (cell.getCoords().equals(new Coords2D(0, 0)) || cell.getCoords().equals(new Coords2D(0, 1)) ||
                    cell.getCoords().equals(new Coords2D(1, 1)) || cell.getCoords().equals(new Coords2D(1, 0))) {
                assertEquals(QuadState.RED, cell.getState());
            } else {
                assertEquals(QuadState.DEAD, cell.getState());
            }
        }
    }
    @Test
    public void testNextCellState_moreGreenColors() throws Exception {
        List<Cell> neighborsStates = new ArrayList<>(4);

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                neighborsStates.add(new Cell(QuadState.GREEN, new Coords2D(i, j)));
        neighborsStates.remove(3);

        quadLife.insertStructure(neighborsStates);
        assertEquals(3, quadLife.getAliveCount());
        quadLife.calculateNextState();
        for (Cell cell : quadLife) {
            if (cell.getCoords().equals(new Coords2D(0, 0)) || cell.getCoords().equals(new Coords2D(0, 1)) ||
                    cell.getCoords().equals(new Coords2D(1, 1)) || cell.getCoords().equals(new Coords2D(1, 0))) {
                assertEquals(QuadState.GREEN, cell.getState());
            } else {
                assertEquals(QuadState.DEAD, cell.getState());
            }
        }
    }
    @Test
    public void testNextCellState_moreRedColors() throws Exception {
        List<Cell> neighborsStates = new ArrayList<>(4);

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                neighborsStates.add(new Cell(QuadState.RED, new Coords2D(i, j)));
        neighborsStates.remove(3);

        quadLife.insertStructure(neighborsStates);
        assertEquals(3, quadLife.getAliveCount());
        quadLife.calculateNextState();
        for (Cell cell : quadLife) {
            if (cell.getCoords().equals(new Coords2D(0, 0)) || cell.getCoords().equals(new Coords2D(0, 1)) ||
                    cell.getCoords().equals(new Coords2D(1, 1)) || cell.getCoords().equals(new Coords2D(1, 0))) {
                assertEquals(QuadState.RED, cell.getState());
            } else {
                assertEquals(QuadState.DEAD, cell.getState());
            }
        }
    }
    @Test
    public void testNextCellState_moreBlueColors() throws Exception {
        List<Cell> neighborsStates = new ArrayList<>(4);

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                neighborsStates.add(new Cell(QuadState.BLUE, new Coords2D(i, j)));
        neighborsStates.remove(3);

        quadLife.insertStructure(neighborsStates);
        assertEquals(3, quadLife.getAliveCount());
        quadLife.calculateNextState();
        for (Cell cell : quadLife) {
            if (cell.getCoords().equals(new Coords2D(0, 0)) || cell.getCoords().equals(new Coords2D(0, 1)) ||
                    cell.getCoords().equals(new Coords2D(1, 1)) || cell.getCoords().equals(new Coords2D(1, 0))) {
                assertEquals(QuadState.BLUE, cell.getState());
            } else {
                assertEquals(QuadState.DEAD, cell.getState());
            }
        }
    }
    @Test
    public void testNextCellState_moreYellowColors() throws Exception {
        List<Cell> neighborsStates = new ArrayList<>(4);

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                neighborsStates.add(new Cell(QuadState.YELLOW, new Coords2D(i, j)));
        neighborsStates.remove(3);

        quadLife.insertStructure(neighborsStates);
        assertEquals(3, quadLife.getAliveCount());
        quadLife.calculateNextState();
        for (Cell cell : quadLife) {
            if (cell.getCoords().equals(new Coords2D(0, 0)) || cell.getCoords().equals(new Coords2D(0, 1)) ||
                    cell.getCoords().equals(new Coords2D(1, 1)) || cell.getCoords().equals(new Coords2D(1, 0))) {
                assertEquals(QuadState.YELLOW, cell.getState());
            } else {
                assertEquals(QuadState.DEAD, cell.getState());
            }
        }
    }
    @Test
    public void testNextCellState_equalColorsCount() throws Exception {
        List<Cell> neighborsStates = new ArrayList<>(4);

        neighborsStates.add(new Cell(QuadState.RED, new Coords2D(0, 0)));
        neighborsStates.add(new Cell(QuadState.BLUE, new Coords2D(0, 1)));
        neighborsStates.add(new Cell(QuadState.GREEN, new Coords2D(1, 0)));

        quadLife.insertStructure(neighborsStates);
        assertEquals(3, quadLife.getAliveCount());
        quadLife.calculateNextState();
        for (Cell cell : quadLife) {
            if (cell.getCoords().equals(new Coords2D(1, 0))) {
                assertEquals(QuadState.GREEN, cell.getState());
            } else if(cell.getCoords().equals(new Coords2D(0, 1))) {
                assertEquals(QuadState.BLUE,cell.getState());
            } else if(cell.getCoords().equals(new Coords2D(0, 0))) {
                assertEquals(QuadState.RED,cell.getState());
            } else if(cell.getCoords().equals(new Coords2D(1, 1))) {
                assertEquals(QuadState.YELLOW, cell.getState());
            }
            else {
                assertEquals(QuadState.DEAD, cell.getState());
            }
        }
    }
}