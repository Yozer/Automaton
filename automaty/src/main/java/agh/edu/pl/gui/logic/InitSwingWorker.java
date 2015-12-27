package agh.edu.pl.gui.logic;

import agh.edu.pl.automaton.Automaton1Dim;
import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.gui.enums.PossibleAutomaton;
import agh.edu.pl.gui.structures.GameOfLiveStructureLoader;
import agh.edu.pl.gui.structures.OneDimStructureLoader;
import agh.edu.pl.gui.structures.StructureInfo;
import agh.edu.pl.gui.structures.WireWorldStructureLoader;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Dominik on 2015-12-18.
 */
class InitSwingWorker extends SwingWorker<Void, Void>
{
    private final AutomatonManager automatonManager;
    private final Runnable invokeAfter;

    public InitSwingWorker(AutomatonManager automatonManager, Runnable invokeAfter)
    {
        this.automatonManager = automatonManager;
        this.invokeAfter = invokeAfter;
    }

    @Override
    protected Void doInBackground()
    {
        automatonManager.init();
        return null;
    }

    @Override
    protected void done()
    {
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

class InsertStructureSwingWorker extends SwingWorker<Void, Void>
{
    private final AutomatonManager manager;
    private final StructureInfo structureInfo;
    private final int x;
    private final int y;

    public InsertStructureSwingWorker(AutomatonManager manager, StructureInfo structureInfo, int x, int y)
    {
        this.manager = manager;
        this.structureInfo = structureInfo;
        this.x = x;
        this.y = y;
    }


    @Override
    protected Void doInBackground() throws Exception
    {
        if(manager.automaton == null)
            manager.init();

        Coords2D atPoint = new Coords2D((int)(x / manager.settings.getCellSize()), (int)(y / manager.settings.getCellSize()));
        if(atPoint.getX() + structureInfo.getWidth() > manager.settings.getWidth() || atPoint.getY() + structureInfo.getHeight() > manager.settings.getHeight())
            return null;

        List<Cell> structure = null;
        if(manager.settings.getSelectedAutomaton() == PossibleAutomaton.WireWorld)
            structure = new WireWorldStructureLoader().getStructure(structureInfo, atPoint);
        else if(manager.settings.getSelectedAutomaton() == PossibleAutomaton.GameOfLive)
            structure = new GameOfLiveStructureLoader().getStructure(structureInfo, atPoint);
        else if(manager.settings.getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy)
            structure = new OneDimStructureLoader().getStructure(structureInfo, atPoint);

        boolean isRunning = manager.simulationThread.isRunning();
        if(isRunning)
            manager.pause();

        manager.automaton.insertStructure(structure);
        manager.automaton1DimRow = 0;
        manager.drawCurrentAutomaton();
        manager. automatonPanel.repaint();

        if(isRunning)
            manager.start();
        return null;
    }

    @Override
    protected void done()
    {

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
    protected Void doInBackground()
    {
        List<Cell> someRand = null;
        if(manager.automaton instanceof Automaton2Dim)
            someRand = new ArrayList<>(manager.settings.getHeight() * manager.settings.getWidth());
        else if(manager.automaton instanceof Automaton1Dim)
            someRand = new ArrayList<>(manager.settings.getWidth());

        Random random = new Random();

        manager.init();
        List<CellState> values = null;
        if (manager.settings.getSelectedAutomaton() == PossibleAutomaton.GameOfLive ||
                manager.settings.getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy)
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

        if(manager.automaton instanceof Automaton2Dim)
        {
            for (int x = 0; x < manager.settings.getWidth(); x++)
            {
                for (int y = 0; y < manager.settings.getHeight(); y++)
                {
                    someRand.add(new Cell(values.get(random.nextInt(values.size())), new Coords2D(x, y)));
                }
            }
        }
        else if(manager.automaton instanceof Automaton1Dim)
        {
            for (int x = 0; x < manager.settings.getWidth(); x++)
            {
                someRand.add(new Cell(values.get(random.nextInt(values.size())), new Coords1D(x)));
            }
        }

        manager.automaton.insertStructure(someRand);
        manager.statistics.aliveCellsCount.set(manager.automaton.getAliveCount());
        manager.statistics.deadCellsCount.set(manager.statistics.totalCellsCount.get() - manager.statistics.aliveCellsCount.get());
        manager.automaton1DimRow = 0;
        manager.drawCurrentAutomaton();
        manager.automatonPanel.repaint();
        return null;
    }

    @Override
    protected void done()
    {
        invokeAfter.run();
    }
}

