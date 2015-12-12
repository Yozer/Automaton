package agh.edu.pl.gui;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.automata.GameOfLife;
import agh.edu.pl.automaton.automata.QuadLife;
import agh.edu.pl.automaton.automata.WireWorld;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.cells.states.WireElectronState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.GeneralStateFactory;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AutomataPanel extends JPanel
{
    private int simulationDelay = 500;
    private final Timer timerAutomata;

    private Automaton automaton;
    private PossibleAutomaton selectedAutomaton;

    private int cellSize;
    private final int cellBorderThickness = 1;

    private int lastSimulationTime = 0;
    private int generationCount = 0;
    private int aliveCellsCount = 0;

    public AutomataPanel()
    {
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setOpaque(true);

        timerAutomata = new Timer(simulationDelay, simulateNextGeneration());
        timerAutomata.setRepeats(true);
        timerAutomata.setCoalesce(true);

        addListeners();
    }

    private void addListeners()
    {
        addComponentListener(new ComponentListener()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                resetAutomata();
            }
            @Override
            public void componentMoved(ComponentEvent e) {}
            @Override
            public void componentShown(ComponentEvent e) {}
            @Override
            public void componentHidden(ComponentEvent e) {}
        });
    }

    private void resetAutomata()
    {
        int width = getWidth();
        int height = getHeight();

        // window not created
        if(width == 0 || height == 0)
            return;
        if(!timerAutomata.isRunning())
            timerAutomata.start();

        generationCount = 0;
        aliveCellsCount = 0;
        Map<CellCoordinates, CellState> someRand = new HashMap<>();
        Random random = new Random();

        for(int x = 0; x < width/cellSize ; x++)
        {
            for(int y = 0; y < height/cellSize; y++)
            {
                if(selectedAutomaton == PossibleAutomaton.GameOfLive)
                {
                    List<BinaryState> values = Arrays.stream(BinaryState.values()).collect(Collectors.toList());
                    values.add(BinaryState.DEAD);
                    values.add(BinaryState.DEAD);
                    values.add(BinaryState.DEAD);
                    someRand.put(new Coords2D(x, y),  values.get(random.nextInt(values.size())));
                }
                else if(selectedAutomaton == PossibleAutomaton.QuadLife)
                {
                    List<QuadState> values = Arrays.stream(QuadState.values()).collect(Collectors.toList());
                    values.add(QuadState.DEAD);
                    values.add(QuadState.DEAD);
                    values.add(QuadState.DEAD);
                    someRand.put(new Coords2D(x, y), values.get(random.nextInt(values.size())));
                }
                else if(selectedAutomaton == PossibleAutomaton.WireWorld)
                {
                    List<WireElectronState> values = Arrays.stream(WireElectronState.values()).collect(Collectors.toList());
                    values.add(WireElectronState.VOID);
                    values.add(WireElectronState.VOID);
                    values.add(WireElectronState.VOID);
                    someRand.put(new Coords2D(x, y), values.get(random.nextInt(values.size())));
                }
            }
        }

        if(selectedAutomaton == PossibleAutomaton.GameOfLive)
        {
            CellStateFactory factory = new GeneralStateFactory(someRand);
            //CellStateFactory factory = new UniformStateFactory(BinaryState.DEAD);
            CellNeighborhood neighborhood = new MoorNeighborhood(1, true, width / cellSize, height / cellSize);
            automaton = new GameOfLife(new HashSet<>(Arrays.asList(2,3)), new HashSet<>(Collections.singletonList(3)), width / cellSize, height / cellSize, factory, neighborhood);
        }
        else if(selectedAutomaton == PossibleAutomaton.QuadLife)
        {
            CellStateFactory factory = new GeneralStateFactory(someRand);
            CellNeighborhood neighborhood = new MoorNeighborhood(1, false, width / cellSize, height / cellSize);
            automaton = new QuadLife( width / cellSize, height / cellSize, factory, neighborhood);
        }
        else if(selectedAutomaton == PossibleAutomaton.WireWorld)
        {
            CellStateFactory factory = new GeneralStateFactory(someRand);
            CellNeighborhood neighborhood = new MoorNeighborhood(1, false, width / cellSize, height / cellSize);
            automaton = new WireWorld(width / cellSize, height / cellSize, factory, neighborhood);
        }
        else
        {
            return;
        }

        //HashMap<CellCoordinates, CellState> blinker = new HashMap<>();
        // blinker
       // blinker.put(new Coords2D(15, 20), BinaryState.ALIVE);
        //blinker.put(new Coords2D(15, 21), BinaryState.ALIVE);
        //blinker.put(new Coords2D(15, 22), BinaryState.ALIVE);
       // automaton.insertStructure(blinker);

        /*HashMap<CellCoordinates, CellState> glider = new HashMap<>();
        glider.put(new Coords2D(5, 5), BinaryState.ALIVE);
        glider.put(new Coords2D(6, 5), BinaryState.ALIVE);
        glider.put(new Coords2D(7, 5), BinaryState.ALIVE);
        glider.put(new Coords2D(7, 4), BinaryState.ALIVE);
        glider.put(new Coords2D(6, 3), BinaryState.ALIVE);
        automaton.insertStructure(glider);*/
    }

    private void drawAutomata(Graphics g)
    {
        if(automaton == null)
            return;
        Graphics2D g2d = ((Graphics2D) g);

        if(automaton instanceof Automaton2Dim)
        {
            for (Cell cell : automaton)
            {
                Coords2D coords = (Coords2D) cell.getCoords();

                g2d.setPaint(cell.getState().toColor());
                g2d.fillRect(coords.getX() * cellSize, coords.getY() * cellSize, cellSize, cellSize);
                g2d.setPaint(Color.BLACK);

                Stroke oldStroke = g2d.getStroke();
                g2d.setStroke(new BasicStroke(cellBorderThickness));
                g2d.drawRect(coords.getX() * cellSize, coords.getY() * cellSize, cellSize, cellSize);
                g2d.setStroke(oldStroke);
            }
        }

        g2d.dispose();
    }
    private ActionListener simulateNextGeneration()
    {
        return e -> {
            long before = System.nanoTime();

            repaint();
            aliveCellsCount = automaton.nextState();

            long after = System.nanoTime();
            long diff = after - before;
            lastSimulationTime = (int) (diff/1000000f);
            generationCount++;
        };
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        drawAutomata(g);
    }

    public void setCellSize(int cellSize)
    {
        this.cellSize = cellSize;
        resetAutomata();
    }
    public void setAutomaton(PossibleAutomaton selectedAutomaton)
    {
        this.selectedAutomaton = selectedAutomaton;
        resetAutomata();
    }

    public void setSimulationSpeed(int simulationSpeed)
    {
        this.simulationDelay = simulationSpeed;
        timerAutomata.setDelay(this.simulationDelay);
    }

    public int getLastSimulationTime()
    {
        return lastSimulationTime;
    }

    public int getGenerationCount()
    {
        return generationCount;
    }

    public int getAliveCellsCount()
    {
        return aliveCellsCount;
    }
}
