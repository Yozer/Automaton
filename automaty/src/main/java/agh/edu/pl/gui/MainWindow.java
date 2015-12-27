package agh.edu.pl.gui;


import agh.edu.pl.gui.enums.*;
import agh.edu.pl.gui.logic.AutomatonManager;
import agh.edu.pl.gui.logic.exceptions.IllegalRulesFormatException;
import agh.edu.pl.gui.structures.StructureInfo;
import com.sun.javaws.exceptions.InvalidArgumentException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;

public class MainWindow extends MainWindowDesign
{
    // TODO refactor neighborhood
    // TODO make drawing 60fps

    private final Timer timerStatistics;
    private final int statisticUpdateEvery = 500; // ms

    private AutomatonManager automaton;

    public MainWindow()
    {
        super();
        automaton = new AutomatonManager(automatonPanel);
        automatonPanel.addMouseListener(this);

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
    private void insertStructure(StructureInfo structureInfo, int x, int y)
    {
        if(automaton.getSettings().getSelectedAutomaton() == PossibleAutomaton.Langton)
            automaton.insertAnt(structureInfo, x, y, getColorFromChooser());
        else
            automaton.insertStructure(structureInfo, x, y);
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
            if(selectedAutomaton == PossibleAutomaton.Langton)
                showColorChooser();
            else
                hideColorChooser();

            setStructureList(selectedAutomaton);
            initAutomaton();
        }
        else if(cmd.equals(Commands.START_AUTOMATON.toString()))
        {
            if(automaton.getSettings().getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy &&
                    automaton.getSettings().getNeighborHood() != CellNeighborhoodType.OneDim)
            {
                JOptionPane.showMessageDialog(null, "Dla jednowymiarowego automatu wybierz jednowymiarowe sąsiedztwo!", "Ostrzeżenie", JOptionPane.WARNING_MESSAGE);
                return;
            }

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
        else if(cmd.equals(Commands.INSERT_STRUCT.toString()))
        {
            setStateSelectingStruct();
        }
        else if(cmd.equals(Commands.CANCEL_INSERTING_STRUCT.toString()))
        {
            setStateCancelSelectingStruct();
        }
        else if(cmd.equals(Commands.CHANGE_NEIGHBORHOOD_TYPE.toString()))
        {
            String btnText = ((JRadioButton) e.getSource()).getText();
            automaton.setNeighborhoodType(btnText.equals("Moore") ? CellNeighborhoodType.Moore :
                    btnText.equals("Von Neumann") ? CellNeighborhoodType.VonNeumann :
                    btnText.equals("Jednowymiarowe") ? CellNeighborhoodType.OneDim : null);
        }
        else if(cmd.equals(Commands.SET_WRAP.toString()))
        {
            automaton.setWrap(isWrappingSelected());
        }
        else if(cmd.equals(Commands.CHANGE_TWO_DIM_RULES.toString()))
        {
            String rulesString = getRulesString();
            try
            {
                automaton.setRulesTwoDim(rulesString);
            }
            catch (IllegalRulesFormatException ex)
            {
                JOptionPane.showMessageDialog(null, "Niepoprawny format! Przykład: 23/3!", "Ostrzeżenie", JOptionPane.WARNING_MESSAGE);
            }
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
        else if(source instanceof JSpinner)
        {
            JSpinner jSpinner = (JSpinner)source;
            String name = jSpinner.getName();
            if(name.equals(Commands.CHANGE_NEIGHBORHOOD_RADIUS.toString()))
            {
                automaton.setNeighborhoodRadius((Integer) jSpinner.getValue());
            }
            else if(name.equals(Commands.CHANGE_ONE_DIM_RULES.toString()))
            {
                automaton.setRuleOneDim((Integer) jSpinner.getValue());
            }
        }
    }

    // fires when clicked on automaton panel
    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(getCurrentState() == AutomatonState.INSERTING_STRUCT)
        {
            StructureInfo structureInfo = getSelectedStructure();
            insertStructure(structureInfo, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {

    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }

}

