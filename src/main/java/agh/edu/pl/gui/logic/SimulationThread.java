package agh.edu.pl.gui.logic;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread which is responsible for simulation current automaton.
 * @author Dominik Baran
 */
class SimulationThread implements Runnable {
    private final Object PAUSE_MONITOR = new Object();
    private final Object IS_PAUSED_MONITOR = new Object();
    private final Object DRAWING_MONITOR = new Object();
    private final AutomatonManager manager;
    private final AutomatonStatistics statistics;
    private final DrawingThread drawingThread;
    @SuppressWarnings("FieldCanBeLocal")
    private final Thread drawingThreadObject;
    private final AtomicBoolean isDrawing = new AtomicBoolean();
    private AtomicBoolean pauseThreadFlag = new AtomicBoolean(true);
    private AtomicBoolean isPausedFlag = new AtomicBoolean(true);

    public SimulationThread(AutomatonManager manager) {
        this.manager = manager;
        this.statistics = manager.getStatistics();
        this.drawingThread = new DrawingThread(manager, DRAWING_MONITOR, isDrawing);

        drawingThreadObject = new Thread(drawingThread, "DrawingThread");
        drawingThreadObject.start();
    }

    @Override
    public void run() {
        Timer timerTotal = new Timer();
        Timer timerSimulation = new Timer();

        //noinspection InfiniteLoopStatement
        while (true) {
            timerTotal.start();

            drawingThread.draw();

            checkForPausedAndWait();
            timerSimulation.start();
            manager.getAutomaton().beginCalculatingNextState();
            timerSimulation.stop();
            statistics.setGenerationTime(timerSimulation.getElapsed());

            waitForDrawing();

            manager.getAutomaton().endCalculatingNextState();
            statistics.incrementGenerationsCount();
            statistics.setAliveCellsCount(manager.getAutomaton().getAliveCount());
            statistics.setDeadCellsCount(statistics.getTotalCellsCount() - statistics.getAliveCellsCount());

            timerTotal.stop();
            statistics.setTimeOfOnePass(timerTotal.getElapsed());

            int currentDelay = manager.getSimulationDelay() - 2 * timerTotal.getElapsed();
            while (currentDelay > 1) {
                try {
                    Thread.sleep(10);
                    currentDelay -= 10;
                } catch (InterruptedException ignored) {
                }
                checkForPausedAndWait();
            }
        }
    }

    private void waitForDrawing() {
        synchronized (DRAWING_MONITOR) {
            while (isDrawing.get()) {
                try {
                    DRAWING_MONITOR.wait();
                } catch (InterruptedException ignored) {

                }
            }
        }
    }

    private void checkForPausedAndWait() {
        synchronized (PAUSE_MONITOR) {
            boolean wasNotified = false;
            while (pauseThreadFlag.get()) {
                if (!wasNotified) {
                    isPausedFlag.set(true);
                    synchronized (IS_PAUSED_MONITOR) {
                        IS_PAUSED_MONITOR.notify();
                    }
                    wasNotified = true;
                }

                try {
                    PAUSE_MONITOR.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public void pauseThread() {
        if (pauseThreadFlag.get())
            return;

        isPausedFlag.set(false);
        pauseThreadFlag.set(true);
        synchronized (IS_PAUSED_MONITOR) {
            while (!isPausedFlag.get()) {
                try {
                    IS_PAUSED_MONITOR.wait();
                } catch (InterruptedException ignored) {

                }
            }
        }
    }

    public void resumeThread() {
        pauseThreadFlag.set(false);
        synchronized (PAUSE_MONITOR) {
            PAUSE_MONITOR.notify();
        }
    }

    public boolean isRunning() {
        return !pauseThreadFlag.get();
    }
}

