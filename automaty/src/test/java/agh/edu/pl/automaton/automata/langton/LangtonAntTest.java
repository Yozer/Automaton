package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.automata.langton.LangtonAnt;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryAntState;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Dominik on 2015-12-10.
 */
public class LangtonAntTest
{
    Automaton automaton;
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

    }

    @Test
    public void testNewInstance() throws Exception
    {

    }

    @Test
    public void testNextCellState() throws Exception
    {

    }
}