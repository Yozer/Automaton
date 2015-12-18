package agh.edu.pl.gui.helpers;

public class Timer
{
    private long before, after;
    private int elapsed;
    public void start()
    {
        before = System.nanoTime();
        after = 0;
        elapsed = 0;
    }
    public int stop()
    {
        after = System.nanoTime();
        elapsed = (int) ((after - before)/1000000f);
        return elapsed;
    }
    public int getElapsed()
    {
        return elapsed;
    }
}
