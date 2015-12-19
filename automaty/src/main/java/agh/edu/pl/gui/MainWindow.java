package agh.edu.pl.gui;


import agh.edu.pl.gui.enums.*;
import agh.edu.pl.gui.logic.AutomatonManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;

public class MainWindow extends MainWindowDesign
{
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
    private void disableSettingsPanel()
    {
        setPanelState(settingsPanel, false);
    }
    private void enableSettingsPanel()
    {
        setPanelState(settingsPanel, true);
    }
    private void setPanelState(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);

        Component[] components = panel.getComponents();

        for(int i = 0; i < components.length; i++) {
            if(Objects.equals(components[i].getClass().getName(), "javax.swing.JPanel") &&
                    !Objects.equals(components[i].getName(), "statisticPanel")) {
                setPanelState((JPanel) components[i], isEnabled);
            }

            components[i].setEnabled(isEnabled);
        }
    }
    private void resetAutomaton()
    {
        disableSettingsPanel();
        automaton.reset(() -> {}, false);
    }
    private void randCells()
    {
        disableSettingsPanel();
        automaton.randCells(() -> {
            enableSettingsPanel();
            pauseButton.setEnabled(false);
        });
    }
    private void pauseAutomaton()
    {
        disableSettingsPanel();
        automaton.pause(() -> {
            enableSettingsPanel();
            pauseButton.setEnabled(false);
        });
    }
    private void startAutomaton()
    {
        disableSettingsPanel();
        automaton.start(() -> {
            disableSettingsPanel();
            pauseButton.setEnabled(true);
            sliderDelay.setEnabled(true);
        });
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
            startButton.setEnabled(false);
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
                // TODO it has restart automaton? I don't think so
                automaton.setCellSize(slider.getValue());
                //resetAutomaton();
            }
            else if(name.equals(Commands.CHANGE_SIMULATION_DELAY.toString()))
            {
                automaton.setSimulationDelay(slider.getValue());
            }
        }
    }
}

