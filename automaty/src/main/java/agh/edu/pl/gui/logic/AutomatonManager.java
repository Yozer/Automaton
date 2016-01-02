package agh.edu.pl.gui.logic;

import agh.edu.pl.automaton.Automaton;
import agh.edu.pl.automaton.Automaton1Dim;
import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.automata.ElementaryAutomaton;
import agh.edu.pl.automaton.automata.GameOfLife;
import agh.edu.pl.automaton.automata.QuadLife;
import agh.edu.pl.automaton.automata.WireWorld;
import agh.edu.pl.automaton.automata.langton.Ant;
import agh.edu.pl.automaton.automata.langton.LangtonAnt;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.MoorNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.OneDimensionalNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.VonNeumannNeighborhood;
import agh.edu.pl.automaton.cells.states.BinaryAntState;
import agh.edu.pl.automaton.cells.states.BinaryState;
import agh.edu.pl.automaton.cells.states.QuadState;
import agh.edu.pl.automaton.cells.states.WireElectronState;
import agh.edu.pl.automaton.satefactory.UniformStateFactory;
import agh.edu.pl.gui.enums.CellNeighborhoodType;
import agh.edu.pl.gui.enums.PossibleAutomaton;
import agh.edu.pl.gui.logic.exceptions.IllegalRulesFormatException;
import agh.edu.pl.gui.structures.AntStructureInfo;
import agh.edu.pl.gui.structures.StructureInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is a bridge between gui and {@code Automaton}.
 * @author Dominik Baran
 */
public class AutomatonManager {
    private final SimulationThread simulationThread;
    @SuppressWarnings("FieldCanBeLocal")
    private final Thread simulationThreadObject;
    private final AtomicInteger simulationDelay = new AtomicInteger(0);
    private final AutomatonSettings settings = new AutomatonSettings();
    private final AutomatonStatistics statistics = new AutomatonStatistics();
    private final AutomatonPanel automatonPanel;
    private Automaton automaton;
    private int automaton1DimCurrentRow;
    private boolean settingsHasChanged = true;

    /**
     * @param automatonPanel AutomatonPanel where cells should be rendered
     */
    public AutomatonManager(AutomatonPanel automatonPanel) {
        this.simulationDelay.set(settings.getSimulationDelay());
        this.automatonPanel = automatonPanel;
        this.simulationThread = new SimulationThread(this);
        this.simulationThreadObject = new Thread(simulationThread, "SimulationThread");
        simulationThreadObject.start();
    }

    /**
     * Starts automaton. This method returns immediately.
     * @param invokeAfter {@code Runnable to invoke after execution finishes}
     */
    public void start(Runnable invokeAfter) {
        resetAutomatonIfSettingsHasChanged();
        simulationThread.resumeThread();
        invokeAfter.run();
    }
    /**
     * Clears automaton. Sets all cells to dead. This method returns immediately - it's invoked in another thread.
     * @param invokeAfter {@code Runnable to invoke after execution finishes}
     */
    public void clearAutomaton(Runnable invokeAfter) {
        SwingWorker swingWorker = new ClearSwingWorker(this, invokeAfter);
        swingWorker.execute();
    }
    /**
     * Pauses automaton. This method returns immediately - it's invoked in another thread.
     * @param invokeAfter {@code Runnable to invoke after execution finishes}
     */
    public void pause(Runnable invokeAfter) {
        SwingWorker swingWorker = new PauseSwingWorker(this, invokeAfter);
        swingWorker.execute();
    }

    /**
     * Insert structure to current automaton. You can invoke it even when automaton is running.
     * This method returns immediately - it's invoked in another thread.
     * @param structureInfo Structure to insert
     * @param x x coordinate relative to automaton coordinates
     * @param y y coordinate relative to automaton coordinates
     * @param structRotation Structure rotation in radians. Should be 0, 90, 180 or 270 degree.
     */
    public void insertStructure(StructureInfo structureInfo, int x, int y, double structRotation) {
        SwingWorker swingWorker = new InsertStructureSwingWorker(this, structureInfo, x, y, structRotation);
        swingWorker.execute();
    }

