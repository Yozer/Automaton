package agh.edu.pl.gui;

import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.gui.enums.AutomatonState;
import agh.edu.pl.gui.enums.CellNeighborhoodType;
import agh.edu.pl.gui.enums.Commands;
import agh.edu.pl.gui.enums.PossibleAutomaton;
import agh.edu.pl.gui.logic.AutomatonPanel;
import agh.edu.pl.gui.logic.AutomatonSettings;
import agh.edu.pl.gui.structures.*;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Dominik on 2015-12-10.
 */
@SuppressWarnings("SpellCheckingInspection")
abstract class MainWindowDesign extends JFrame implements ActionListener, ChangeListener, MouseListener, MouseMotionListener, KeyListener {
    // helps get default settings
    private final AutomatonSettings automatonSettings = new AutomatonSettings();
    private final ArrayList<Component> disabledWhenRunning = new ArrayList<>();
    private final ArrayList<Component> disabledWhenNotRunning = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private final BufferedImage cursorImg;
    private final Cursor blankCursor;
    AutomatonPanel automatonPanel;
    private JLabel generationCountLabel;
    private JLabel simulationTimeLabel;
    private JLabel aliveCellsCountLabel;
    private JLabel deadCellsLabel;
    private JLabel totalCellsLabel;
    private JLabel onePassTimeLabel;
    private JLabel renderTimeLabel;
    private JPanel settingsPanel;
    private JCheckBox wrappingCheckBox;
    private JComboBox<StructureInfo> structuresList;
    private JButton insertStructButton, applyRulesButton;
    private JSpinner radiusSpinnerOneDimRule, radiusSpinner;
    private JTextField textFieldRules;
    private JRadioButton radioButtonMoore, radioButtonVonNeumann, radioButtonOneDim;
    private JRadioButton oneDimAutomatonRadioButton, langtonAutomatonRadioButton, gameOfLifeAutomatonRadioButton;
    private JButton colorPicker;
    private Color choosedColor = Color.RED;
    private AutomatonState automatonState;
    private AutomatonState rememberState;

    MainWindowDesign() {
        initUI();
        setStatePaused();

        cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
    }

