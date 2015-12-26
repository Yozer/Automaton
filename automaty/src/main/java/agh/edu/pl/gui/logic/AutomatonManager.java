package agh.edu.pl.gui.logic;

import agh.edu.pl.automaton.*;
import agh.edu.pl.automaton.automata.*;
import agh.edu.pl.automaton.automata.langton.*;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.*;
import agh.edu.pl.automaton.cells.neighborhoods.*;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.*;
import agh.edu.pl.gui.*;
import agh.edu.pl.gui.enums.*;
import agh.edu.pl.gui.structures.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dominik on 2015-12-13.
 */
// TODO add resetting automaton after changing new options
// TODO add controlling distribution of each cell type during rand
// TODO disable random for langton and enable color picker when inserting structs (need to be refactored anyway)
// TODO add error checking for rules parsing
// TODO add better disabling/enabling controls
public class AutomatonManager
{
    Automaton automaton;

    final SimulationThread simulationThread;
    private final Thread simulationThreadObject;
    private final AtomicInteger simulationDelay = new AtomicInteger(0);

    final AutomatonSettings settings = new AutomatonSettings();
    final AutomatonStatistics statistics = new AutomatonStatistics();
    final AutomatonPanel automatonPanel;

    int automaton1DimRow;

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
        SwingWorker swingWorker = new InsertStructureSwingWorker(this, structureInfo, x, y);
        swingWorker.execute();
    }

    public void insertAnt(StructureInfo structureInfo, int x, int y, Color antColor)
    {
        Coords2D atPoint = new Coords2D((int)(x / settings.getCellSize()), (int)(y / settings.getCellSize()));
        if(atPoint.getX() + structureInfo.getWidth() > settings.getWidth() || atPoint.getY() + structureInfo.getHeight() > settings.getHeight())
            return;

        LangtonAntStructureLoader.AntInfo ant = new LangtonAntStructureLoader().loadAnt(structureInfo, atPoint);

        boolean isRunning = simulationThread.isRunning();
        if(isRunning)
            pause();

        ((LangtonAnt) automaton).addAnt(ant.getAntCoords(), antColor, ant.getAntState());
        drawCurrentAutomaton();
        automatonPanel.repaint();

        if(isRunning)
            start();
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
        if(automaton instanceof Automaton1Dim)
            automaton1DimRow = 0;

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

                if(settings.getSelectedAutomaton() == PossibleAutomaton.Langton)
                {
                    LangtonAnt langtonAnt = ((LangtonAnt) automaton);
                    for (Ant ant : langtonAnt.getAnts())
                    {
                        pixels[ant.getCoordinates().getY() * settings.getWidth() + ant.getCoordinates().getX()] = Color.YELLOW.getRGB();
                    }
                }
            }
            else if(automaton instanceof Automaton1Dim)
            {
                if(automaton1DimRow == settings.getHeight())
                {
                    for(int row = 1; row < settings.getHeight(); row++)
                    {
                        System.arraycopy(pixels, row * settings.getWidth(), pixels, (row - 1) * settings.getWidth(), settings.getWidth());
                    }
                    --automaton1DimRow;
                }

                for(Cell cell : automaton)
                {
                    Coords1D coords = (Coords1D) cell.getCoords();
                    pixels[automaton1DimRow * settings.getWidth() + coords.getX()] = cell.getState().toColor().getRGB();
                }
                ++automaton1DimRow;
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
        else if(settings.getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy)
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
        else if(neighborhoodType == CellNeighborhoodType.VonNeumann)
            return new VonNeumanNeighborhood(settings.getNeighborhoodRadius(), settings.getWrap(), settings.getWidth(), settings.getHeight());
        else if(neighborhoodType == CellNeighborhoodType.OneDim)
            return new OneDimensionalNeighborhood(settings.getWrap(), settings.getWidth());
        return null;
    }

    public void randCells(Runnable invokeAfter)
    {
        // doesn't make sense for this automaton
        if(settings.getSelectedAutomaton() == PossibleAutomaton.Langton)
            return;

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

    public AutomatonSettings getSettings()
    {
        return settings;
    }

    public void setNeighborhoodType(CellNeighborhoodType neighborhoodType)
    {
        settings.setNeighborhood(neighborhoodType);
    }

    public void setNeighborhoodRadius(int neighborhoodRadius)
    {
        settings.setNeighborhoodRadius(neighborhoodRadius);
    }

    public void setWrap(boolean wrap)
    {
        settings.setWrap(wrap);
    }

    public void setRulesTwoDim(String rulesTwoDim)
    {
        settings.setFormattedRules(rulesTwoDim);
    }

    public void setRuleOneDim(Integer ruleOneDim)
    {
        settings.setOneDimRule(ruleOneDim);
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
