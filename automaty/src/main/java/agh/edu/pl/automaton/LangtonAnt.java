package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.AntState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.util.Set;

public class LangtonAnt extends Automaton2Dim
{
    protected LangtonAnt(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(width, height, cellStateFactory, cellNeighborhood);
    }

    @Override
    protected Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        return new LangtonAnt(getWidth(), getHeight(), cellStateFactory, cellNeighborhood);
    }

    @Override
    protected CellState nextCellState(Cell cell, Set<Cell> neighborsStates)
    {

    }

    private class Ant
    {
        private Coords2D coordinates;
        private AntState antState;

        private Ant(Coords2D coordinates, AntState antState)
        {
            this.coordinates = coordinates;
            this.antState = antState;
        }

        public void rotateLeft()
        {
            if(antState == AntState.NORTH)
                antState = AntState.WEST;
            else if(antState == AntState.WEST)
                antState = AntState.SOUTH;
            else if(antState == AntState.SOUTH)
                antState = AntState.EAST;
            else if(antState == AntState.EAST)
                antState = AntState.NORTH;
        }
        public void rotateRight()
        {
            if(antState == AntState.NORTH)
                antState = AntState.EAST;
            else if(antState == AntState.WEST)
                antState = AntState.NORTH;
            else if(antState == AntState.SOUTH)
                antState = AntState.WEST;
            else if(antState == AntState.EAST)
                antState = AntState.SOUTH;
        }
        public void move()
        {
            int x = coordinates.getX();
            int y = coordinates.getY();

            if(antState == AntState.NORTH)
                y--;
            else if(antState == AntState.SOUTH)
                y++;
            else if(antState == AntState.WEST)
                x--;
            else if(antState == AntState.EAST)
                x++;

            if(x < 0 || x >= getWidth())
                x = Math.floorMod(x, getWidth());
            if(y < 0 || y >= getHeight())
                y = Math.floorMod(y, getHeight());

            coordinates = new Coords2D(x, y);
        }
    }
}