    /**
     * Insert ant to current automaton. You can invoke it even when automaton is running.
     * @param structureInfo Structure to insert
     * @param x x coordinate relative to automaton coordinates
     * @param y y coordinate relative to automaton coordinates
     * @param antColor Color for ant
     */
    public void insertAnt(AntStructureInfo structureInfo, int x, int y, Color antColor) {
        resetAutomatonIfSettingsHasChanged();
        Coords2D atPoint = new Coords2D(x, y);
        if (atPoint.getX() + structureInfo.getWidth() > settings.getWidth() || atPoint.getY() + structureInfo.getHeight() > settings.getHeight())
            return;

        boolean isRunning = simulationThread.isRunning();
        if (isRunning)
            pause();

        ((LangtonAnt) automaton).addAnt(atPoint, antColor, structureInfo.getState());
        drawCurrentAutomaton();
        automatonPanel.repaint();

        if (isRunning)
            start();
    }
    /**
     * Rands cell states for given automaton. This method returns immediately - it's invoked in another thread.
     * @param invokeAfter {@code Runnable to invoke after execution finishes}
     */
    public void randCells(Runnable invokeAfter) {
        SwingWorker worker = new RandCellsWorker(this, invokeAfter);
        worker.execute();
    }

    private void init() {
        pause();
        statistics.resetStatistics();
        initAutomatonPanel();
        statistics.setTotalCellsCount(settings.getWidth() * settings.getHeight());

        // get automaton from settings
        automaton = getAutomatonFromSettings();
        if (automaton instanceof Automaton1Dim)
            automaton1DimCurrentRow = 0;
    }

    void pause() {
        simulationThread.pauseThread();
    }

    void start() {
        simulationThread.resumeThread();
    }

    Automaton getAutomaton() {
        return automaton;
    }

    void resetAutomatonOneDimRow() {
        automaton1DimCurrentRow = 0;
    }

    void decrementAutomatonOneDimRow() {
        if (automaton1DimCurrentRow > 0)
            automaton1DimCurrentRow--;
    }

