package agh.edu.pl.gui;

import com.horstmann.corejava.GBC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Dominik on 2015-12-10.
 */
public class MainWindowDesign extends JFrame
{
    protected AutomataPanel automataPanel = new AutomataPanel();
    protected Label generationCountLabel;
    protected Label simulationTimeLabel;
    protected Label aliveCellsCount;

    protected MainWindowDesign()
    {
        initUI();
    }

    private void initUI()
    {
        setTitle("Automat komórkowy");
        setSize(1500, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        // automata window
        mainPanel.add(automataPanel, new GBC(0, 0).setFill(GridBagConstraints.BOTH).setWeight(0.99, 1));

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(16, 1));
        settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(settingsPanel, new GBC(1, 0).setFill(GridBagConstraints.BOTH).setWeight(0.01, 1));

        ActionListener listener = e -> {
            JRadioButton btn = (JRadioButton) e.getSource();
            PossibleAutomaton automata = Arrays.stream(PossibleAutomaton.values()).filter(t -> Objects.equals(t.toString(), btn.getText())).findAny().get();
            automataPanel.setAutomaton(automata);
        };

        // ------------------------------------------------------------------------ \\
        settingsPanel.add(new Label("Typ automatu"));
        ButtonGroup group = new ButtonGroup();
        JPanel panelRadio = new JPanel();
        for (PossibleAutomaton automaton : PossibleAutomaton.values())
        {
            JRadioButton radio = new JRadioButton(automaton.toString());
            group.add(radio);
            radio.addActionListener(listener);

            if(automaton == PossibleAutomaton.GameOfLive)
            {
                radio.setSelected(true);
                automataPanel.setAutomaton(PossibleAutomaton.GameOfLive);
            }

            panelRadio.add(radio);
        }
        settingsPanel.add(panelRadio);

        // ------------------------------------------------------------------------ \\
        settingsPanel.add(new Label("Rozmiar komórki"));

        JSlider slider = new JSlider(3, 100, 20);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(ce -> automataPanel.setCellSize(((JSlider) ce.getSource()).getValue()));
        automataPanel.setCellSize(slider.getValue());
        settingsPanel.add(slider);
        // ------------------------------------------------------------------------ \\

        settingsPanel.add(new Label("Szybkość symulacji [ms]"));

        slider = new JSlider(0, 1000, 100);
        slider.setMinorTickSpacing(50);
        slider.setMajorTickSpacing(250);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(ce -> automataPanel.setSimulationSpeed(((JSlider) ce.getSource()).getValue()));
        automataPanel.setSimulationSpeed(slider.getValue());
        settingsPanel.add(slider);
        // ------------------------------------------------------------------------ \\
        JPanel statisticsPanel = new JPanel(new GridLayout(2,2));
        generationCountLabel = new Label("Liczba generacji: 0");
        simulationTimeLabel = new Label("Czas symulacji jednej: 0");
        aliveCellsCount = new Label("Liczba żywych komórek: 0");
        statisticsPanel.add(generationCountLabel);
        statisticsPanel.add(simulationTimeLabel);
        statisticsPanel.add(aliveCellsCount);
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
    protected void setAliveCellsCount(int count)
    {
        aliveCellsCount.setText("Liczba żywych komórek: " + count);
    }
}
