package agh.edu.pl.gui;

import agh.edu.pl.gui.enums.*;
import agh.edu.pl.gui.structures.StructureInfo;
import agh.edu.pl.gui.structures.StructureLoader;
import com.horstmann.corejava.GBC;
import javafx.scene.control.ColorPicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Created by Dominik on 2015-12-10.
 */
abstract class MainWindowDesign extends JFrame implements ActionListener, ChangeListener, MouseListener
{
    protected AutomatonPanel automatonPanel;

    private Label generationCountLabel, simulationTimeLabel, aliveCellsCountLabel, deadCellsLabel, totalCellsLabel, onePassTimeLabel;
    private Label renderTimeLabel;
    private JPanel settingsPanel;

    private JComboBox<StructureInfo> structuresList;
    private JButton insertStructButton;

    private JButton colorPicker;
    private Color choosedColor = Color.RED;

    private ArrayList<Component> disabledWhenRunning = new ArrayList<>();
    private ArrayList<Component> disabledWhenNotRunning= new ArrayList<>();

    private AutomatonState automatonState;
    private AutomatonState rememberState;

    // helps get default settings
    private final AutomatonSettings automatonSettings = new AutomatonSettings();

    protected MainWindowDesign()
    {
        initUI();
        setStatePaused();
    }

    private void initUI()
    {
        setTitle("Automat komórkowy");
        //setSize(1700, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        // automata window
        automatonPanel = new AutomatonPanel();
        mainPanel.add(automatonPanel, new GBC(0, 0).setFill(GridBagConstraints.BOTH).setWeight(0.99, 1));

        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(16, 1));
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(settingsPanel, new GBC(1, 0).setFill(GridBagConstraints.BOTH).setWeight(0.01, 1));

        // ------------------------------------------------------------------------ \\
        settingsPanel.add(new Label("Typ automatu"));
        ButtonGroup group = new ButtonGroup();
        JPanel panelRadio = new JPanel();
        for (PossibleAutomaton automaton : PossibleAutomaton.values())
        {
            JRadioButton radio = new JRadioButton(automaton.toString());
            disabledWhenRunning.add(radio);
            radio.setActionCommand(Commands.CHANGE_AUTOMATON.toString());
            group.add(radio);
            radio.addActionListener(this);

            if(automaton == automatonSettings.getSelectedAutomaton())
            {
                radio.setSelected(true);
            }

            panelRadio.add(radio);
        }
        settingsPanel.add(panelRadio);

        // ------------------------------------------------------------------------ \\
        settingsPanel.add(new Label("Rozmiar komórki"));

        JSlider slider = new JSlider(1, 20, 5);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setName(Commands.CHANGE_CELL_SIZE.toString());
        slider.setValue((int) automatonSettings.getCellSize());
        slider.addChangeListener(this);
        settingsPanel.add(slider);
        disabledWhenRunning.add(slider);
        // ------------------------------------------------------------------------ \\

        settingsPanel.add(new Label("Opóźnienie między kolejnymi symulacjami [ms]"));

        slider = new JSlider(0, 1000, 0);
        slider.setMinorTickSpacing(50);
        slider.setMajorTickSpacing(250);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setName(Commands.CHANGE_SIMULATION_DELAY.toString());
        slider.setValue(automatonSettings.getSimulationDelay());
        slider.addChangeListener(this);
        settingsPanel.add(slider);
        // ------------------------------------------------------------------------ \\
        JPanel navigationButtonsPanel = new JPanel(new GridLayout(4, 2));
        JButton startButton = new JButton("Start");
        startButton.setActionCommand(Commands.START_AUTOMATON.toString());
        startButton.addActionListener(this);
        startButton.setEnabled(false);
        navigationButtonsPanel.add(startButton);
        disabledWhenRunning.add(startButton);

        JButton pauseButton = new JButton("Pauza");
        pauseButton.setActionCommand(Commands.PAUSE_AUTOMATON.toString());
        pauseButton.addActionListener(this);
        pauseButton.setEnabled(false);
        navigationButtonsPanel.add(pauseButton);
        disabledWhenNotRunning.add(pauseButton);

        JButton randButton = new JButton("Losuj");
        randButton.setActionCommand(Commands.RAND_CELLS.toString());
        randButton.addActionListener(this);
        navigationButtonsPanel.add(randButton);
        disabledWhenRunning.add(randButton);

        JButton clearButton = new JButton("Wyczyść");
        clearButton.setActionCommand(Commands.CLEAR_AUTOMATON.toString());
        clearButton.addActionListener(this);
        navigationButtonsPanel.add(clearButton);
        disabledWhenRunning.add(clearButton);

        structuresList = new JComboBox<>();
        setStructureList(automatonSettings.getSelectedAutomaton());
        navigationButtonsPanel.add(structuresList);

        insertStructButton = new JButton("Wstaw strukturę");
        insertStructButton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        insertStructButton.setActionCommand(Commands.INSERT_STRUCT.toString());
        insertStructButton.addActionListener(this);
        navigationButtonsPanel.add(insertStructButton);

        colorPicker = new JButton("Wybierz kolor");
        colorPicker.setVisible(automatonSettings.getSelectedAutomaton() == PossibleAutomaton.Langton);
        colorPicker.addActionListener(e ->
        {
            Color c = JColorChooser.showDialog(null, "Wybierz kolor", Color.RED);
            if(c != null)
                choosedColor = c;
        });
        navigationButtonsPanel.add(colorPicker);


