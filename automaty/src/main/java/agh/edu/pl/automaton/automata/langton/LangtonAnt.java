package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.NeighborhoodList;
import agh.edu.pl.automaton.cells.states.BinaryAntState;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements Langton automaton. It's a two dimensional automaton with ants that change states of cell.
 * @author Dominik Baran
 * @see Automaton2Dim
 * @see Ant
 * @see AntDirection
 * @see BinaryAntState
 */
public class LangtonAnt extends Automaton2Dim {
    private List<Ant> currentAnts = new ArrayList<>();
    private List<Ant> nextStateAnts = new ArrayList<>();
    private int idCounter = 0;

    /**
     * @param width Automaton width
     * @param height Automaton height
     * @param cellNeighborhood Neighborhood for automaton
     * @param cellStateFactory State factory for initial state of each cell in automaton
     */
    public LangtonAnt(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood) {
        super(width, height, cellStateFactory, cellNeighborhood);
    }

    /**
     * Creates and adds new ant to automaton
     * @param antCoords Coords for ant
     * @param antColor Color that ant will be painting alive cells
     * @param antRotation Initial roration of ant
     * @return Created ant
     */
    public Ant addAnt(Coords2D antCoords, Color antColor, AntDirection antRotation) {
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

    /** {@inheritDoc}
     */
    @Override
    public void endCalculatingNextState() {
        super.endCalculatingNextState();
        List<Ant> tmp = currentAnts;
        currentAnts = nextStateAnts;
        nextStateAnts = tmp;
    }
    /** {@inheritDoc}
     */
    @Override
    protected CellState nextCellState(Cell cell, NeighborhoodList neighborsStates) {
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
    /** {@inheritDoc}
     */
    @Override
    protected boolean cellIsAlive(CellState state) {
        return state == BinaryState.ALIVE;
    }
    /** {@inheritDoc}
     */
    @Override
    protected boolean cellChangedStateFromDeadToAlive(CellState newState, CellState oldState) {
        return oldState == BinaryState.DEAD && newState == BinaryState.ALIVE;
    }
    /** {@inheritDoc}
     */
    @Override
    protected boolean cellChangedStateFromAliveToDead(CellState newState, CellState oldState) {
        return newState == BinaryState.DEAD && oldState == BinaryState.ALIVE;
    }
}

