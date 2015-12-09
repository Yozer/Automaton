package agh.edu.pl.automaton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryAntState;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LangtonAnt extends Automaton2Dim
{
    private List<Ant> ants = new ArrayList<>();

    protected LangtonAnt(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(width, height, cellStateFactory, cellNeighborhood);
    }

    public void addAnt(Coords2D antCoords, Color antColor, AntState antRotation)
    {
        Ant ant = new Ant(antCoords, antRotation, antColor);
        ants.add(ant);
    }

    @Override
    protected Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        return new LangtonAnt(getWidth(), getHeight(), cellStateFactory, cellNeighborhood);
    }

    @Override
    protected CellState nextCellState(Cell cell, Set<Cell> neighborsStates)
    {
        Optional<Ant> anyAnt = ants.stream().filter(t -> t.coordinates.equals(cell.getCoords())).findAny();

        if(!anyAnt.isPresent())
            return cell.getState();

        Ant ant = anyAnt.get();
        BinaryAntState state = (BinaryAntState) cell.getState();

        if(state.getBinaryState() == BinaryState.ALIVE)
        {
            ant.rotateRight();
            state = new BinaryAntState(BinaryState.DEAD);
        }
        else if(state.getBinaryState() == BinaryState.DEAD)
        {
            ant.rotateLeft();
            state = new BinaryAntState(BinaryState.ALIVE, ant.getAntColor());
        }

        ant.move();
        return state;
    }

    public class Ant
    {
        private Coords2D coordinates;
        private AntState antState;
        private Color antColor;

        private Ant(Coords2D coordinates, AntState antState, Color antColor)
        {
            this.coordinates = coordinates;
            this.antState = antState;
            this.antColor = antColor;
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

        public Color getAntColor()
        {
            return antColor;
        }
    }

    public enum AntState
    {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
