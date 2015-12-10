package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.cells.Cell;
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

    public LangtonAnt(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        super(width, height, cellStateFactory, cellNeighborhood);
    }

    public void addAnt(Coords2D antCoords, Color antColor, AntState antRotation)
    {
        Ant ant = new Ant(antCoords, antRotation, antColor, getWidth(), getHeight());
        ants.add(ant);
    }
    public List<Ant> getAnts()
    {
        List<Ant> antsList = new ArrayList<>(ants.size());
        for(Ant ant : ants)
        {
            antsList.add(ant);
        }

        return antsList;
    }

    @Override
    protected Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood)
    {
        return new LangtonAnt(getWidth(), getHeight(), cellStateFactory, cellNeighborhood);
    }

    @Override
    protected CellState nextCellState(Cell cell, Set<Cell> neighborsStates)
    {
        Optional<Ant> anyAnt = ants.stream().filter(t -> t.getCoordinates().equals(cell.getCoords())).findAny();

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


}

