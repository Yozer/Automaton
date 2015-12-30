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
import agh.edu.pl.automaton.cells.Cell;
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
import agh.edu.pl.gui.structures.LangtonAntStructureLoader;
import agh.edu.pl.gui.structures.StructureInfo;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dominik on 2015-12-13.
 */
// TODO fix GUI grid
// TODO check adding monster
// TODO fix adding struct for ant
// TODO fix alpha for new struct preview (border maybe)?
// TODO add "jumping" for inserting new structs
// TODO add rotation for inserting structs
// TODO add controlling distribution of each cell type during rand
// TODO make drawing 60fps - check if it would be faster then drawing each generation

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

    public AutomatonManager(AutomatonPanel automatonPanel) {
        this.simulationDelay.set(settings.getSimulationDelay());
        this.automatonPanel = automatonPanel;
        this.simulationThread = new SimulationThread(this);
        this.simulationThreadObject = new Thread(simulationThread, "SimulationThread");
        simulationThreadObject.start();
    }

    public void start(Runnable invokeAfter) {
        resetAutomatonIfSettingsHasChanged();
        simulationThread.resumeThread();
        invokeAfter.run();
    }

    public void init(Runnable invokeAfter) {
        SwingWorker swingWorker = new InitSwingWorker(this, invokeAfter);
        swingWorker.execute();
    }

    public void pause(Runnable invokeAfter) {
        SwingWorker swingWorker = new PauseSwingWorker(this, invokeAfter);
        swingWorker.execute();
    }

    public void insertStructure(StructureInfo structureInfo, int x, int y) {
        resetAutomatonIfSettingsHasChanged();
        SwingWorker swingWorker = new InsertStructureSwingWorker(this, structureInfo, x, y);
        swingWorker.execute();
    }

    public void insertAnt(StructureInfo structureInfo, int x, int y, Color antColor) {
//        resetAutomatonIfSettingsHasChanged();
//        Coords2D atPoint = new Coords2D((int) (x / settings.getCellCount()), (int) (y / settings.getCellCount()));
//        if (atPoint.getX() + structureInfo.getWidth() > settings.getWidth() || atPoint.getY() + structureInfo.getHeight() > settings.getHeight())
//            return;
//
//        LangtonAntStructureLoader.AntInfo ant;
//        try {
//            ant = new LangtonAntStructureLoader().loadAnt(structureInfo, atPoint);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        boolean isRunning = simulationThread.isRunning();
//        if (isRunning)
//            pause();
//
//        ((LangtonAnt) automaton).addAnt(ant.getAntCoords(), antColor, ant.getAntState());
//        drawCurrentAutomaton();
//        automatonPanel.repaint();
//
//        if (isRunning)
//            start();
    }

    public void randCells(Runnable invokeAfter) {
        resetAutomatonIfSettingsHasChanged();
        SwingWorker worker = new RandCellsWorker(this, invokeAfter);
        worker.execute();
    }

    void init() {
        pause();
        statistics.resetStatistics();
        initAutomatonPanelAndUpdateDimensions();
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
        if(automaton1DimCurrentRow > 0)
            automaton1DimCurrentRow--;
    }

    void drawCurrentAutomaton() {
        synchronized (AutomatonPanel.LOCKER) {
            int[] pixels = automatonPanel.getPixelsForDrawing();

            if (automaton instanceof Automaton2Dim) {
                Iterator<Cell> cellIterator = automaton.iteratorChangedOnly();
                Cell cell;
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
                    Coords2D coords = (Coords2D) cell.getCoords();
                    pixels[coords.getY() * settings.getWidth() + coords.getX()] = cell.getState().toColor().getRGB();
                }

                if (settings.getSelectedAutomaton() == PossibleAutomaton.Langton) {
                    LangtonAnt langtonAnt = ((LangtonAnt) automaton);
                    for (Ant ant : langtonAnt.getAnts()) {
                        pixels[ant.getCoordinates().getY() * settings.getWidth() + ant.getCoordinates().getX()] = Color.YELLOW.getRGB();
                    }
                }
            } else if (automaton instanceof Automaton1Dim) {
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
                ++automaton1DimCurrentRow;
            }
        }
    }

    void repaint() {
        automatonPanel.repaint();
    }

    private void initAutomatonPanelAndUpdateDimensions() {
        automatonPanel.createBufferedImage(settings.getCellCount());
        settings.setWidth(automatonPanel.getAutomatonWidth());
        settings.setHeight(automatonPanel.getAutomatonHeight());
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

    private void resetAutomatonIfSettingsHasChanged() {
        if (settingsHasChanged) {
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
        initAutomatonPanelAndUpdateDimensions();
    }

    public void setCellCount(int cellCount) {
        settings.setCellCount(cellCount);
        settingsHasChanged = true;
        initAutomatonPanelAndUpdateDimensions();
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
