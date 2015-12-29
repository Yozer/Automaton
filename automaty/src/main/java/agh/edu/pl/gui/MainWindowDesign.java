package agh.edu.pl.gui;

import agh.edu.pl.gui.enums.AutomatonState;
import agh.edu.pl.gui.enums.CellNeighborhoodType;
import agh.edu.pl.gui.enums.Commands;
import agh.edu.pl.gui.enums.PossibleAutomaton;
import agh.edu.pl.gui.logic.AutomatonPanel;
import agh.edu.pl.gui.logic.AutomatonSettings;
import agh.edu.pl.gui.structures.StructureInfo;
import agh.edu.pl.gui.structures.StructureLoader;
import com.horstmann.corejava.GBC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Dominik on 2015-12-10.
 */
@SuppressWarnings("SpellCheckingInspection")
abstract class MainWindowDesign extends JFrame implements ActionListener, ChangeListener, MouseListener, MouseMotionListener {
    // helps get default settings
    private final AutomatonSettings automatonSettings = new AutomatonSettings();
    private final ArrayList<Component> disabledWhenRunning = new ArrayList<>();
    private final ArrayList<Component> disabledWhenNotRunning = new ArrayList<>();
    AutomatonPanel automatonPanel;
    private Label generationCountLabel, simulationTimeLabel, aliveCellsCountLabel, deadCellsLabel, totalCellsLabel, onePassTimeLabel;
    private Label renderTimeLabel;
    private JPanel settingsPanel;
    private JCheckBox wrappingCheckBox;
    private JComboBox<StructureInfo> structuresList;
    private JButton insertStructButton, applyRulesButton;
    private JSpinner radiusSpinnerOneDimRule, radiusSpinner;
    private TextField textFieldRules;
    private JRadioButton radioButtonMoore, radioButtonVonNeumann, radioButtonOneDim;
    private JRadioButton oneDimAutomatonRadioButton, langtonAutomatonRadioButton, gameOfLifeAutomatonRadioButton;
    private JButton colorPicker;
    private Color choosedColor = Color.RED;
    private AutomatonState automatonState;
    private AutomatonState rememberState;

    MainWindowDesign() {
        initUI();
        setStatePaused();
    }

    private void initUI() {
        setTitle("Automat komórkowy");
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        // automata window
        automatonPanel = new AutomatonPanel();
        automatonPanel.addMouseMotionListener(this);
        mainPanel.add(automatonPanel, new GBC(0, 0).setFill(GridBagConstraints.BOTH).setWeight(0.99, 1));

        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(12, 1));
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(settingsPanel, new GBC(1, 0).setFill(GridBagConstraints.BOTH).setWeight(0.01, 1));

        // ------------------------------------------------------------------------ \\
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(new BoldLabel("Typ automatu"));
        ButtonGroup group = new ButtonGroup();
        JPanel panelRadio = new JPanel();
        for (PossibleAutomaton automaton : PossibleAutomaton.values()) {
            JRadioButton radio = new JRadioButton(automaton.toString());
            if (automaton == PossibleAutomaton.Jednowymiarowy)
                oneDimAutomatonRadioButton = radio;
            else if (automaton == PossibleAutomaton.Langton)
                langtonAutomatonRadioButton = radio;
            else if (automaton == PossibleAutomaton.GameOfLife)
                gameOfLifeAutomatonRadioButton = radio;

            disabledWhenRunning.add(radio);
            radio.setActionCommand(Commands.CHANGE_AUTOMATON.toString());
            group.add(radio);
            radio.addActionListener(this);

            if (automaton == automatonSettings.getSelectedAutomaton()) {
                radio.setSelected(true);
            }

            panelRadio.add(radio);
        }
        panel.add(panelRadio);
        settingsPanel.add(panel);

        // ------------------------------------------------------------------------ \\
        panel = new JPanel(new GridLayout(2, 1));
        panel.add(new BoldLabel("Rozmiar komórki"));

