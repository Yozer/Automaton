package agh.edu.pl.gui;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.cells.states.WireElectronState;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Dominik on 2015-12-18.
 */
class ResetSwingWorker extends SwingWorker<Void, Void>
{
    private final AutomatonManager automatonManager;
    private final Runnable invokeAfter;
    private final boolean startImmediately;

    public ResetSwingWorker(AutomatonManager automatonManager, Runnable invokeAfter, boolean startImmediately)
    {
        this.automatonManager = automatonManager;
        this.invokeAfter = invokeAfter;
        this.startImmediately = startImmediately;
    }

    @Override
    protected Void doInBackground()
    {
        automatonManager.reset();
        return null;
    }

    @Override
    protected void done()
    {
        if(startImmediately)
        {
            automatonManager.start();
        }
        invokeAfter.run();
    }
}

class PauseSwingWorker extends SwingWorker<Void, Void>
{
    private final AutomatonManager automatonManager;
    private final Runnable invokeAfter;

    public PauseSwingWorker(AutomatonManager automatonManager, Runnable invokeAfter)
    {

        this.automatonManager = automatonManager;
        this.invokeAfter = invokeAfter;
    }

    @Override
    protected Void doInBackground() throws Exception
    {
        automatonManager.pause();
        return null;
    }

    @Override
    protected void done()
    {
        invokeAfter.run();
    }
}

class RandCellsWorker extends SwingWorker<Void, Void>
{
    private final AutomatonManager manager;
    private final Runnable invokeAfter;

    public RandCellsWorker(AutomatonManager manager, Runnable invokeAfter)
    {
        this.manager = manager;
        this.invokeAfter = invokeAfter;
    }

    @Override
    protected Void doInBackground() throws Exception
    {
        List<Cell> someRand = new ArrayList<>(manager.settings.getHeight() * manager.settings.getHeight());
        Random random = new Random();

        manager.reset();
        List<CellState> values = null;
        if (manager.settings.getSelectedAutomaton() == PossibleAutomaton.GameOfLive)
        {
            values = Arrays.stream(BinaryState.values()).collect(Collectors.toList());
            for (int i = 0; i < 3; i++)
                values.add(BinaryState.DEAD);

        } else if (manager.settings.getSelectedAutomaton() == PossibleAutomaton.QuadLife)
        {
            values = Arrays.stream(QuadState.values()).collect(Collectors.toList());
            for (int i = 0; i < 4; i++)
                values.add(QuadState.DEAD);

        } else if (manager.settings.getSelectedAutomaton() == PossibleAutomaton.WireWorld)
        {
            values = Arrays.stream(WireElectronState.values()).collect(Collectors.toList());
            for (int i = 0; i < 20; i++)
                values.add(WireElectronState.VOID);
        }

        for (int x = 0; x < manager.settings.getWidth(); x++)
        {
            for (int y = 0; y < manager.settings.getHeight(); y++)
            {
                someRand.add(new Cell(values.get(random.nextInt(values.size())), new Coords2D(x, y)));
            }
        }

        /*HashMap<CellCoordinates, CellState> blinker = new HashMap<>();
        blinker.put(new Coords2D(15, 20), BinaryState.ALIVE);
        blinker.put(new Coords2D(15, 21), BinaryState.ALIVE);
        blinker.put(new Coords2D(15, 22), BinaryState.ALIVE);
        automaton.insertStructure(blinker);*/

        /*HashMap<CellCoordinates, CellState> glider = new HashMap<>();
        glider.put(new Coords2D(5, 5), BinaryState.ALIVE);
        glider.put(new Coords2D(6, 5), BinaryState.ALIVE);
        glider.put(new Coords2D(7, 5), BinaryState.ALIVE);
        glider.put(new Coords2D(7, 4), BinaryState.ALIVE);
        glider.put(new Coords2D(6, 3), BinaryState.ALIVE);
        automaton.insertStructure(glider);*/
        //automaton.start();
        manager.automaton.insertStructure(someRand);
        manager.statistics.aliveCellsCount.set(manager.automaton.getAliveCount());
        manager.statistics.deadCellsCount.set(manager.statistics.totalCellsCount.get() - manager.statistics.aliveCellsCount.get());
        manager.drawCurrentAutomaton();
        manager.automatonPanel.paintImmediately(0, 0, manager.automatonPanel.getWidth(), manager.automatonPanel.getHeight());
        return null;
    }

    @Override
    protected void done()
    {
        invokeAfter.run();
    }
}