    private void initUI() {
        setTitle("Automat komórkowy");
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel mainPanel = new JPanel(new MigLayout());

        // automata window
        automatonPanel = new AutomatonPanel();
        settingsPanel = new JPanel(new MigLayout());

        // ------------------------------------------------------------------------ \\
        JPanel panel = new JPanel(new MigLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(new BoldLabel("Typ automatu"), new CC().wrap().alignX("left"));
        ButtonGroup group = new ButtonGroup();
        JPanel panelRadio = new JPanel(new MigLayout());
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
        panel.add(panelRadio, "wrap");

        panel.add(new BoldLabel("Szerokość planszy:"), new CC().alignX("left"));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(automatonSettings.getWidth(), 1, 1000000, 1);
        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setName(Commands.CHANGE_PLANE_WIDTH.toString());
        spinner.addChangeListener(this);
        panel.add(spinner, new CC().alignX("left").cell(0, 2));
        disabledWhenRunning.add(spinner);

        panel.add(new BoldLabel("Wysokość planszy:"), new CC().alignX("left").cell(0, 3));
        spinnerModel = new SpinnerNumberModel(automatonSettings.getHeight(), 1, 1000000, 1);
        spinner = new JSpinner(spinnerModel);
        spinner.setName(Commands.CHANGE_PLANE_HEIGHT.toString());
        spinner.addChangeListener(this);
        panel.add(spinner, new CC().alignX("left").cell(0, 3));
        disabledWhenRunning.add(spinner);
        settingsPanel.add(panel, new CC().alignX("left").wrap());
        // ------------------------------------------------------------------------ \\
        panel = new JPanel(new MigLayout());
        panel.add(new BoldLabel("Wybierz zasady"), new CC().alignX("left").wrap());

        panel.add(new Label("Zasada dla jednowymiarowego [0-255]:"), new CC().alignX("left"));
        spinnerModel = new SpinnerNumberModel(automatonSettings.getOneDimRule(), 0, 255, 1);
        radiusSpinnerOneDimRule = new JSpinner(spinnerModel);
        radiusSpinnerOneDimRule.setEnabled(automatonSettings.getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy);
        radiusSpinnerOneDimRule.addChangeListener(this);
        radiusSpinnerOneDimRule.setName(Commands.CHANGE_ONE_DIM_RULES.toString());
        panel.add(radiusSpinnerOneDimRule, "wrap");

        panel.add(new Label("Zasada dla GameOfLife:"), new CC().alignX("left"));
        textFieldRules = new JTextField(automatonSettings.getFormattedRules(), 13);
        textFieldRules.setEnabled(automatonSettings.getSelectedAutomaton() == PossibleAutomaton.GameOfLife ||
                automatonSettings.getSelectedAutomaton() == PossibleAutomaton.QuadLife);
        panel.add(textFieldRules);
        applyRulesButton = new JButton("Zastosuj zasadę");
        applyRulesButton.setEnabled(automatonSettings.getSelectedAutomaton() == PossibleAutomaton.GameOfLife ||
                automatonSettings.getSelectedAutomaton() == PossibleAutomaton.QuadLife);
        applyRulesButton.addActionListener(this);
        applyRulesButton.setActionCommand(Commands.CHANGE_TWO_DIM_RULES.toString());
        panel.add(applyRulesButton, new CC().wrap());

        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        settingsPanel.add(panel, new CC().alignX("left").wrap().grow());
        // ------------------------------------------------------------------------ \\
        panel = new JPanel(new MigLayout());
        panel.add(new BoldLabel("Sąsiedztwa"), new CC().wrap().alignX("left"));
        group = new ButtonGroup();

        radioButtonMoore = new JRadioButton("Moore");
        radioButtonMoore.setActionCommand(Commands.CHANGE_NEIGHBORHOOD_TYPE.toString());
        radioButtonMoore.addActionListener(this);
        radioButtonMoore.setSelected(automatonSettings.getNeighborHood() == CellNeighborhoodType.Moore);
        radioButtonMoore.setEnabled(automatonSettings.getNeighborHood() != CellNeighborhoodType.OneDim);
        group.add(radioButtonMoore);
        panel.add(radioButtonMoore, new CC().alignX("center"));

        radioButtonVonNeumann = new JRadioButton("von Neumann");
        radioButtonVonNeumann.setActionCommand(Commands.CHANGE_NEIGHBORHOOD_TYPE.toString());
        radioButtonVonNeumann.addActionListener(this);
        radioButtonVonNeumann.setSelected(automatonSettings.getNeighborHood() == CellNeighborhoodType.VonNeumann);
        radioButtonVonNeumann.setEnabled(automatonSettings.getNeighborHood() != CellNeighborhoodType.OneDim);
        group.add(radioButtonVonNeumann);
        panel.add(radioButtonVonNeumann, new CC().alignX("center"));

        radioButtonOneDim = new JRadioButton("Jednowymiarowe");
        radioButtonOneDim.setActionCommand(Commands.CHANGE_NEIGHBORHOOD_TYPE.toString());
        radioButtonOneDim.addActionListener(this);
        radioButtonOneDim.setSelected(automatonSettings.getNeighborHood() == CellNeighborhoodType.OneDim);
        radioButtonOneDim.setEnabled(automatonSettings.getNeighborHood() == CellNeighborhoodType.OneDim);
        group.add(radioButtonOneDim);
        panel.add(radioButtonOneDim, new CC().alignX("center").wrap());

        panel.add(new Label("Promień sąsiedztwa (r):"), new CC().alignX("left"));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        spinnerModel = new SpinnerNumberModel(automatonSettings.getNeighborhoodRadius(), 0, 100, 1);

        radiusSpinner = new JSpinner(spinnerModel);
        radiusSpinner.addChangeListener(this);
        radiusSpinner.setName(Commands.CHANGE_NEIGHBORHOOD_RADIUS.toString());
        radiusSpinner.setEnabled(automatonSettings.getSelectedAutomaton() != PossibleAutomaton.Jednowymiarowy);
        panel.add(radiusSpinner);

        wrappingCheckBox = new JCheckBox("Zawijać planszę?");
        wrappingCheckBox.addActionListener(this);
        wrappingCheckBox.setSelected(automatonSettings.getWrap());
        wrappingCheckBox.setActionCommand(Commands.SET_WRAP.toString());
        panel.add(wrappingCheckBox);
        settingsPanel.add(panel, new CC().alignX("left").wrap().grow());
        // ------------------------------------------------------------------------ \\
        panel = new JPanel(new MigLayout());
        Label tmpLabel = new BoldLabel("Opóźnienie między kolejnymi symulacjami: " + automatonSettings.getSimulationDelay() + " [ms]");
        panel.add(tmpLabel, new CC().alignX("left").cell(0, 0));

        JSlider slider = new JSlider(0, 1000, 0);
        slider.setMinorTickSpacing(50);
        slider.setMajorTickSpacing(250);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setName(Commands.CHANGE_SIMULATION_DELAY.toString());
        slider.setValue(automatonSettings.getSimulationDelay());
        slider.addChangeListener(this);
        final JSlider finalSlider = slider;
        slider.addChangeListener(e1 -> tmpLabel.setText("Opóźnienie między kolejnymi symulacjami: " + finalSlider.getValue() + " [ms]"));
        panel.add(slider, new CC().push().cell(0, 1).grow());
        //panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        settingsPanel.add(panel, new CC().wrap().grow());
        // ------------------------------------------------------------------------ \\
        JPanel navigationButtonsPanel = new JPanel(new MigLayout());
        JButton startButton = new JButton("Start");
        startButton.setActionCommand(Commands.START_AUTOMATON.toString());
        startButton.addActionListener(this);
        startButton.setEnabled(false);
        navigationButtonsPanel.add(startButton, new CC().grow().push());
        disabledWhenRunning.add(startButton);

        JButton pauseButton = new JButton("Pauza");
        pauseButton.setActionCommand(Commands.PAUSE_AUTOMATON.toString());
        pauseButton.addActionListener(this);
        pauseButton.setEnabled(false);
        navigationButtonsPanel.add(pauseButton, new CC().grow().push().wrap());
        disabledWhenNotRunning.add(pauseButton);

        JButton randButton = new JButton("Losuj");
        randButton.setActionCommand(Commands.RAND_CELLS.toString());
        randButton.addActionListener(this);
        navigationButtonsPanel.add(randButton, new CC().grow().push());
        disabledWhenRunning.add(randButton);

        JButton clearButton = new JButton("Wyczyść");
        clearButton.setActionCommand(Commands.CLEAR_AUTOMATON.toString());
        clearButton.addActionListener(this);
        navigationButtonsPanel.add(clearButton, new CC().grow().push().wrap());
        disabledWhenRunning.add(clearButton);

        structuresList = new JComboBox<>();
        setStructureList(automatonSettings.getSelectedAutomaton());
        structuresList.setActionCommand(Commands.CHANGE_STRUCTURE.toString());
        structuresList.addActionListener(this);
        navigationButtonsPanel.add(structuresList, new CC().grow().push());
        structuresList.setOpaque(true);

        insertStructButton = new JButton("Wstaw strukturę");
        insertStructButton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        insertStructButton.setActionCommand(Commands.INSERT_STRUCT.toString());
        insertStructButton.addActionListener(this);
        navigationButtonsPanel.add(insertStructButton, new CC().grow().push().wrap());

        colorPicker = new JButton("Wybierz kolor");
        colorPicker.setEnabled(false);
        colorPicker.setVisible(automatonSettings.getSelectedAutomaton() == PossibleAutomaton.Langton);
        colorPicker.addActionListener(e ->
        {
            Color c = JColorChooser.showDialog(null, "Wybierz kolor", Color.RED);
            if (c != null)
                choosedColor = c;
        });
        navigationButtonsPanel.add(colorPicker, new CC().grow().push());


        // ------------------------------------------------------------------------ \\
        JPanel statisticsPanel = new JPanel(new GridLayout(4, 2));
        statisticsPanel.setName("statisticPanel");
        //statisticsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        generationCountLabel = new JLabel("Liczba generacji: 0");
        simulationTimeLabel = new JLabel("Czas symulacji jednej: 0");
        aliveCellsCountLabel = new JLabel("Liczba żywych komórek: 0");
        renderTimeLabel = new JLabel("Czas renderowania: 0");
        totalCellsLabel = new JLabel("Wszystkich komórek: 0");
        deadCellsLabel = new JLabel("Martwych komórek: 0");
        onePassTimeLabel = new JLabel("Czas jednego przejścia: 0");
        statisticsPanel.add(generationCountLabel);
        statisticsPanel.add(simulationTimeLabel);
        statisticsPanel.add(aliveCellsCountLabel);
        statisticsPanel.add(renderTimeLabel);
        statisticsPanel.add(deadCellsLabel);
        statisticsPanel.add(onePassTimeLabel);
        statisticsPanel.add(totalCellsLabel);
        //settingsPanel.add(statisticsPanel, new CC().wrap().pushX().growX());
        settingsPanel.add(navigationButtonsPanel, new CC().wrap().pushX().growX());
        settingsPanel.add(statisticsPanel, new CC().wrap().pushX().growX().dockSouth());
        //  ------------------------------------------------------------------------ \\

        mainPanel.setLayout(new MigLayout("", "[grow, 80%][grow, 20%]", "[grow]"));
        mainPanel.add(automatonPanel, new CC().grow());
        mainPanel.add(settingsPanel, new CC().grow());
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
        StructureLoader structureLoader;
        if (selectedAutomaton == PossibleAutomaton.GameOfLife) {
            structureLoader = new RLEFormatStructureLoader(BinaryState.ALIVE, BinaryState.DEAD);
        } else if (selectedAutomaton == PossibleAutomaton.QuadLife) {
            structureLoader = new RLEFormatStructureLoader(QuadState.BLUE, QuadState.DEAD);
        } else if (selectedAutomaton == PossibleAutomaton.WireWorld) {
            structureLoader = new WireWorldStructureLoader();
        } else if (selectedAutomaton == PossibleAutomaton.Jednowymiarowy) {
            structureLoader = new OneDimStructureLoader();
        } else if (selectedAutomaton == PossibleAutomaton.Langton) {
            structureLoader = new LangtonAntStructureLoader();
        } else {
            throw new IllegalArgumentException("Invalid automaton type");
        }
        for (StructureInfo structureInfo : structureLoader.getAvailableStructures(selectedAutomaton))
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

        automatonPanel.setCursor(blankCursor);
    }

    void setStateCancelSelectingStruct() {
        insertStructButton.setText("Wstaw strukturę");
        colorPicker.setEnabled(false);
        insertStructButton.setActionCommand(Commands.INSERT_STRUCT.toString());
        if (rememberState == AutomatonState.PAUSED)
            setStatePaused();
        else if (rememberState == AutomatonState.RUNNING)
            setStateRunning();
        automatonPanel.setCursor(Cursor.getDefaultCursor());
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
        disableComponentList(componentList);
    }

    private void disableComponentList(ArrayList<Component> componentList) {
        for (Component component : componentList)
            component.setEnabled(false);
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