        JSlider slider = new JSlider((int) 1, (int) 5, (int) (automatonSettings.getCellCount()/1e5));
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setName(Commands.CHANGE_CELL_COUNT.toString());
        slider.addChangeListener(this);
        panel.add(slider);
        disabledWhenRunning.add(slider);
        settingsPanel.add(panel);
        // ------------------------------------------------------------------------ \\
        panel = new JPanel(new GridLayout(3, 1));
        panel.add(new BoldLabel("Wybierz zasady"));

        JPanel tmpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tmpPanel.add(new Label("Zasada dla jednowymiarowego [0-255]:"));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(automatonSettings.getOneDimRule(), 0, 255, 1);
        radiusSpinnerOneDimRule = new JSpinner(spinnerModel);
        radiusSpinnerOneDimRule.setEnabled(automatonSettings.getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy);
        radiusSpinnerOneDimRule.addChangeListener(this);
        radiusSpinnerOneDimRule.setName(Commands.CHANGE_ONE_DIM_RULES.toString());
        tmpPanel.add(radiusSpinnerOneDimRule);
        panel.add(tmpPanel);

        tmpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tmpPanel.add(new Label("Zasada dla GameOfLife:"));
        textFieldRules = new TextField(automatonSettings.getFormattedRules());
        textFieldRules.setEnabled(automatonSettings.getSelectedAutomaton() == PossibleAutomaton.GameOfLife ||
                automatonSettings.getSelectedAutomaton() == PossibleAutomaton.QuadLife);
        tmpPanel.add(textFieldRules);
        applyRulesButton = new JButton("Zastosuj zasadę");
        applyRulesButton.setEnabled(automatonSettings.getSelectedAutomaton() == PossibleAutomaton.GameOfLife ||
                automatonSettings.getSelectedAutomaton() == PossibleAutomaton.QuadLife);
        applyRulesButton.addActionListener(this);
        applyRulesButton.setActionCommand(Commands.CHANGE_TWO_DIM_RULES.toString());
        tmpPanel.add(applyRulesButton);
        panel.add(tmpPanel);

        settingsPanel.add(panel);
        // ------------------------------------------------------------------------ \\

        panel = new JPanel(new GridLayout(2, 1));
        panel.add(new BoldLabel("Typ sąsiedztwa"));
        group = new ButtonGroup();

        tmpPanel = new JPanel(new GridLayout(1, 3));

        radioButtonMoore = new JRadioButton("Moore");
        radioButtonMoore.setActionCommand(Commands.CHANGE_NEIGHBORHOOD_TYPE.toString());
        radioButtonMoore.addActionListener(this);
        radioButtonMoore.setSelected(automatonSettings.getNeighborHood() == CellNeighborhoodType.Moore);
        radioButtonMoore.setEnabled(automatonSettings.getNeighborHood() != CellNeighborhoodType.OneDim);
        group.add(radioButtonMoore);
        tmpPanel.add(radioButtonMoore);

        radioButtonVonNeumann = new JRadioButton("von Neumann");
        radioButtonVonNeumann.setActionCommand(Commands.CHANGE_NEIGHBORHOOD_TYPE.toString());
        radioButtonVonNeumann.addActionListener(this);
        radioButtonVonNeumann.setSelected(automatonSettings.getNeighborHood() == CellNeighborhoodType.VonNeumann);
        radioButtonVonNeumann.setEnabled(automatonSettings.getNeighborHood() != CellNeighborhoodType.OneDim);
        group.add(radioButtonVonNeumann);
        tmpPanel.add(radioButtonVonNeumann);

        radioButtonOneDim = new JRadioButton("Jednowymiarowe");
        radioButtonOneDim.setActionCommand(Commands.CHANGE_NEIGHBORHOOD_TYPE.toString());
        radioButtonOneDim.addActionListener(this);
        radioButtonOneDim.setSelected(automatonSettings.getNeighborHood() == CellNeighborhoodType.OneDim);
        radioButtonOneDim.setEnabled(automatonSettings.getNeighborHood() == CellNeighborhoodType.OneDim);
        group.add(radioButtonOneDim);
        tmpPanel.add(radioButtonOneDim);

