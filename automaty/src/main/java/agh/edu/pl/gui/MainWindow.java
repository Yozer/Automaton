package agh.edu.pl.gui;

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

    private AutomatonThread automaton;

    public MainWindow()
    {
        super();
        automaton = new AutomatonThread(automatonPanel);
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
            if(Objects.equals(components[i].getClass().getName(), "javax.swing.JPanel")) {
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
        });
    }
    private ActionListener updateStatistics()
    {
        return e ->
        {
            setSimulationTimeLabel(automaton.getLastSimulationTime());
            setGenerationCountLabel(automaton.getGenerationCount());
            setAliveCellsCountLabel(automaton.getAliveCellsCount());
            setRenderTimeLabel(automaton.getRenderTime());
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
            //resetAutomaton();
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
                automaton.setCellSize(slider.getValue() + 1);
                //resetAutomaton();
            }
            else if(name.equals(Commands.CHANGE_SIMULATION_DELAY))
            {
                automaton.setSimulationDelay(slider.getValue());
            }
        }
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

enum Commands
{
    CHANGE_AUTOMATON("CHANGE_AUTOMATON"),
    CHANGE_CELL_SIZE("CHANGE_CELL_SIZE"),
    START_AUTOMATON("START_AUTOMATON"),
    PAUSE_AUTOMATON("PAUSE_AUTOMATON"),
    RAND_CELLS("RAND_CELLS"),
    CHANGE_SIMULATION_DELAY("CHANGE_SIMULATION_DELAY");

    private final String text;

    Commands(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}

