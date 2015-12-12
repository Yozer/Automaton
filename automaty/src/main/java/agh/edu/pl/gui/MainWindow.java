package agh.edu.pl.gui;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MainWindow extends MainWindowDesign
{
    private final Timer timerStatistics;
    private final int statisticUpdateEvery = 500; // ms

    public MainWindow()
    {
        super();
        timerStatistics = new Timer(statisticUpdateEvery, updateStatistics());
        timerStatistics.setRepeats(true);
        timerStatistics.setCoalesce(true);
        timerStatistics.start();
    }
    private ActionListener updateStatistics()
    {
        return e ->
        {
            setSimulationTimeLabel(automataPanel.getLastSimulationTime());
            setGenerationCountLabel(automataPanel.getGenerationCount());
            setAliveCellsCountLabel(automataPanel.getAliveCellsCount());
            setRenderTimeLabel(automataPanel.getRenderTime());
        };
    }
}

enum PossibleAutomaton
{
    GameOfLive,
    QuadLife,
    WireWorld,
    Langton,
    OneDim
}