        panel.add(tmpPanel);
        settingsPanel.add(panel);
        // ------------------------------------------------------------------------ \\
        panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new BoldLabel("Promień sąsiedztwa (r)"));
        spinnerModel = new SpinnerNumberModel(automatonSettings.getNeighborhoodRadius(), 0, 100, 1);

        radiusSpinner = new JSpinner(spinnerModel);
        radiusSpinner.addChangeListener(this);
        radiusSpinner.setName(Commands.CHANGE_NEIGHBORHOOD_RADIUS.toString());
        radiusSpinner.setMaximumSize(new Dimension(25, 25));
        radiusSpinner.setEnabled(automatonSettings.getSelectedAutomaton() != PossibleAutomaton.Jednowymiarowy);
        panel.add(radiusSpinner);

        wrappingCheckBox = new JCheckBox("Zawijać planszę?");
        wrappingCheckBox.addActionListener(this);
        wrappingCheckBox.setSelected(automatonSettings.getWrap());
        wrappingCheckBox.setActionCommand(Commands.SET_WRAP.toString());
        panel.add(wrappingCheckBox);

        settingsPanel.add(panel);
        // ------------------------------------------------------------------------ \\
        panel = new JPanel(new GridLayout(2, 1));
        Label tmpLabel = new BoldLabel("Opóźnienie między kolejnymi symulacjami: " + automatonSettings.getSimulationDelay() + " [ms]");
        panel.add(tmpLabel);

        slider = new JSlider(0, 1000, 0);
        slider.setMinorTickSpacing(50);
        slider.setMajorTickSpacing(250);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setName(Commands.CHANGE_SIMULATION_DELAY.toString());
        slider.setValue(automatonSettings.getSimulationDelay());
        slider.addChangeListener(this);
        final JSlider finalSlider = slider;
        slider.addChangeListener(e1 -> tmpLabel.setText("Opóźnienie między kolejnymi symulacjami: " + finalSlider.getValue() + " [ms]"));
        panel.add(slider);
        settingsPanel.add(panel);
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
        structuresList.setActionCommand(Commands.CHANGE_STRUCTURE.toString());
        structuresList.addActionListener(this);
        navigationButtonsPanel.add(structuresList);

        insertStructButton = new JButton("Wstaw strukturę");
        insertStructButton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        insertStructButton.setActionCommand(Commands.INSERT_STRUCT.toString());
        insertStructButton.addActionListener(this);
        navigationButtonsPanel.add(insertStructButton);

        colorPicker = new JButton("Wybierz kolor");
        colorPicker.setEnabled(false);
        colorPicker.setVisible(automatonSettings.getSelectedAutomaton() == PossibleAutomaton.Langton);
        colorPicker.addActionListener(e ->
        {
            Color c = JColorChooser.showDialog(null, "Wybierz kolor", Color.RED);
            if (c != null)
                choosedColor = c;
        });
        navigationButtonsPanel.add(colorPicker);


        // ------------------------------------------------------------------------ \\
        JPanel statisticsPanel = new JPanel(new GridLayout(4, 2));
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

    void selectOneDimNeighborhood() {
        radioButtonOneDim.setSelected(true);
    }

    void selectMooreNeighborhood() {
        radioButtonMoore.setSelected(true);
    }

    void setProperGuiSettings() {
        enableSettingsPanel();
    }

    String getRulesString() {
        return textFieldRules.getText();
    }

    void setStructureList(PossibleAutomaton selectedAutomaton) {
        structuresList.removeAllItems();
        for (StructureInfo structureInfo : StructureLoader.getAvailableStructures(selectedAutomaton))
            structuresList.addItem(structureInfo);
    }

    StructureInfo getSelectedStructure() {
        return (StructureInfo) structuresList.getSelectedItem();
    }

    void setGenerationCountLabel(int count) {
        generationCountLabel.setText("Liczba generacji: " + count);
    }

    void setSimulationTimeLabel(int time) {
        simulationTimeLabel.setText("Czas symulacji jednej generacji: " + time);
    }

    void setAliveCellsCountLabel(int count) {
        aliveCellsCountLabel.setText("Liczba żywych komórek: " + count);
    }

    void setTotalCellsLabel(int count) {
        totalCellsLabel.setText("Wszystkich komórek: " + count);
    }

    void setDeadCellsLabel(int count) {
        deadCellsLabel.setText("Martwych komórek: " + count);
    }

    void setRenderTimeLabel(int time) {
        renderTimeLabel.setText("Czas renderowania: " + time);
    }

    void setOnePassTimeLabel(int time) {
        onePassTimeLabel.setText("Czas jednego przejścia: " + time);
    }

    boolean isWrappingSelected() {
        return wrappingCheckBox.isSelected();
    }

    AutomatonState getCurrentState() {
        return automatonState;
    }

    void setStatePaused() {
        automatonState = AutomatonState.PAUSED;
        enableSettingsPanel();
        disableListOfComponents(disabledWhenNotRunning);
    }

    void setStateRunning() {
        automatonState = AutomatonState.RUNNING;
        enableSettingsPanel();
        disableListOfComponents(disabledWhenRunning);
    }

    void setStateBusy() {
        automatonState = AutomatonState.BUSY;
        disableSettingsPanel();
    }

    void setStateSelectingStruct() {
        rememberState = automatonState;
        automatonState = AutomatonState.INSERTING_STRUCT;
        disableSettingsPanel();
        insertStructButton.setEnabled(true);
        structuresList.setEnabled(true);
        colorPicker.setEnabled(true);
        insertStructButton.setText("Anuluj wstawianie");
        insertStructButton.setActionCommand(Commands.CANCEL_INSERTING_STRUCT.toString());
    }

    void setStateCancelSelectingStruct() {
        insertStructButton.setText("Wstaw strukturę");
        colorPicker.setEnabled(false);
        insertStructButton.setActionCommand(Commands.INSERT_STRUCT.toString());
        if (rememberState == AutomatonState.PAUSED)
            setStatePaused();
        else if (rememberState == AutomatonState.RUNNING)
            setStateRunning();
    }

    void showColorChooser() {
        colorPicker.setVisible(true);
    }

    void hideColorChooser() {
        colorPicker.setVisible(false);
    }

    Color getColorFromChooser() {
        return choosedColor;
    }


    private void disableListOfComponents(ArrayList<Component> componentList) {
        switchState(componentList, false);
    }

    private void switchState(ArrayList<Component> componentList, boolean state) {
        for (Component component : componentList)
            component.setEnabled(state);
    }

    private void disableSettingsPanel() {
        switchStatePanel(settingsPanel, false);
    }

    private void enableSettingsPanel() {
        switchStatePanel(settingsPanel, true);

        // just restore
        if (oneDimAutomatonRadioButton.isSelected()) {
            radioButtonVonNeumann.setEnabled(false);
            radioButtonMoore.setEnabled(false);
        } else if (langtonAutomatonRadioButton.isSelected()) {
            radioButtonVonNeumann.setEnabled(false);
            radioButtonMoore.setEnabled(false);
            radioButtonOneDim.setEnabled(false);
            radiusSpinner.setEnabled(false);
            radiusSpinnerOneDimRule.setEnabled(false);
        } else {
            radiusSpinnerOneDimRule.setEnabled(false);
            radioButtonOneDim.setEnabled(false);
            radiusSpinnerOneDimRule.setEnabled(false);
        }

        if (!gameOfLifeAutomatonRadioButton.isSelected()) {
            textFieldRules.setEnabled(false);
            applyRulesButton.setEnabled(false);
        }

        if (radioButtonOneDim.isSelected())
            radiusSpinner.setEnabled(false);

    }

    private void switchStatePanel(JPanel panel, boolean state) {
        panel.setEnabled(state);

        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (Objects.equals(component.getClass().getName(), "javax.swing.JPanel")) {
                switchStatePanel((JPanel) component, state);
            }
            component.setEnabled(state);
        }
    }
}

class BoldLabel extends Label {
    public BoldLabel(String text) {
        super(text);
        final BoldLabel boldLabel = this;
        super.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Font font = boldLabel.getFont();
                font = new Font(font.getName(), Font.BOLD, font.getSize());
                boldLabel.setFont(font);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }
}
