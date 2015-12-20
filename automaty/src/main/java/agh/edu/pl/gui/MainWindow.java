package agh.edu.pl.gui;


import agh.edu.pl.gui.enums.*;
import agh.edu.pl.gui.logic.AutomatonManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;

public class MainWindow extends MainWindowDesign
{
    // TODO make something with enabling/disabling btns

    private final Timer timerStatistics;
    private final int statisticUpdateEvery = 500; // ms

    private AutomatonManager automaton;

    public MainWindow()
    {
        super();
        automaton = new AutomatonManager(automatonPanel);
        timerStatistics = new Timer(statisticUpdateEvery, updateStatistics());
        timerStatistics.setRepeats(true);
        timerStatistics.setCoalesce(true);
        timerStatistics.start();
    }

    private void initAutomaton()
    {
        //setStateBusy();
        automaton.init(this::setStatePaused, false);
    }
    private void randCells()
    {
        setStateBusy();
        automaton.randCells(this::setStatePaused);
    }
    private void pauseAutomaton()
    {
        setStateBusy();
        automaton.pause(this::setStatePaused);
    }
    private void startAutomaton()
    {
        setStateBusy();
        automaton.start(this::setStateRunning);
    }
    private void insertStructure()
    {
        setStateBusy();
        automaton.insertPrimeCounter(this::setStatePaused);
    }
    private ActionListener updateStatistics()
    {
        return e ->
        {
            setSimulationTimeLabel(automaton.getLastSimulationTime());
            setGenerationCountLabel(automaton.getGenerationCount());
            setAliveCellsCountLabel(automaton.getAliveCellsCount());
            setDeadCellsLabel(automaton.getDeadCellsCount());
            setTotalCellsLabel(automaton.getTotalCellsCount());
            setRenderTimeLabel(automaton.getRenderTime());
            setOnePassTimeLabel(automaton.getOnePassTime());
        };
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand().toUpperCase().trim();

        if(cmd.equals(Commands.CHANGE_AUTOMATON.toString()))
        {
            JRadioButton btn = (JRadioButton) e.getSource();
            PossibleAutomaton selectedAutomaton = Arrays.stream(PossibleAutomaton.values()).filter(t -> Objects.equals(t.toString(), btn.getText())).findAny().get();
            automaton.setSelectedAutomaton(selectedAutomaton);
            initAutomaton();
        }
        else if(cmd.equals(Commands.START_AUTOMATON.toString()))
        {
            startAutomaton();
        }
        else if(cmd.equals(Commands.PAUSE_AUTOMATON.toString()))
        {
            pauseAutomaton();
        }
        else if(cmd.equals(Commands.RAND_CELLS.toString()))
        {
            randCells();
        }
        else if(cmd.equals(Commands.CLEAR_AUTOMATON.toString()))
        {
            initAutomaton();
        }
        else if(cmd.equals(Commands.INSERT_PRIME.toString()))
        {
            insertStructure();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        Object source = e.getSource();

        if (source instanceof JSlider)
        {
            JSlider slider = (JSlider)source;
            String name = slider.getName();
            if (name.equals(Commands.CHANGE_CELL_SIZE.toString()))
            {
                automaton.setCellSize(slider.getValue());
                initAutomaton();
            }
            else if(name.equals(Commands.CHANGE_SIMULATION_DELAY.toString()))
            {
                automaton.setSimulationDelay(slider.getValue());
            }
        }
    }
}

