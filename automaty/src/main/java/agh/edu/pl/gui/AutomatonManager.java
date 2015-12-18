package agh.edu.pl.gui;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.automata.ElementaryAutomaton;
import agh.edu.pl.automaton.automata.GameOfLife;
import agh.edu.pl.automaton.automata.QuadLife;
import agh.edu.pl.automaton.automata.WireWorld;
import agh.edu.pl.automaton.automata.langton.LangtonAnt;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.OneDimensionalNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.VonNeumanNeighborhood;
import agh.edu.pl.automaton.cells.states.*;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// TODO initAutomaton run it in nextState and make private
// TODO make this class easier to read, extract some threads etc
// TODO remove drawing from GUI thread (in simulation Thread)
// TODO check why it's hanging. Probably that odd thread.sleep when diff < delay
// TODO check why it behaves strange for wireworld

/**
 * Created by Dominik on 2015-12-13.
 */
class AutomatonManager
{
    private Automaton automaton;
    private Thread simulationThread;

    private final AutomatonSettings settings = new AutomatonSettings();
    private final AutomatonStatistics statistics = new AutomatonStatistics();
    private final AtomicInteger delay;
    private final AutomatonPanel automatonPanel;

    private final AtomicBoolean shouldPause = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(true);

    public AutomatonManager(AutomatonPanel automatonPanel)
    {
        this.delay = new AtomicInteger(settings.getSimulationDelay());
        this.automatonPanel = automatonPanel;
    }

    public void reset(Runnable invokeAfter, boolean startImmediately)
    {
        AutomatonManager thr = this;
        SwingWorker swingWorker = new SwingWorker<Void, Void>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                reset();
                return null;
            }

