package agh.edu.pl.gui.logic;

import agh.edu.pl.automaton.Automaton1Dim;
import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.cells.states.WireElectronState;
import agh.edu.pl.gui.enums.PossibleAutomaton;
import agh.edu.pl.gui.structures.StructureInfo;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Dominik on 2015-12-18.
 */
class InitSwingWorker extends SwingWorker<Void, Void> {
    private final AutomatonManager automatonManager;
    private final Runnable invokeAfter;

    public InitSwingWorker(AutomatonManager automatonManager, Runnable invokeAfter) {
        this.automatonManager = automatonManager;
        this.invokeAfter = invokeAfter;
    }

    @Override
    protected Void doInBackground() {
        automatonManager.init();
        return null;
    }

    @Override
    protected void done() {
        invokeAfter.run();
    }
}

class PauseSwingWorker extends SwingWorker<Void, Void> {
    private final AutomatonManager automatonManager;
    private final Runnable invokeAfter;

    public PauseSwingWorker(AutomatonManager automatonManager, Runnable invokeAfter) {
        this.automatonManager = automatonManager;
        this.invokeAfter = invokeAfter;
    }

    @Override
    protected Void doInBackground() {
        automatonManager.pause();
        return null;
    }

    @Override
    protected void done() {
        invokeAfter.run();
    }
}

class InsertStructureSwingWorker extends SwingWorker<Void, Void> {
    private final AutomatonManager manager;
    private final StructureInfo structureInfo;
    private final int x;
    private final int y;
    private final double structRotation;

    public InsertStructureSwingWorker(AutomatonManager manager, StructureInfo structureInfo, int x, int y, double structRotation) {
        this.manager = manager;
        this.structureInfo = structureInfo;
        this.x = x;
        this.y = y;
        this.structRotation = structRotation;
    }

    @Override
    protected Void doInBackground() throws IOException {
        if (manager.getAutomaton() == null)
            manager.init();

        manager.resetAutomatonIfSettingsHasChanged();
        Coords2D atPoint = new Coords2D(x, y);
        if (atPoint.getX() + structureInfo.getWidth() > manager.getSettings().getWidth()
                || atPoint.getY() + structureInfo.getHeight() > manager.getSettings().getHeight())
            return null;

        List<Cell> cells = structureInfo.getCells(x, y, structRotation);

        boolean isRunning = manager.isRunning();
        if (isRunning)
            manager.pause();

        manager.getAutomaton().insertStructure(cells);
        manager.decrementAutomatonOneDimRow();
        manager.drawCurrentAutomaton();
        manager.repaint();

        if (isRunning)
            manager.start();
        return null;
    }

    @Override
    protected void done() {

    }
}

class RandCellsWorker extends SwingWorker<Void, Void> {
    private final AutomatonManager manager;
    private final Runnable invokeAfter;

    public RandCellsWorker(AutomatonManager manager, Runnable invokeAfter) {
        this.manager = manager;
        this.invokeAfter = invokeAfter;
    }

    @Override
    protected Void doInBackground() {
        List<Cell> someRand;
        if (manager.getAutomaton() instanceof Automaton2Dim)
            someRand = new ArrayList<>(manager.getSettings().getHeight() * manager.getSettings().getWidth());
        else if (manager.getAutomaton() instanceof Automaton1Dim)
            someRand = new ArrayList<>(manager.getSettings().getWidth());
        else
            throw new IllegalArgumentException("Invalid automaton!");

        Random random = new Random();

        manager.init();
        List<CellState> values;
        if (manager.getSettings().getSelectedAutomaton() == PossibleAutomaton.GameOfLife ||
                manager.getSettings().getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy) {
            values = Arrays.stream(BinaryState.values()).collect(Collectors.toList());
            for (int i = 0; i < 3; i++)
                values.add(BinaryState.DEAD);

        } else if (manager.getSettings().getSelectedAutomaton() == PossibleAutomaton.QuadLife) {
            values = Arrays.stream(QuadState.values()).collect(Collectors.toList());
            for (int i = 0; i < 4; i++)
                values.add(QuadState.DEAD);

        } else if (manager.getSettings().getSelectedAutomaton() == PossibleAutomaton.WireWorld) {
            values = Arrays.stream(WireElectronState.values()).collect(Collectors.toList());
            for (int i = 0; i < 20; i++)
                values.add(WireElectronState.VOID);
        } else {
            throw new IllegalArgumentException("Invalid automaton!");
        }

        if (manager.getAutomaton() instanceof Automaton2Dim) {
            for (int x = 0; x < manager.getSettings().getWidth(); x++) {
                for (int y = 0; y < manager.getSettings().getHeight(); y++) {
                    someRand.add(new Cell(values.get(random.nextInt(values.size())), new Coords2D(x, y)));
                }
            }
        } else if (manager.getAutomaton() instanceof Automaton1Dim) {
            for (int x = 0; x < manager.getSettings().getWidth(); x++) {
                someRand.add(new Cell(values.get(random.nextInt(values.size())), new Coords1D(x)));
            }
        }

        AutomatonStatistics statistics = manager.getStatistics();
        manager.getAutomaton().insertStructure(someRand);
        statistics.setAliveCellsCount(manager.getAutomaton().getAliveCount());
        statistics.setDeadCellsCount(statistics.getTotalCellsCount() - statistics.getAliveCellsCount());
        manager.resetAutomatonOneDimRow();
        manager.drawCurrentAutomaton();
        manager.repaint();
        return null;
    }

    @Override
    protected void done() {
        invokeAfter.run();
    }
}

