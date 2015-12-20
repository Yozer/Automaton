package agh.edu.pl.gui.logic;

import agh.edu.pl.automaton.*;
import agh.edu.pl.automaton.automata.*;
import agh.edu.pl.automaton.automata.langton.LangtonAnt;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.*;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.*;
import agh.edu.pl.gui.*;
import agh.edu.pl.gui.enums.*;
import agh.edu.pl.gui.structures.StructureInfo;
import agh.edu.pl.gui.structures.WireWorldStructureLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dominik on 2015-12-13.
 */
// TODO add adding structures

public class AutomatonManager
{
    Automaton automaton;

    private final SimulationThread simulationThread;
    private final Thread simulationThreadObject;
    private final AtomicInteger simulationDelay = new AtomicInteger(0);

    final AutomatonSettings settings = new AutomatonSettings();
    final AutomatonStatistics statistics = new AutomatonStatistics();
    final AutomatonPanel automatonPanel;

    public AutomatonManager(AutomatonPanel automatonPanel)
    {
        this.automatonPanel = automatonPanel;
        this.simulationThread = new SimulationThread(this);
        this.simulationThreadObject = new Thread(simulationThread, "SimulationThread");
        simulationThreadObject.start();
    }

    public void start(Runnable invokeAfter)
    {
        // automaton was not initiated (user didn't change any settings)
        if(automaton == null)
            init();

        simulationThread.resumeThread();
        invokeAfter.run();
    }

    public void init(Runnable invokeAfter, boolean startImmediately)
    {
        SwingWorker swingWorker = new InitSwingWorker(this, invokeAfter, startImmediately);
        swingWorker.execute();
    }

    public void pause(Runnable invokeAfter)
    {
        SwingWorker swingWorker = new PauseSwingWorker(this, invokeAfter);
        swingWorker.execute();
    }
    public void insertStructure(StructureInfo structureInfo, int x, int y)
    {
//        SwingWorker swingWorker = new InsertPrimeSwingWorker(this, invokeAfter);
//        swingWorker.execute();

        Coords2D atPoint = new Coords2D((int)(x / settings.getCellSize()), (int)(y / settings.getCellSize()));
        if(atPoint.getX() + structureInfo.getWidth() > settings.getWidth() || atPoint.getY() + structureInfo.getHeight() > settings.getHeight())
            return;

        List<Cell> structure = null;
        if(settings.getSelectedAutomaton() == PossibleAutomaton.WireWorld)
            structure = new WireWorldStructureLoader().getStructure(structureInfo, atPoint);

        automaton.insertStructure(structure);
        drawCurrentAutomaton();
        automatonPanel.repaint();
    }

    void init()
    {
        pause();
        statistics.resetStatistics();

        // setup scale and dimensions
        settings.setWidth((int) (automatonPanel.getWidth() /  settings.getCellSize()));
        settings.setHeight((int) (automatonPanel.getHeight() /  settings.getCellSize()));
        automatonPanel.setScale(settings.getCellSize());
        statistics.totalCellsCount.set(settings.getWidth()*settings.getHeight());

        // get automaton from settings
        automaton = getAutomatonFromSettings();

        automatonPanel.createBufferedImage(settings.getWidth(), settings.getHeight());
        drawCurrentAutomaton();
        automatonPanel.repaint();
    }
    void pause()
    {
        simulationThread.pauseThread();
    }
    void start()
    {
        simulationThread.resumeThread();
    }


    void drawCurrentAutomaton()
    {
        synchronized (automatonPanel.LOCKER)
        {
            int[] pixels = automatonPanel.getPixelsForDrawing();

            if (automaton instanceof Automaton2Dim)
            {
                Iterator<Cell> cellIterator = automaton.iteratorChangedOnly();
                Cell cell = null;
                while (cellIterator.hasNext())
                {
                    cell = cellIterator.next();
                    Coords2D coords = (Coords2D) cell.getCoords();
                    pixels[coords.getY() * settings.getWidth() + coords.getX()] = cell.getState().toColor().getRGB();
                }
            }
        }
    }

