package agh.edu.pl.gui;

import com.horstmann.corejava.GBC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;
import java.util.TimerTask;

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
            setAliveCellsCount(automataPanel.getAliveCellsCount());
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

