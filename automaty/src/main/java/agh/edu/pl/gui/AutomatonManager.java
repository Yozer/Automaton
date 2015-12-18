package agh.edu.pl.gui;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.automata.ElementaryAutomaton;
import agh.edu.pl.automaton.automata.GameOfLife;
import agh.edu.pl.automaton.automata.QuadLife;
import agh.edu.pl.automaton.automata.WireWorld;
import agh.edu.pl.automaton.automata.langton.LangtonAnt;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.OneDimensionalNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.VonNeumanNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// TODO make this class easier to read, extract some threads etc
// TODO remove drawing from GUI thread (in simulation Thread)

/**
 * Created by Dominik on 2015-12-13.
 */
class AutomatonManager
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
        this.simulationThreadObject = new Thread(simulationThread);
        simulationThreadObject.start();
    }

    public void start(Runnable invokeAfter)
    {
        simulationThread.resumeThread();
        invokeAfter.run();
    }

    public void reset(Runnable invokeAfter, boolean startImmediately)
    {
        SwingWorker swingWorker = new ResetSwingWorker(this, invokeAfter, startImmediately);
        swingWorker.execute();
    }

    public void pause(Runnable invokeAfter)
    {
        SwingWorker swingWorker = new PauseSwingWorker(this, invokeAfter);
        swingWorker.execute();
    }

    void reset()
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
        automatonPanel.paintImmediately(0, 0, automatonPanel.getWidth(), automatonPanel.getHeight());
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
            return new ElementaryAutomaton(settings.getWidth(), settings.getOneDimRule(), stateFactory, neighborhood);
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

    void start()
    {
        simulationThread.resumeThread();
    }


    void drawCurrentAutomaton()
    {
        BufferedImage bufferedImage = automatonPanel.getBitmapForDrawing();
        if(automaton instanceof Automaton2Dim)
        {
            for (Cell cell : automaton)
            {
                if (cell.hasChanged())
                {
                    Coords2D coords = (Coords2D) cell.getCoords();
                    bufferedImage.setRGB(coords.getX(), coords.getY(), cell.getState().toColor().getRGB());
                }
            }
        }

        automatonPanel.releaseBitmapAfterDrawing();
    }


    void pause()
    {
        simulationThread.pauseThread();
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