            @Override
            protected void done()
            {
                if(startImmediately)
                {
                    thr.start();
                }
                invokeAfter.run();
            }
        };
        swingWorker.run();
    }

    private void reset()
    {
        //czekamy na stop poprzedniego
        if(simulationThread != null) // at first run there is no thread object so there is nothing to pause
        {
            pause();
            simulationThread.interrupt();
        }

        resetStatistics();

        // resetujemy automaton do stanu (wszystko martwe)
        UniformStateFactory stateFactory;
        settings.setWidth((int) (automatonPanel.getWidth() /  settings.getCellSize()));
        settings.setHeight((int) (automatonPanel.getHeight() /  settings.getCellSize()));
        automatonPanel.setScale(settings.getCellSize());
        statistics.totalCellsCount.set(settings.getWidth()*settings.getHeight());

        CellNeighborhoodType neighborhoodType = settings.getNeighborHood();
        CellNeighborhood neighborhood = null;
        if(neighborhoodType == CellNeighborhoodType.Moore)
            neighborhood = new MoorNeighborhood(settings.getNeighborhoodRadius(), settings.getWrap(), settings.getWidth(), settings.getHeight());
        else if(neighborhoodType == CellNeighborhoodType.VonNeuman)
            neighborhood = new VonNeumanNeighborhood(settings.getNeighborhoodRadius(), settings.getWrap(), settings.getWidth(), settings.getHeight());
        else if(neighborhoodType == CellNeighborhoodType.OneDim)
            neighborhood = new OneDimensionalNeighborhood(settings.getWrap(), settings.getWidth());

        if(settings.getSelectedAutomaton() == PossibleAutomaton.GameOfLive)
        {
            stateFactory = new UniformStateFactory(BinaryState.DEAD);
            automaton = new GameOfLife(settings.getSurviveFactors(), settings.getComeAliveFactors(), settings.getWidth(), settings.getHeight(),
                    stateFactory, neighborhood);
        }
        else if(settings.getSelectedAutomaton() == PossibleAutomaton.QuadLife)
        {
            stateFactory = new UniformStateFactory(QuadState.DEAD);
            automaton = new QuadLife(settings.getWidth(), settings.getHeight(), stateFactory, neighborhood);
        }
        else if(settings.getSelectedAutomaton() == PossibleAutomaton.WireWorld)
        {
            stateFactory = new UniformStateFactory(WireElectronState.VOID);
            automaton = new WireWorld(settings.getWidth(), settings.getHeight(), stateFactory, neighborhood);
        }
        else if(settings.getSelectedAutomaton() == PossibleAutomaton.OneDim)
        {
            stateFactory = new UniformStateFactory(BinaryState.DEAD);
            automaton = new ElementaryAutomaton(settings.getWidth(), settings.getOneDimRule(), stateFactory, neighborhood);
        }
        else if(settings.getSelectedAutomaton() == PossibleAutomaton.Langton)
        {
            stateFactory = new UniformStateFactory(new BinaryAntState(BinaryState.DEAD));
            automaton = new LangtonAnt(settings.getWidth(), settings.getHeight(), stateFactory, neighborhood);
        }

        automatonPanel.createBufferedImage(settings.getWidth(), settings.getHeight());
        drawCurrentAutomaton();
        automatonPanel.paintImmediately(0, 0, automatonPanel.getWidth(), automatonPanel.getHeight());

        simulationThread = new Thread(() ->
        {
            long simulateTimeBefore, simulateTimeAfter;

            while(!Thread.currentThread().isInterrupted())
            {
                CountDownLatch wasDrawn = new CountDownLatch(1);
                long timeBefore = System.nanoTime();


                SwingUtilities.invokeLater(() ->
                {
                    long drawTimeBefore = System.nanoTime();
                    drawCurrentAutomaton();
                    wasDrawn.countDown();
                    automatonPanel.paintImmediately(0,0, automatonPanel.getWidth(), automatonPanel.getHeight());
                    long drawTimeAfter = System.nanoTime();
                    statistics.renderTime.set((int) ((drawTimeAfter - drawTimeBefore)/1000000f));
                });

                simulateTimeBefore = System.nanoTime();
                automaton.beginCalculatingNextState();

                simulateTimeAfter = System.nanoTime();
                statistics.generationTime.set((int) ((simulateTimeAfter - simulateTimeBefore)/1000000f));

                try
                {
                    wasDrawn.await();
                } catch (InterruptedException e)
                {

                }

                automaton.endCalculatingNextState();
                statistics.generationCount.incrementAndGet();
                statistics.aliveCellsCount.set(automaton.getAliveCount());
                statistics.deadCellsCount.set(statistics.totalCellsCount.get() - statistics.aliveCellsCount.get());

                long timeAfter = System.nanoTime();
                int difference = (int) ((timeAfter - timeBefore)/1000000f);
                statistics.timeOfOnePass.set(difference);
                int currentDelay = delay.get();
                if(difference < currentDelay && difference > 10)
                {
                    try
                    {
                        Thread.sleep(currentDelay - difference);
                    } catch (InterruptedException e)
                    {

                    }
                }

                if(shouldPause.get())
                {
                    paused.set(true);
                    shouldPause.set(false);
                    // czekamy do końca pauzy albo jak wątek nie zostanie przerwany
                    while(paused.get() && !Thread.currentThread().isInterrupted())
                    {
                        try
                        {
                            Thread.sleep(0);
                        } catch (InterruptedException e)
                        {

                        }
                    }
                }
            }
        });
    }

    private void resetStatistics()
    {
        statistics.generationCount.set(0);
        statistics.renderTime.set(0);
        statistics.generationTime.set(0);
        statistics.aliveCellsCount.set(0);
        statistics.deadCellsCount.set(0);
        statistics.totalCellsCount.set(0);
        statistics.timeOfOnePass.set(0);
    }

    private void drawCurrentAutomaton()
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


    public void start(Runnable invokeAfter)
    {
        if(!paused.get())
            return;

        // pierwsze odpalenie (nikt nic nie zmienił w ustawieniach)
        if(simulationThread == null)
        {
            reset(invokeAfter, true);
        }
        else
        {
            start();
            invokeAfter.run();
        }
    }
    private void start()
    {
        if(!simulationThread.isAlive())
            simulationThread.start();
        shouldPause.set(false);
        paused.set(false);
    }

    public void pause(Runnable invokeAfter)
    {
        SwingWorker swingWorker = new SwingWorker<Void, Void>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                pause();
                return null;
            }

            @Override
            protected void done()
            {
                invokeAfter.run();
            }
        };
        swingWorker.execute();
    }

    private void pause()
    {
        shouldPause.set(true);
        while(!paused.get())
            try
            {
                Thread.sleep(0);
            } catch (InterruptedException e)
            {

            }
    }

    public void randCells(Runnable invokeAfter)
    {
        if(!paused.get())
            throw new UnsupportedOperationException("Can't rand cells while automaton is running");

        SwingWorker worker = new SwingWorker<Void, Void>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                Map<CellCoordinates, CellState> someRand = new HashMap<>();
                Random random = new Random();

                reset();

                for(int x = 0; x < settings.getWidth() ; x++)
                {
                    for(int y = 0; y < settings.getHeight(); y++)
                    {
                        if(settings.getSelectedAutomaton() == PossibleAutomaton.GameOfLive)
                        {
                            List<BinaryState> values = Arrays.stream(BinaryState.values()).collect(Collectors.toList());
                            for(int i = 0; i < 3; i++)
                                values.add(BinaryState.DEAD);
                            someRand.put(new Coords2D(x, y),  values.get(random.nextInt(values.size())));
                        }
                        else if(settings.getSelectedAutomaton() == PossibleAutomaton.QuadLife)
                        {
                            List<QuadState> values = Arrays.stream(QuadState.values()).collect(Collectors.toList());
                            for(int i = 0; i < 4; i++)
                                values.add(QuadState.DEAD);
                            someRand.put(new Coords2D(x, y), values.get(random.nextInt(values.size())));
                        }
                        else if(settings.getSelectedAutomaton() == PossibleAutomaton.WireWorld)
                        {
                            List<WireElectronState> values = Arrays.stream(WireElectronState.values()).collect(Collectors.toList());
                            for(int i = 0; i < 20; i++)
                                values.add(WireElectronState.VOID);
                            someRand.put(new Coords2D(x, y), values.get(random.nextInt(values.size())));
                        }
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
                automaton.insertStructure(someRand);
                statistics.aliveCellsCount.set(automaton.getAliveCount());
                statistics.deadCellsCount.set(statistics.totalCellsCount.get() - statistics.aliveCellsCount.get());
                drawCurrentAutomaton();
                automatonPanel.paintImmediately(0,0, automatonPanel.getWidth(), automatonPanel.getHeight());
                return null;
            }

            @Override
            protected void done()
            {
                invokeAfter.run();
            }
        };
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
        this.delay.set(simulationDelay);
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
    public int getOonePassTime()
    {
        return statistics.timeOfOnePass.get();
    }


    private class AutomatonStatistics
    {
        private final AtomicInteger generationCount = new AtomicInteger();
        private final AtomicInteger aliveCellsCount = new AtomicInteger();
        private final AtomicInteger totalCellsCount = new AtomicInteger();
        private final AtomicInteger deadCellsCount  = new AtomicInteger();
        private final AtomicInteger renderTime = new AtomicInteger();
        private final AtomicInteger generationTime = new AtomicInteger();
        public final AtomicInteger timeOfOnePass = new AtomicInteger();
    }
}
