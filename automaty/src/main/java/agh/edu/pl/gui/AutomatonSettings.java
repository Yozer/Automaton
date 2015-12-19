package agh.edu.pl.gui;

import agh.edu.pl.gui.enums.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AutomatonSettings
{
    private float cellSize = 5;
    private PossibleAutomaton selectedAutomaton = PossibleAutomaton.GameOfLive;
    private int simulationDelay = 0;
    private Set<Integer> surviveFactors = new HashSet<>(Arrays.asList(2, 3));
    private Set<Integer> comeAliveFactors= new HashSet<>(Arrays.asList(3));
    private CellNeighborhoodType neighborhoodType = CellNeighborhoodType.Moore;
    private int oneDimRule;
    private int neighborhoodRadius = 1;
    private boolean wrap = true;

    private int width;
    private int height;

    public PossibleAutomaton getSelectedAutomaton()
    {
        return selectedAutomaton;
    }

    public void setSelectedAutomaton(PossibleAutomaton selectedAutomaton)
    {
        this.selectedAutomaton = selectedAutomaton;
    }

    public float getCellSize()
    {
        return cellSize;
    }

    public void setCellSize(int cellSize)
    {
        this.cellSize = cellSize;
    }

    public int getSimulationDelay()
    {
        return simulationDelay;
    }
    public void setSimulationDelay(int delay)
    {
        this.simulationDelay = delay;
    }

    public Set<Integer> getSurviveFactors()
    {
        return surviveFactors;
    }

    public Set<Integer> getComeAliveFactors()
    {
        return comeAliveFactors;
    }


    public CellNeighborhoodType getNeighborHood()
    {
        return neighborhoodType;
    }

    public int getOneDimRule()
    {
        return oneDimRule;
    }

    public int getNeighborhoodRadius()
    {
        return neighborhoodRadius;
    }

    public boolean getWrap()
    {
        return wrap;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }
}
