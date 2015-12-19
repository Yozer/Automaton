package agh.edu.pl.gui.logic;

import agh.edu.pl.gui.helpers.Timer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Dominik on 2015-12-18.
 */
class SimulationThread implements Runnable
{
    private final Object PAUSE_MONITOR = new Object();
    private final Object IS_PAUSED_MONITOR = new Object();
    private final Object DRAWING_MONITOR = new Object();
    private final AutomatonManager manager;

    private AtomicBoolean isDrawning = new AtomicBoolean();
    private volatile boolean pauseThreadFlag = true;
    private volatile boolean isPausedFlag = true;

    private final int FPS_LIMIT = (int) (1000/2f);

    private final DrawingThread drawingThread;
    private final Thread drawingThreadObject;

    public SimulationThread(AutomatonManager manager)
    {
        this.manager = manager;
        this.drawingThread = new DrawingThread(manager, DRAWING_MONITOR, isDrawning);

        drawingThreadObject = new Thread(drawingThread, "DrawingThread");
        drawingThreadObject.start();
    }

    @Override
    public void run()
    {
        Timer timerTotal = new Timer();
        Timer timerSimulation = new Timer();
        int sumTotalTime = 0;
        int avgStep = 0;
        while (true)
        {
            avgStep++;
            timerTotal.start();

            if(sumTotalTime >= FPS_LIMIT)
            {
                drawingThread.draw();
            }

            checkForPausedAndWait();
            timerSimulation.start();
            manager.automaton.beginCalculatingNextState();
            timerSimulation.stop();
            manager.statistics.generationTime.set(timerSimulation.getElapsed());

            if(sumTotalTime >= FPS_LIMIT)
            {
                waitForDrawing();
                sumTotalTime = 0;
            }

            manager.automaton.endCalculatingNextState();
            manager.statistics.generationCount.incrementAndGet();
            manager.statistics.aliveCellsCount.set(manager.automaton.getAliveCount());
            manager.statistics.deadCellsCount.set(manager.statistics.totalCellsCount.get() - manager.statistics.aliveCellsCount.get());

            timerTotal.stop();
            sumTotalTime += timerTotal.getElapsed();
            manager.statistics.timeOfOnePass.set(timerTotal.getElapsed());

            int currentDelay = manager.getDelayFromSettings() - timerTotal.getElapsed();
            if(currentDelay > 10)
            {
                sumTotalTime += currentDelay;
                try
                {
                    Thread.sleep(currentDelay);
                } catch (InterruptedException e)
                {
                }
            }
        }
    }
    private void waitForDrawing()
    {
        synchronized (DRAWING_MONITOR)
        {
            while (isDrawning.get())
            {
                try
                {
                    DRAWING_MONITOR.wait();
                } catch (InterruptedException e)
                {

                }
            }
        }
    }

    private void checkForPausedAndWait()
    {
        synchronized (PAUSE_MONITOR)
        {
            boolean wasNotified = false;
            while(pauseThreadFlag)
            {
                if(!wasNotified)
                {
                    isPausedFlag = true;
                    synchronized (IS_PAUSED_MONITOR)
                    {
                        IS_PAUSED_MONITOR.notify();
                    }
                    wasNotified = true;
                }

                try
                {
                    PAUSE_MONITOR.wait();
                } catch (InterruptedException ignored)
                {
                }
            }
        }
    }

    public void pauseThread()
    {
        if(pauseThreadFlag)
            return;

        pauseThreadFlag = true;
        isPausedFlag = false;
        synchronized (IS_PAUSED_MONITOR)
        {
            while (!isPausedFlag)
            {
                try
                {
                    IS_PAUSED_MONITOR.wait();
                } catch (InterruptedException ignored)
                {

                }
            }
        }
    }

    public void resumeThread()
    {
        synchronized(PAUSE_MONITOR)
        {
            pauseThreadFlag = false;
            PAUSE_MONITOR.notify();
        }
    }
}