    void drawCurrentAutomaton() {
        int[] pixels = automatonPanel.getPixelsForDrawing();


        if (automaton instanceof Automaton2Dim) {
            Iterator<Cell> cellIterator = automaton.iteratorChangedOnly();
            Cell cell;
            synchronized (automatonPanel.LOCKER) {
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
                    Coords2D coords = (Coords2D) cell.getCoords();
                    pixels[coords.getY() * settings.getWidth() + coords.getX()] = cell.getState().toColor().getRGB();
                }
            }

            if (settings.getSelectedAutomaton() == PossibleAutomaton.Langton) {
                LangtonAnt langtonAnt = ((LangtonAnt) automaton);
                synchronized (automatonPanel.LOCKER) {
                    for (Ant ant : langtonAnt.getAnts()) {
                        pixels[ant.getCoordinates().getY() * settings.getWidth() + ant.getCoordinates().getX()] = Color.YELLOW.getRGB();
                    }
                }
            }
        } else if (automaton instanceof Automaton1Dim) {
            synchronized (automatonPanel.LOCKER) {
                if (automaton1DimCurrentRow == settings.getHeight()) {
                    for (int row = 1; row < settings.getHeight(); row++) {
                        System.arraycopy(pixels, row * settings.getWidth(), pixels, (row - 1) * settings.getWidth(), settings.getWidth());
                    }
                    --automaton1DimCurrentRow;
                }

                for (Cell cell : automaton) {
                    Coords1D coords = (Coords1D) cell.getCoords();
                    pixels[automaton1DimCurrentRow * settings.getWidth() + coords.getX()] = cell.getState().toColor().getRGB();
                }
            }
            ++automaton1DimCurrentRow;
        }
    }

    void repaint() {
        automatonPanel.repaint();
    }

    private void initAutomatonPanel() {
        automatonPanel.createBufferedImage(settings.getWidth(), settings.getHeight());
        automatonPanel.repaint();
    }

    private void executeActionWhenRunning(Runnable runnable) {
        if (automaton != null) {
            boolean isRunning = simulationThread.isRunning();
            if (isRunning)
                pause();
            runnable.run();
            if (isRunning)
                start();
        }
    }

    private void setNeighborhoodFromSettings() {
        executeActionWhenRunning(() -> automaton.setNeighborhood(getCellNeighborhoodFromSettings()));
    }

    private void setRulesFromSettings() {
        executeActionWhenRunning(() ->
        {
            if (settings.getSelectedAutomaton() == PossibleAutomaton.GameOfLife) {
                ((GameOfLife) automaton).setComeAliveFactors(settings.getComeAliveFactors());
                ((GameOfLife) automaton).setSurviveFactors(settings.getSurviveFactors());
            } else if (settings.getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy) {
                ((ElementaryAutomaton) automaton).setRule(settings.getOneDimRule());
            }
        });
    }

    private Automaton getAutomatonFromSettings() {
        CellNeighborhood neighborhood = getCellNeighborhoodFromSettings();
        if (settings.getSelectedAutomaton() == PossibleAutomaton.GameOfLife) {
            UniformStateFactory stateFactory = new UniformStateFactory(BinaryState.DEAD);
            return new GameOfLife(settings.getSurviveFactors(), settings.getComeAliveFactors(), settings.getWidth(), settings.getHeight(),
                    stateFactory, neighborhood);
        } else if (settings.getSelectedAutomaton() == PossibleAutomaton.QuadLife) {
            UniformStateFactory stateFactory = new UniformStateFactory(QuadState.DEAD);
            return new QuadLife(settings.getWidth(), settings.getHeight(), stateFactory, neighborhood);
        } else if (settings.getSelectedAutomaton() == PossibleAutomaton.WireWorld) {
            UniformStateFactory stateFactory = new UniformStateFactory(WireElectronState.VOID);
            return new WireWorld(settings.getWidth(), settings.getHeight(), stateFactory, neighborhood);
        } else if (settings.getSelectedAutomaton() == PossibleAutomaton.Jednowymiarowy) {
            UniformStateFactory stateFactory = new UniformStateFactory(BinaryState.DEAD);
            return new ElementaryAutomaton(settings.getWidth(), settings.getOneDimRule(), stateFactory);
        } else if (settings.getSelectedAutomaton() == PossibleAutomaton.Langton) {
            UniformStateFactory stateFactory = new UniformStateFactory(new BinaryAntState(BinaryState.DEAD));
            return new LangtonAnt(settings.getWidth(), settings.getHeight(), stateFactory, neighborhood);
        }
        return null;
    }

    private CellNeighborhood getCellNeighborhoodFromSettings() {
        CellNeighborhoodType neighborhoodType = settings.getNeighborHood();
        if (neighborhoodType == CellNeighborhoodType.Moore)
            return new MoorNeighborhood(settings.getNeighborhoodRadius(), settings.getWrap(), settings.getWidth(), settings.getHeight());
        else if (neighborhoodType == CellNeighborhoodType.VonNeumann)
            return new VonNeumannNeighborhood(settings.getNeighborhoodRadius(), settings.getWrap(), settings.getWidth(), settings.getHeight());
        else if (neighborhoodType == CellNeighborhoodType.OneDim)
            return new OneDimensionalNeighborhood(settings.getWrap(), settings.getWidth());
        return null;
    }

    void resetAutomatonIfSettingsHasChanged() {
        resetAutomatonIfSettingsHasChanged(false);
    }

    void resetAutomatonIfSettingsHasChanged(boolean force) {
        if (settingsHasChanged || force || automaton == null) {
            init();
            settingsHasChanged = false;
        }
    }

    public AutomatonSettings getSettings() {
        return settings;
    }

    public int getSimulationDelay() {
        return simulationDelay.get();
    }

    public void setSimulationDelay(int simulationDelay) {
        this.simulationDelay.set(simulationDelay);
        this.settings.setSimulationDelay(simulationDelay);
    }

    public void setSelectedAutomaton(PossibleAutomaton selectedAutomaton) {
        settings.setSelectedAutomaton(selectedAutomaton);
        settingsHasChanged = true;
        initAutomatonPanel();
    }

    public void setWidth(int width) {
        settings.setWidth(width);
        settingsHasChanged = true;
        initAutomatonPanel();
    }

    public void setHeight(int height) {
        settings.setHeight(height);
        settingsHasChanged = true;
        initAutomatonPanel();
    }

    public void setNeighborhoodType(CellNeighborhoodType neighborhoodType) {
        settings.setNeighborhood(neighborhoodType);
        setNeighborhoodFromSettings();
    }

    public void setNeighborhoodRadius(int neighborhoodRadius) {
        settings.setNeighborhoodRadius(neighborhoodRadius);
        setNeighborhoodFromSettings();
    }

    public void setWrap(boolean wrap) {
        settings.setWrap(wrap);
        setNeighborhoodFromSettings();
    }

    public void setRulesTwoDim(String rulesTwoDim) throws IllegalRulesFormatException {
        settings.setFormattedRules(rulesTwoDim);
        setRulesFromSettings();
    }

    public void setRuleOneDim(Integer ruleOneDim) {
        settings.setOneDimRule(ruleOneDim);
        setRulesFromSettings();
    }

    public AutomatonStatistics getStatistics() {
        return statistics;
    }

    public boolean isRunning() {
        return simulationThread.isRunning();
    }
}
