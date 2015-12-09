package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.List;

public class QuadLife extends GameOfLife
{

    public QuadLife(List<Integer> surviveFactors, List<Integer> comeAliveFactors, int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(surviveFactors, comeAliveFactors, width, height, cellStateFactory, cellNeighborhood);
    }
}