    private Automaton getAutomatonFromSettings()
    {
        CellNeighborhood neighborhood = getCellNeighborhoodFromSettings(settings.getNeighborHood());
        if(settings.getSelectedAutomaton() == PossibleAutomaton.GameOfLive)
        {
            UniformStateFactory stateFactory = new UniformStateFactory(BinaryState.DEAD);
            return new GameOfLife(settings.getSurviveFactors(), settings.getComeAliveFactors(), settings.getWidth(), settings.getHeight(),
                    stateFactory, neighborhood);
        }
        else if(settings.getSelectedAutomaton() == PossibleAutomaton.QuadLife)
        {
            UniformStateFactory stateFactory = new UniformStateFactory(QuadState.DEAD);
            return new QuadLife(settings.getWidth(), settings.getHeight(), stateFactory, neighborhood);
        }
        else if(settings.getSelectedAutomaton() == PossibleAutomaton.WireWorld)
        {
            UniformStateFactory stateFactory = new UniformStateFactory(WireElectronState.VOID);
            return new WireWorld(settings.getWidth(), settings.getHeight(), stateFactory, neighborhood);
        }
        else if(settings.getSelectedAutomaton() == PossibleAutomaton.OneDim)
        {
            UniformStateFactory stateFactory = new UniformStateFactory(BinaryState.DEAD);
            return new ElementaryAutomaton(settings.getWidth(), settings.getOneDimRule(), stateFactory);
        }
        else if(settings.getSelectedAutomaton() == PossibleAutomaton.Langton)
        {
            UniformStateFactory stateFactory = new UniformStateFactory(new BinaryAntState(BinaryState.DEAD));
            return new LangtonAnt(settings.getWidth(), settings.getHeight(), stateFactory, neighborhood);
        }
        return null;
    }

    private CellNeighborhood getCellNeighborhoodFromSettings(CellNeighborhoodType neighborhoodType)
    {
        if(neighborhoodType == CellNeighborhoodType.Moore)
            return new MoorNeighborhood(settings.getNeighborhoodRadius(), settings.getWrap(), settings.getWidth(), settings.getHeight());
        else if(neighborhoodType == CellNeighborhoodType.VonNeuman)
            return new VonNeumanNeighborhood(settings.getNeighborhoodRadius(), settings.getWrap(), settings.getWidth(), settings.getHeight());
        else if(neighborhoodType == CellNeighborhoodType.OneDim)
            return new OneDimensionalNeighborhood(settings.getWrap(), settings.getWidth());
        return null;
    }

    public void randCells(Runnable invokeAfter)
    {
        SwingWorker worker = new RandCellsWorker(this, invokeAfter);
        worker.execute();
    }

    public void setSelectedAutomaton(PossibleAutomaton selectedAutomaton)
    {
        settings.setSelectedAutomaton(selectedAutomaton);
    }

    public void setCellSize(int cellSize)
    {
        settings.setCellSize(cellSize);
    }

    public void setSimulationDelay(int simulationDelay)
    {
        this.simulationDelay.set(simulationDelay);
        this.settings.setSimulationDelay(simulationDelay);
    }

    public int getLastSimulationTime()
    {
        return statistics.generationTime.get();
    }

    public int getGenerationCount()
    {
        return statistics.generationCount.get();
    }

    public int getAliveCellsCount()
    {
        return statistics.aliveCellsCount.get();
    }

    public int getRenderTime()
    {
        return statistics.renderTime.get();
    }

    public int getDeadCellsCount()
    {
        return statistics.deadCellsCount.get();
    }

    public int getTotalCellsCount()
    {
        return statistics.totalCellsCount.get();
    }

    public int getOnePassTime()
    {
        return statistics.timeOfOnePass.get();
    }

    public int getDelayFromSettings()
    {
        return this.simulationDelay.get();
    }


    class AutomatonStatistics
    {
        final AtomicInteger generationCount = new AtomicInteger();
        final AtomicInteger aliveCellsCount = new AtomicInteger();
        final AtomicInteger totalCellsCount = new AtomicInteger();
        final AtomicInteger deadCellsCount  = new AtomicInteger();
        final AtomicInteger renderTime = new AtomicInteger();
        final AtomicInteger generationTime = new AtomicInteger();
        final AtomicInteger timeOfOnePass = new AtomicInteger();

        void resetStatistics()
        {
            statistics.generationCount.set(0);
            statistics.renderTime.set(0);
            statistics.generationTime.set(0);
            statistics.aliveCellsCount.set(0);
            statistics.deadCellsCount.set(0);
            statistics.totalCellsCount.set(0);
            statistics.timeOfOnePass.set(0);
        }
    }
}
