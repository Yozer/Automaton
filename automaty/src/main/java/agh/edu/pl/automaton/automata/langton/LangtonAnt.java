package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.NeighborhoodArray;
import agh.edu.pl.automaton.cells.states.BinaryAntState;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LangtonAnt extends Automaton2Dim {
    private List<Ant> currentAnts = new ArrayList<>();
    private List<Ant> nextStateAnts = new ArrayList<>();
    private int idCounter = 0;

    public LangtonAnt(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood) {
        super(width, height, cellStateFactory, cellNeighborhood);
    }

    public Ant addAnt(Coords2D antCoords, Color antColor, AntState antRotation) {
        if (antCoords.getX() < 0 || antCoords.getY() < 0 || antCoords.getX() >= getWidth() || antCoords.getY() >= getHeight()) {
            throw new IllegalArgumentException("Ant has to be inside plane");
        }

        Ant ant = new Ant(antCoords, antRotation, antColor, getWidth(), getHeight(), idCounter++);
        currentAnts.add(ant);
        insertStructure(Collections.singletonList(new Cell(new BinaryAntState(BinaryState.DEAD), antCoords)));

        nextStateAnts.add(ant.cloneAnt());
        return ant;
    }

    public List<Ant> getAnts() {
        List<Ant> antsList = new ArrayList<>(currentAnts.size());
        antsList.addAll(currentAnts.stream().collect(Collectors.toList()));
        return antsList;
    }

    @Override
    public void endCalculatingNextState() {
        super.endCalculatingNextState();
        List<Ant> tmp = currentAnts;
        currentAnts = nextStateAnts;
        nextStateAnts = tmp;
    }

    @Override
    protected CellState nextCellState(Cell cell, NeighborhoodArray neighborsStates) {
        Ant currentAnt = null, nextStateAnt = null;
        for (Ant tmpAnt : currentAnts) {
            if (tmpAnt.getCoordinates().equals(cell.getCoords())) {
                currentAnt = tmpAnt;
                for (int i = 0; i < nextStateAnts.size(); i++) {
                    if (currentAnt.getId() == nextStateAnts.get(i).getId()) {
                        nextStateAnts.set(i, currentAnt.cloneAnt());
                        nextStateAnt = nextStateAnts.get(i);
                        break;
                    }
                }
                break;
            }
        }
        if (currentAnt == null || nextStateAnt == null)
            return cell.getState();

        BinaryAntState state = (BinaryAntState) cell.getState();

        if (state.getBinaryState() == BinaryState.ALIVE) {
            nextStateAnt.rotateLeft();
            state = new BinaryAntState(BinaryState.DEAD);
        } else if (state.getBinaryState() == BinaryState.DEAD) {
            nextStateAnt.rotateRight();
            state = new BinaryAntState(BinaryState.ALIVE, currentAnt.getAntColor());
        }

        nextStateAnt.move();
        return state;
    }

    @Override
    protected boolean cellIsAlive(CellState state) {
        return state == BinaryState.ALIVE;
    }

    @Override
    protected boolean cellChangedToAlive(CellState newState, CellState oldState) {
        return oldState == BinaryState.DEAD && newState == BinaryState.ALIVE;
    }

    @Override
    protected boolean cellChangedToDead(CellState newState, CellState oldState) {
        return newState == BinaryState.DEAD && oldState == BinaryState.ALIVE;
    }
}

