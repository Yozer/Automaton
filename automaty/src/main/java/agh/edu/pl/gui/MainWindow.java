package agh.edu.pl.gui;


import agh.edu.pl.gui.enums.AutomatonState;
import agh.edu.pl.gui.enums.CellNeighborhoodType;
import agh.edu.pl.gui.enums.Commands;
import agh.edu.pl.gui.enums.PossibleAutomaton;
import agh.edu.pl.gui.logic.AutomatonManager;
import agh.edu.pl.gui.logic.AutomatonStatistics;
import agh.edu.pl.gui.logic.exceptions.IllegalRulesFormatException;
import agh.edu.pl.gui.structures.AntStructureInfo;
import agh.edu.pl.gui.structures.StructureInfo;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Objects;

public class MainWindow extends MainWindowDesign {
    private static final int statisticUpdateEvery = 500; // ms
    private final Timer timerStatistics;
    private final AutomatonManager automaton;
    private final AutomatonStatistics statistics;

    private StructureInfo structureInfo = null;

    public MainWindow() {
        super();
        automaton = new AutomatonManager(automatonPanel);
        statistics = automaton.getStatistics();

        timerStatistics = new Timer(statisticUpdateEvery, updateStatistics());
        timerStatistics.setRepeats(true);
        timerStatistics.setCoalesce(true);
        timerStatistics.start();

        automatonPanel.addMouseListener(this);
        automatonPanel.addMouseMotionListener(this);
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    private void clearAutomaton() {
        automaton.clearAutomaton(() -> {});
    }

    private void randCells() {
        setStateBusy();
        automaton.randCells(this::setStatePaused);
    }

    private void pauseAutomaton() {
        setStateBusy();
        automaton.pause(this::setStatePaused);
    }

    private void startAutomaton() {
        setStateBusy();
        automaton.start(this::setStateRunning);
    }

    private void insertStructure(StructureInfo structureInfo, int x, int y, double structRotation) {
        if (automaton.getSettings().getSelectedAutomaton() == PossibleAutomaton.Langton)
            automaton.insertAnt(((AntStructureInfo) structureInfo), x, y, getColorFromChooser());
        else
            automaton.insertStructure(structureInfo, x, y, structRotation);
    }

    private ActionListener updateStatistics() {
        return e ->
        {
            setSimulationTimeLabel(statistics.getLastSimulationTime());
            setGenerationCountLabel(statistics.getGenerationsCount());
            setAliveCellsCountLabel(statistics.getAliveCellsCount());
            setDeadCellsLabel(statistics.getDeadCellsCount());
            setTotalCellsLabel(statistics.getTotalCellsCount());
            setRenderTimeLabel(statistics.getRenderTime());
            setOnePassTimeLabel(statistics.getOnePassTime());
        };
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand().toUpperCase().trim();

        if (cmd.equals(Commands.CHANGE_AUTOMATON.toString())) {
            JRadioButton btn = (JRadioButton) e.getSource();
            PossibleAutomaton selectedAutomaton = Arrays.stream(PossibleAutomaton.values()).filter(t -> Objects.equals(t.toString(), btn.getText())).findAny().get();
            automaton.setSelectedAutomaton(selectedAutomaton);
            if (selectedAutomaton == PossibleAutomaton.Langton)
                showColorChooser();
            else
                hideColorChooser();
            if (selectedAutomaton == PossibleAutomaton.Jednowymiarowy) {
                selectOneDimNeighborhood();
                automaton.setNeighborhoodType(CellNeighborhoodType.OneDim);
            } else if (automaton.getSettings().getNeighborHood() == CellNeighborhoodType.OneDim) {
                selectMooreNeighborhood();
                automaton.setNeighborhoodType(CellNeighborhoodType.Moore);
            }

            setProperGuiSettings();
            setStructureList(selectedAutomaton);
        } else if (cmd.equals(Commands.START_AUTOMATON.toString())) {
            if (automaton.getSettings().getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy &&
                    automaton.getSettings().getNeighborHood() != CellNeighborhoodType.OneDim) {
                JOptionPane.showMessageDialog(null, "Dla jednowymiarowego automatu wybierz jednowymiarowe sąsiedztwo!", "Ostrzeżenie", JOptionPane.WARNING_MESSAGE);
                return;
            }

            startAutomaton();
        } else if (cmd.equals(Commands.PAUSE_AUTOMATON.toString())) {
            pauseAutomaton();
        } else if (cmd.equals(Commands.RAND_CELLS.toString())) {
            randCells();
        } else if (cmd.equals(Commands.CLEAR_AUTOMATON.toString())) {
            clearAutomaton();
        } else if (cmd.equals(Commands.INSERT_STRUCT.toString())) {
            setStateSelectingStruct();
            structureInfo = getSelectedStructure();
        } else if (cmd.equals(Commands.CANCEL_INSERTING_STRUCT.toString())) {
            setStateCancelSelectingStruct();
            structureInfo = null;
            automatonPanel.disableStructurePreview();
        } else if (cmd.equals(Commands.CHANGE_NEIGHBORHOOD_TYPE.toString())) {
            String btnText = ((JRadioButton) e.getSource()).getText();
            automaton.setNeighborhoodType(btnText.equals("Moore") ? CellNeighborhoodType.Moore :
                    btnText.equals("von Neumann") ? CellNeighborhoodType.VonNeumann :
                            btnText.equals("Jednowymiarowe") ? CellNeighborhoodType.OneDim : null);

            setProperGuiSettings();
        } else if (cmd.equals(Commands.SET_WRAP.toString())) {
            automaton.setWrap(isWrappingSelected());
        } else if (cmd.equals(Commands.CHANGE_TWO_DIM_RULES.toString())) {
            String rulesString = getRulesString();
            try {
                automaton.setRulesTwoDim(rulesString);
            } catch (IllegalRulesFormatException ex) {
                JOptionPane.showMessageDialog(null, "Niepoprawny format! Przykład: 23/3!", "Ostrzeżenie", JOptionPane.WARNING_MESSAGE);
            }
        } else if (cmd.equals(Commands.CHANGE_STRUCTURE.toString())) {
            if(structureInfo != null) {
                structureInfo = getSelectedStructure();
            }
            this.requestFocus();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();

        if (source instanceof JSlider) {
            JSlider slider = (JSlider) source;
            String name = slider.getName();
            if (name.equals(Commands.CHANGE_SIMULATION_DELAY.toString())) {
                automaton.setSimulationDelay(slider.getValue());
            }
        } else if (source instanceof JSpinner) {
            JSpinner jSpinner = (JSpinner) source;
            String name = jSpinner.getName();
            if (name.equals(Commands.CHANGE_NEIGHBORHOOD_RADIUS.toString())) {
                automaton.setNeighborhoodRadius((Integer) jSpinner.getValue());
            } else if (name.equals(Commands.CHANGE_ONE_DIM_RULES.toString())) {
                automaton.setRuleOneDim((Integer) jSpinner.getValue());
            } else if (name.equals(Commands.CHANGE_PLANE_HEIGHT.toString())) {
                automaton.setHeight((Integer)jSpinner.getValue());
            } else if (name.equals(Commands.CHANGE_PLANE_WIDTH.toString())) {
                automaton.setWidth((Integer)jSpinner.getValue());
            }
        }
    }

    // fires when clicked on automaton panel
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e) && getCurrentState() == AutomatonState.INSERTING_STRUCT) {
            Point2D point2D = automatonPanel.getStructInsertionPoint(e.getPoint());
            insertStructure(structureInfo, (int)(point2D.getX() + 0.5), (int)(point2D.getY() + 0.5), automatonPanel.getStructRotation());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (getCurrentState() == AutomatonState.INSERTING_STRUCT && structureInfo != null) {
            automatonPanel.setStructurePreview(structureInfo.getPreviewImage(), e.getPoint());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(getCurrentState() == AutomatonState.INSERTING_STRUCT && structureInfo != null) {
            if (e.getKeyCode() == KeyEvent.VK_KP_LEFT || e.getKeyCode() == KeyEvent.VK_LEFT) {
                automatonPanel.rotateStructPreviewLeft();
            } else if(e.getKeyCode() == KeyEvent.VK_KP_RIGHT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                automatonPanel.rotateStructPreviewRight();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

