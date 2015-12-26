package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.NeighborhoodArray;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryAntState;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class LangtonAnt extends Automaton2Dim
{
    private List<Ant> ants = new ArrayList<>();

    public LangtonAnt(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(width, height, cellStateFactory, cellNeighborhood);
    }

    public Ant addAnt(Coords2D antCoords, Color antColor, AntState antRotation)
    {
        if(antCoords.getX() < 0 || antCoords.getY() < 0 || antCoords.getX() >= getWidth() || antCoords.getY() >= getHeight())
        {
            throw new IllegalArgumentException("Ant has to be inside plane");
        }

        Ant ant = new Ant(antCoords, antRotation, antColor, getWidth(), getHeight());
        ants.add(ant);
        insertStructure(Collections.singletonList(new Cell(new BinaryAntState(BinaryState.DEAD), antCoords)));
        return ant;
    }
    public List<Ant> getAnts()
    {
        List<Ant> antsList = new ArrayList<>(ants.size());
        antsList.addAll(ants.stream().collect(Collectors.toList()));
        return antsList;
    }

    @Override
    protected CellState nextCellState(Cell cell, NeighborhoodArray neighborsStates)
    {
        Ant ant = null;
        for(Ant a : ants)
        {
            if(a.getCoordinates().equals(cell.getCoords()))
            {
                ant = a;
                break;
            }
        }
        if(ant == null)
            return cell.getState();

        BinaryAntState state = (BinaryAntState) cell.getState();

        if(state.getBinaryState() == BinaryState.ALIVE)
        {
            ant.rotateLeft();
            state = new BinaryAntState(BinaryState.DEAD);
        }
        else if(state.getBinaryState() == BinaryState.DEAD)
        {
            ant.rotateRight();
            state = new BinaryAntState(BinaryState.ALIVE, ant.getAntColor());
        }

        ant.move();
        return state;
    }

    @Override
    protected boolean cellIsAlive(CellState state)
    {
        return state == BinaryState.ALIVE;
    }

    @Override
    protected boolean cellChangedToAlive(CellState newState, CellState oldState)
    {
        return oldState == BinaryState.DEAD && newState == BinaryState.ALIVE;
    }

    @Override
    protected boolean cellChangedToDead(CellState newState, CellState oldState)
    {
        return newState == BinaryState.DEAD && oldState == BinaryState.ALIVE;
    }
}

