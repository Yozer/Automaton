package agh.edu.pl.gui.logic;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread which is responsible for drawing automaton.
 * @author Dominik Baran
 */
class DrawingThread implements Runnable {
    private final AutomatonManager manager;
    private final AutomatonStatistics statistics;
    private final Object DRAWING_MONITOR;
    private final AtomicBoolean shouldStartDraw;

    public DrawingThread(AutomatonManager manager, Object DRAWING_MONITOR, AtomicBoolean shouldStartDraw) {
        this.manager = manager;
        this.statistics = manager.getStatistics();
        this.DRAWING_MONITOR = DRAWING_MONITOR;
        this.shouldStartDraw = shouldStartDraw;
    }

    @Override
    public void run() {
        Timer timer = new Timer();

        //noinspection InfiniteLoopStatement
        while (true) {
            waitForSignalToDraw();

            timer.start();
            manager.drawCurrentAutomaton();
            timer.stop();
            statistics.setRenderTime(timer.getElapsed());

            manager.repaint();

            shouldStartDraw.set(false);
            synchronized (DRAWING_MONITOR) {
                DRAWING_MONITOR.notify();
            }
        }
    }

    private void waitForSignalToDraw() {
        synchronized (this) {
            while (!shouldStartDraw.get()) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public void draw() {
        synchronized (this) {
            shouldStartDraw.set(true);
            notify();
        }
    }
}
