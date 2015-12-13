package agh.edu.pl.gui;

import com.horstmann.corejava.GBC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by Dominik on 2015-12-10.
 */
public abstract class MainWindowDesign extends JFrame implements ActionListener, ChangeListener
{
    protected AutomatonPanel automatonPanel;
    protected Label generationCountLabel, simulationTimeLabel, aliveCellsCountLabel;
    protected Label renderTimeLabel;
    protected JPanel settingsPanel;

    protected JButton randButton, startButton, pauseButton;

    // helps get default settings
    private final AutomatonSettings automatonSettings = new AutomatonSettings();

    protected MainWindowDesign()
    {
        initUI();
    }

    private void initUI()
    {
        setTitle("Automat komórkowy");
        //setSize(1920, 1080);
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

        JSlider slider = new JSlider(0, 20, 5);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setName(Commands.CHANGE_CELL_SIZE.toString());
        slider.setValue(automatonSettings.getCellSize());
        slider.addChangeListener(this);
        settingsPanel.add(slider);
        // ------------------------------------------------------------------------ \\

        settingsPanel.add(new Label("Opóźnienie między kolejnymi symulacjami [ms]"));

        slider = new JSlider(0, 1000, 0);
        slider.setMinorTickSpacing(50);
        slider.setMajorTickSpacing(250);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setName(Commands.CHANGE_SIMULATION_DELAY.toString());
        slider.addChangeListener(this);
        slider.setValue(automatonSettings.getSimulationDelay());
        settingsPanel.add(slider);
        // ------------------------------------------------------------------------ \\
        JPanel navigationButtonsPanel = new JPanel(new GridLayout(1, 3));
        startButton = new JButton("Start");
        startButton.setActionCommand(Commands.START_AUTOMATON.toString());
        startButton.addActionListener(this);
        navigationButtonsPanel.add(startButton);

        pauseButton = new JButton("Pauza");
        pauseButton.setActionCommand(Commands.PAUSE_AUTOMATON.toString());
        pauseButton.addActionListener(this);
        pauseButton.setEnabled(false);
        navigationButtonsPanel.add(pauseButton);

        randButton = new JButton("Losuj");
        randButton.setActionCommand(Commands.RAND_CELLS.toString());
        randButton.addActionListener(this);
        navigationButtonsPanel.add(randButton);
        settingsPanel.add(navigationButtonsPanel);
        // ------------------------------------------------------------------------ \\
        JPanel statisticsPanel = new JPanel(new GridLayout(2,2));
        statisticsPanel.setName("statisticPanel");
        generationCountLabel = new Label("Liczba generacji: 0");
        simulationTimeLabel = new Label("Czas symulacji jednej: 0");
        aliveCellsCountLabel = new Label("Liczba żywych komórek: 0");
        renderTimeLabel = new Label("Czas renderowania: 0");
        statisticsPanel.add(generationCountLabel);
        statisticsPanel.add(simulationTimeLabel);
        statisticsPanel.add(aliveCellsCountLabel);
        statisticsPanel.add(renderTimeLabel);
        settingsPanel.add(statisticsPanel);
        // ------------------------------------------------------------------------ \\

        add(mainPanel);
    }

    protected void setGenerationCountLabel(int count)
    {
        generationCountLabel.setText("Liczba generacji: " + count);
    }
    protected void setSimulationTimeLabel(int time)
    {
        simulationTimeLabel.setText("Czas symulacji jednej generacji: " + time);
    }
    protected void setAliveCellsCountLabel(int count)
    {
        aliveCellsCountLabel.setText("Liczba żywych komórek: " + count);
    }
    protected void setRenderTimeLabel(int time)
    {
        renderTimeLabel.setText("Czas renderowania: " + time);
    }
}
