package agh.edu.pl.gui.logic;

import agh.edu.pl.automaton.automata.WireWorld;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.CellCoordinates;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.cells.states.WireElectronState;
import agh.edu.pl.gui.enums.PossibleAutomaton;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Dominik on 2015-12-18.
 */
class ResetSwingWorker extends SwingWorker<Void, Void>
{
    private final AutomatonManager automatonManager;
    private final Runnable invokeAfter;
    private final boolean startImmediately;

    public ResetSwingWorker(AutomatonManager automatonManager, Runnable invokeAfter, boolean startImmediately)
    {
        this.automatonManager = automatonManager;
        this.invokeAfter = invokeAfter;
        this.startImmediately = startImmediately;
    }

    @Override
    protected Void doInBackground()
    {
        automatonManager.reset();
        return null;
    }

    @Override
    protected void done()
    {
        if(startImmediately)
        {
            automatonManager.start();
        }
        invokeAfter.run();
    }
}

class PauseSwingWorker extends SwingWorker<Void, Void>
{
    private final AutomatonManager automatonManager;
    private final Runnable invokeAfter;

    public PauseSwingWorker(AutomatonManager automatonManager, Runnable invokeAfter)
    {

        this.automatonManager = automatonManager;
        this.invokeAfter = invokeAfter;
    }

    @Override
    protected Void doInBackground() throws Exception
    {
        automatonManager.pause();
        return null;
    }

    @Override
    protected void done()
    {
        invokeAfter.run();
    }
}

class InsertPrimeSwingWorker extends SwingWorker<Void, Void>
{
    private final AutomatonManager manager;
    private final Runnable invokeAfter;

    public InsertPrimeSwingWorker(AutomatonManager manager, Runnable invokeAfter)
    {
        this.manager = manager;
        this.invokeAfter = invokeAfter;
    }
    @Override
    protected Void doInBackground()
    {
        /*List<Cell> primeStructure = new ArrayList<>(manager.settings.getHeight() * manager.settings.getHeight());
        primeStructure.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(0, 3)));
        primeStructure.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(1, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(2, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(3, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(4, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(5, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(6, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(6, 2)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(6, 4)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(7, 2)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(7, 4)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(8, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(9, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(10, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(11, 3)));*/

        /*primeStructure.add(new Cell(WireElectronState.ELECTRON_TAIL, new Coords2D(0, 3)));
        primeStructure.add(new Cell(WireElectronState.ELECTRON_HEAD, new Coords2D(1, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(2, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(3, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(4, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(5, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(7, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(6, 2)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(6, 4)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(7, 2)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(7, 4)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(8, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(9, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(10, 3)));
        primeStructure.add(new Cell(WireElectronState.WIRE, new Coords2D(11, 3)));*/

        /*BufferedReader reader = null;
        File file = new File("primes.wi");
        try
        {
            reader = new BufferedReader(new FileReader(file));
            String line;
            int x = 50;
            while ((line = reader.readLine()) != null)
            {
                int y = 50;
                for(int i = 0; i < line.length(); i++, y++)
                {
                    primeStructure.add(new Cell(line.charAt(i) == ' ' ? WireElectronState.VOID :
                            line.charAt(i) == '#' ? WireElectronState.WIRE :
                                    line.charAt(i) == '@' ? WireElectronState.ELECTRON_HEAD : WireElectronState.ELECTRON_TAIL, new Coords2D(y, x)));
                }
                x++;
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }*/

        manager.automaton.insertStructure(primeStructure);
        manager.statistics.aliveCellsCount.set(manager.automaton.getAliveCount());
        manager.statistics.deadCellsCount.set(manager.statistics.totalCellsCount.get() - manager.statistics.aliveCellsCount.get());
        manager.drawCurrentAutomaton();
        manager.automatonPanel.paintImmediately(0, 0, manager.automatonPanel.getWidth(), manager.automatonPanel.getHeight());
        return null;
    }
    @Override
    protected void done()
    {
        invokeAfter.run();
    }
}

class RandCellsWorker extends SwingWorker<Void, Void>
{
    private final AutomatonManager manager;
    private final Runnable invokeAfter;

    public RandCellsWorker(AutomatonManager manager, Runnable invokeAfter)
    {
        this.manager = manager;
        this.invokeAfter = invokeAfter;
    }

    @Override
    protected Void doInBackground()
    {
        List<Cell> someRand = new ArrayList<>(manager.settings.getHeight() * manager.settings.getHeight());
        Random random = new Random();

        manager.reset();
        List<CellState> values = null;
        if (manager.settings.getSelectedAutomaton() == PossibleAutomaton.GameOfLive)
        {
            values = Arrays.stream(BinaryState.values()).collect(Collectors.toList());
            for (int i = 0; i < 3; i++)
                values.add(BinaryState.DEAD);

        } else if (manager.settings.getSelectedAutomaton() == PossibleAutomaton.QuadLife)
        {
            values = Arrays.stream(QuadState.values()).collect(Collectors.toList());
            for (int i = 0; i < 4; i++)
                values.add(QuadState.DEAD);

        } else if (manager.settings.getSelectedAutomaton() == PossibleAutomaton.WireWorld)
        {
            values = Arrays.stream(WireElectronState.values()).collect(Collectors.toList());
            for (int i = 0; i < 20; i++)
                values.add(WireElectronState.VOID);
        }

        for (int x = 0; x < manager.settings.getWidth(); x++)
        {
            for (int y = 0; y < manager.settings.getHeight(); y++)
            {
                someRand.add(new Cell(values.get(random.nextInt(values.size())), new Coords2D(x, y)));
            }
        }


        /*HashMap<CellCoordinates, CellState> blinker = new HashMap<>();
        blinker.put(new Coords2D(15, 20), BinaryState.ALIVE);
        blinker.put(new Coords2D(15, 21), BinaryState.ALIVE);
        blinker.put(new Coords2D(15, 22), BinaryState.ALIVE);
        manager.automaton.insertStructure(blinker);*/

        /*HashMap<CellCoordinates, CellState> glider = new HashMap<>();
        glider.put(new Coords2D(5, 5), BinaryState.ALIVE);
        glider.put(new Coords2D(6, 5), BinaryState.ALIVE);
        glider.put(new Coords2D(7, 5), BinaryState.ALIVE);
        glider.put(new Coords2D(7, 4), BinaryState.ALIVE);
        glider.put(new Coords2D(6, 3), BinaryState.ALIVE);
        manager.automaton.insertStructure(glider);*/

        manager.automaton.insertStructure(someRand);
        manager.statistics.aliveCellsCount.set(manager.automaton.getAliveCount());
        manager.statistics.deadCellsCount.set(manager.statistics.totalCellsCount.get() - manager.statistics.aliveCellsCount.get());
        manager.drawCurrentAutomaton();
        manager.automatonPanel.paintImmediately(0, 0, manager.automatonPanel.getWidth(), manager.automatonPanel.getHeight());
        return null;
    }

    @Override
    protected void done()
    {
        invokeAfter.run();
    }
}

