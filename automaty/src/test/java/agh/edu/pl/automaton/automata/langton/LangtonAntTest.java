package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryAntState;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dominik on 2015-12-26.
 */
public class LangtonAntTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    LangtonAnt automaton;

    @Before
    public void init() {
        CellNeighborhood cellNeighborhood = new MoorNeighborhood(1, true, 3, 3);
        CellStateFactory cellStateFactory = new UniformStateFactory(new BinaryAntState(BinaryState.DEAD));
        automaton = new LangtonAnt(3, 3, cellStateFactory, cellNeighborhood);
    }

    @Test
    public void testAddAnt() throws Exception {
        automaton.addAnt(new Coords2D(2, 2), Color.blue, AntDirection.NORTH);
        automaton.addAnt(new Coords2D(1, 1), Color.red, AntDirection.NORTH);
        assertEquals(2, automaton.getAnts().size());
    }

    @Test
    public void testAddAnt_outOfPlane() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Ant has to be inside plane");
        automaton.addAnt(new Coords2D(1, 1), Color.red, AntDirection.NORTH);
        assertEquals(1, automaton.getAnts().size());
        automaton.addAnt(new Coords2D(2, 3), Color.blue, AntDirection.NORTH);
    }

    @Test
    public void testNextCellState_startWest() throws Exception {
        Ant ant = automaton.addAnt(new Coords2D(1, 1), Color.red, AntDirection.WEST);
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(1, 0), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(1, 0), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(2, 0), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(2, 0), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(2, 1), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(2, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(1, 2), ant.getCoordinates());
    }

    @Test
    public void testNextCellState_startNorth() throws Exception {
        Ant ant = automaton.addAnt(new Coords2D(1, 1), Color.red, AntDirection.NORTH);
        assertEquals(ant.getCoordinates(), new Coords2D(1, 1));

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(2, 1), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(2, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(2, 2), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(2, 2), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(1, 2), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(1, 2), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(0, 1), ant.getCoordinates());
    }

    @Test
    public void testNextCellState_startEast() throws Exception {
        Ant ant = automaton.addAnt(new Coords2D(1, 1), Color.red, AntDirection.EAST);
        assertEquals(ant.getCoordinates(), new Coords2D(1, 1));

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(1, 2), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(1, 2), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(0, 2), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(0, 2), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(0, 1), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(0, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(1, 0), ant.getCoordinates());
    }

    @Test
    public void testNextCellState_startSouth() throws Exception {
        Ant ant = automaton.addAnt(new Coords2D(1, 1), Color.red, AntDirection.SOUTH);
        assertEquals(ant.getCoordinates(), new Coords2D(1, 1));

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(0, 1), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.WEST, ant.getAntState());
        assertEquals(new Coords2D(0, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(0, 0), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.NORTH, ant.getAntState());
        assertEquals(new Coords2D(0, 0), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(1, 0), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(1, 0), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());

        automaton.beginCalculatingNextState();
        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.SOUTH, ant.getAntState());
        assertEquals(new Coords2D(1, 1), ant.getCoordinates());
        automaton.endCalculatingNextState();

        ant = automaton.getAnts().stream().findAny().get();
        assertEquals(AntDirection.EAST, ant.getAntState());
        assertEquals(new Coords2D(2, 1), ant.getCoordinates());
    }
}