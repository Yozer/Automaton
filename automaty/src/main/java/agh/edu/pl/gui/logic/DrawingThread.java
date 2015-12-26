package agh.edu.pl.gui.logic;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Dominik on 2015-12-18.
 */
class DrawingThread implements Runnable
{
    private final AutomatonManager manager;
    private final Object DRAWING_MONITOR;
    private final AtomicBoolean shouldStartDraw;

    public DrawingThread(AutomatonManager manager, Object DRAWING_MONITOR, AtomicBoolean shouldStartDraw)
    {
        this.manager = manager;
        this.DRAWING_MONITOR = DRAWING_MONITOR;
        this.shouldStartDraw = shouldStartDraw;
    }

    @Override
    public void run()
    {
        agh.edu.pl.gui.helpers.Timer timer = new agh.edu.pl.gui.helpers.Timer();

        while (true)
        {
            waitForSignalToDraw();

            timer.start();
            manager.drawCurrentAutomaton();
            timer.stop();
            manager.statistics.renderTime.set(timer.getElapsed());

            SwingUtilities.invokeLater(manager.automatonPanel::repaint);

            shouldStartDraw.set(false);
            synchronized (DRAWING_MONITOR)
            {
                DRAWING_MONITOR.notify();
            }
        }
    }

    private void waitForSignalToDraw()
    {
        synchronized (this)
        {
            while (!shouldStartDraw.get())
            {
                try
                {
                    wait();
                } catch (InterruptedException e)
                {
                }
            }
        }
    }

    public void draw()
    {
        shouldStartDraw.set(true);
        synchronized (this)
        {
            notify();
        }
    }
}