        // ------------------------------------------------------------------------ \\
        JPanel statisticsPanel = new JPanel(new GridLayout(4,2));
        statisticsPanel.setName("statisticPanel");
        generationCountLabel = new Label("Liczba generacji: 0");
        simulationTimeLabel = new Label("Czas symulacji jednej: 0");
        aliveCellsCountLabel = new Label("Liczba żywych komórek: 0");
        renderTimeLabel = new Label("Czas renderowania: 0");
        totalCellsLabel = new Label("Wszystkich komórek: 0");
        deadCellsLabel = new Label("Martwych komórek: 0");
        onePassTimeLabel = new Label("Czas jednego przejścia: 0");
        statisticsPanel.add(generationCountLabel);
        statisticsPanel.add(simulationTimeLabel);
        statisticsPanel.add(aliveCellsCountLabel);
        statisticsPanel.add(renderTimeLabel);
        statisticsPanel.add(deadCellsLabel);
        statisticsPanel.add(onePassTimeLabel);
        statisticsPanel.add(totalCellsLabel);
        settingsPanel.add(statisticsPanel);
        settingsPanel.add(navigationButtonsPanel);
        // ------------------------------------------------------------------------ \\

        add(mainPanel);
    }

    protected void setStructureList(PossibleAutomaton selectedAutomaton)
    {
        structuresList.removeAllItems();
        for(StructureInfo structureInfo : StructureLoader.getAvailableStructures(selectedAutomaton))
            structuresList.addItem(structureInfo);
    }
    protected StructureInfo getSelectedStructure()
    {
        return (StructureInfo) structuresList.getSelectedItem();
    }

    protected void setGenerationCountLabel(int count)
    {
        generationCountLabel.setText("Liczba generacji: " + count);
    }
    protected void setSimulationTimeLabel(int time) { simulationTimeLabel.setText("Czas symulacji jednej generacji: " + time); }
    protected void setAliveCellsCountLabel(int count) { aliveCellsCountLabel.setText("Liczba żywych komórek: " + count); }
    protected void setTotalCellsLabel(int count)
    {
        totalCellsLabel.setText("Wszystkich komórek: " + count);
    }
    protected void setDeadCellsLabel(int count)
    {
        deadCellsLabel.setText("Martwych komórek: " + count);
    }
    protected void setRenderTimeLabel(int time)
    {
        renderTimeLabel.setText("Czas renderowania: " + time);
    }
    protected void setOnePassTimeLabel(int time)
    {
        onePassTimeLabel.setText("Czas jednego przejścia: " + time);
    }

    protected AutomatonState getCurrentState()
    {
        return automatonState;
    }
    protected AutomatonState getRememberedState()
    {
        if(getCurrentState() != AutomatonState.INSERTING_STRUCT)
            throw new IllegalArgumentException("Cannot get remembered state if current is different that inserting struct");
        return rememberState;
    }
    protected void setStatePaused()
    {
        automatonState = AutomatonState.PAUSED;
        enableSettingsPanel();
        disableListOfComponents(disabledWhenNotRunning);
    }
    protected void setStateRunning()
    {
        automatonState = AutomatonState.RUNNING;
        enableSettingsPanel();
        disableListOfComponents(disabledWhenRunning);
    }
    protected void setStateBusy()
    {
        automatonState = AutomatonState.BUSY;
        disableSettingsPanel();
    }
    protected void setStateSelectingStruct()
    {
        rememberState = automatonState;
        automatonState = AutomatonState.INSERTING_STRUCT;
        disableSettingsPanel();
        insertStructButton.setEnabled(true);
        structuresList.setEnabled(true);
        insertStructButton.setText("Anuluj wstawianie");
        insertStructButton.setActionCommand(Commands.CANCEL_INSERTING_STRUCT.toString());
    }
    protected void setStateCancelSelectingStruct()
    {
        insertStructButton.setText("Wstaw strukturę");
        insertStructButton.setActionCommand(Commands.INSERT_STRUCT.toString());
        if(rememberState == AutomatonState.PAUSED)
            setStatePaused();
        else if(rememberState == AutomatonState.RUNNING)
            setStateRunning();
    }
    protected void showColorChooser()
    {
        colorPicker.setVisible(true);
    }
    protected void hideColorChooser()
    {
        colorPicker.setVisible(false);
    }
    protected Color getColorFromChooser()
    {
        return choosedColor;
    }

    private void enableListOfComponents(ArrayList<Component> componentList) { switchState(componentList, true);}
    private void disableListOfComponents(ArrayList<Component> componentList) { switchState(componentList, false);}
    private void switchState(ArrayList<Component> componentList, boolean state)
    {
        for(Component component : componentList)
            component.setEnabled(state);
    }

    private void disableSettingsPanel() { switchStatePanel(settingsPanel, false);}
    private void enableSettingsPanel() { switchStatePanel(settingsPanel, true);}
    private void switchStatePanel(JPanel panel, boolean state)
    {
        panel.setEnabled(state);

        Component[] components = panel.getComponents();

        for(int i = 0; i < components.length; i++) {
            if(components[i].getClass().getName() == "javax.swing.JPanel") {
                switchStatePanel((JPanel) components[i], state);
            }
            components[i].setEnabled(state);
        }
    }
}
