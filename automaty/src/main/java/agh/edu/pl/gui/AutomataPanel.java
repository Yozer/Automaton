package agh.edu.pl.gui;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.automata.GameOfLife;
import agh.edu.pl.automaton.automata.QuadLife;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;
import agh.edu.pl.automaton.satefactory.GeneralStateFactory;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import javafx.geometry.Pos;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AutomataPanel extends JPanel
{
    private int DELAY = 1000;
    private Timer timer;

    private Automaton automaton = null;
    private PossibleAutomaton selectedAutomaton;
    private int cellSize;

    public AutomataPanel()
    {
        setDoubleBuffered(true);
        initTimer();
    }
    private void resetAutomata()
    {
        int width = getWidth();
        int height = getHeight();

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
            }
        }

        if(selectedAutomaton == PossibleAutomaton.GameOfLive)
        {
            CellStateFactory factory = new GeneralStateFactory(someRand);
            CellNeighborhood neighborhood = new MoorNeighborhood(1, true, width / cellSize, height / cellSize);
            automaton = new GameOfLife(Arrays.asList(2,3), Collections.singletonList(3), width / cellSize, height / cellSize, factory, neighborhood);
        }
        else if(selectedAutomaton == PossibleAutomaton.QuadLife)
        {
            CellStateFactory factory = new GeneralStateFactory(someRand);
            CellNeighborhood neighborhood = new MoorNeighborhood(1, false, width / cellSize, height / cellSize);
            automaton = new QuadLife( width / cellSize, height / cellSize, factory, neighborhood);
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

    private void initTimer()
    {
        timer = new Timer(DELAY, e -> {
            repaint();
            if(automaton != null)
                automaton = automaton.nextState();
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();
    }

    Map<CellCoordinates, CellState> buffer = new HashMap<>();
    private void doDrawing(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if(automaton == null)
            return;

        if(automaton instanceof Automaton2Dim)
        {
            for (Cell cell : automaton)
            {
                Coords2D coords = (Coords2D) cell.getCoords();
                if(!buffer.containsKey(coords) || !Objects.equals(buffer.get(coords), cell.getState()))
                {
                    g2d.setPaint(Color.BLACK);
                    g2d.fillRect(coords.getX() * cellSize, coords.getY() * cellSize, cellSize, cellSize);
                    g2d.setPaint(cell.getState().toColor());
                    g2d.fillRect(coords.getX() * cellSize, coords.getY() * cellSize , cellSize , cellSize);
                    buffer.put(coords, cell.getState());
                }
            }
        }
    }


    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        doDrawing(g);
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

    public void setSymulationSpeed(int symulationSpeed)
    {
        this.DELAY = symulationSpeed;
        timer.setDelay(this.DELAY);
    }
}
