package agh.edu.pl.gui.logic;

import java.util.concurrent.atomic.AtomicInteger;

public class AutomatonStatistics {
    private final AtomicInteger generationsCount = new AtomicInteger();
    private final AtomicInteger aliveCellsCount = new AtomicInteger();
    private final AtomicInteger totalCellsCount = new AtomicInteger();
    private final AtomicInteger deadCellsCount = new AtomicInteger();
    private final AtomicInteger renderTime = new AtomicInteger();
    private final AtomicInteger generationTime = new AtomicInteger();
    private final AtomicInteger timeOfOnePass = new AtomicInteger();

    void resetStatistics() {
        generationsCount.set(0);
        renderTime.set(0);
        generationTime.set(0);
        aliveCellsCount.set(0);
        deadCellsCount.set(0);
        totalCellsCount.set(0);
        timeOfOnePass.set(0);
    }

    public int getLastSimulationTime() {
        return generationTime.get();
    }

    public int getGenerationsCount() {
        return generationsCount.get();
    }

    public int getAliveCellsCount() {
        return aliveCellsCount.get();
    }

    void setAliveCellsCount(int aliveCellsCount) {
        this.aliveCellsCount.set(aliveCellsCount);
    }

    public int getRenderTime() {
        return renderTime.get();
    }

    void setRenderTime(int renderTime) {
        this.renderTime.set(renderTime);
    }

    public int getDeadCellsCount() {
        return deadCellsCount.get();
    }

    void setDeadCellsCount(int deadCellsCount) {
        this.deadCellsCount.set(deadCellsCount);
    }

    public int getTotalCellsCount() {
        return totalCellsCount.get();
    }

    void setTotalCellsCount(int totalCellsCount) {
        this.totalCellsCount.set(totalCellsCount);
    }

    public int getOnePassTime() {
        return timeOfOnePass.get();
    }

    void setGenerationTime(int generationTime) {
        this.generationTime.set(generationTime);
    }

    void incrementGenerationsCount() {
        generationsCount.incrementAndGet();
    }

    void setTimeOfOnePass(int timeOfOnePass) {
        this.timeOfOnePass.set(timeOfOnePass);
    }
}
