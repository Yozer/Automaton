package agh.edu.pl.gui.logic;

import agh.edu.pl.gui.enums.CellNeighborhoodType;
import agh.edu.pl.gui.enums.PossibleAutomaton;
import agh.edu.pl.gui.logic.exceptions.IllegalRulesFormatException;

import java.util.*;
import java.util.stream.Collectors;

public class AutomatonSettings {
    private PossibleAutomaton selectedAutomaton = PossibleAutomaton.GameOfLife;
    private int simulationDelay = 0;
    private Set<Integer> surviveFactors = new HashSet<>(Arrays.asList(2, 3));
    private Set<Integer> comeAliveFactors = new HashSet<>(Collections.singletonList(3));
    private CellNeighborhoodType neighborhoodType = CellNeighborhoodType.Moore;
    private int oneDimRule = 30;
    private int neighborhoodRadius = 1;
    private boolean wrap = true;

    private int width = 400;
    private int height = 300;

    public PossibleAutomaton getSelectedAutomaton() {
        return selectedAutomaton;
    }

    void setSelectedAutomaton(PossibleAutomaton selectedAutomaton) {
        this.selectedAutomaton = selectedAutomaton;
    }

    public int getSimulationDelay() {
        return simulationDelay;
    }

    void setSimulationDelay(int delay) {
        this.simulationDelay = delay;
    }

    public Set<Integer> getSurviveFactors() {
        return surviveFactors;
    }

    public Set<Integer> getComeAliveFactors() {
        return comeAliveFactors;
    }

    public CellNeighborhoodType getNeighborHood() {
        return neighborhoodType;
    }

    public int getOneDimRule() {
        return oneDimRule;
    }

    public void setOneDimRule(Integer oneDimRule) {
        this.oneDimRule = oneDimRule;
    }

    public int getNeighborhoodRadius() {
        return neighborhoodRadius;
    }

    void setNeighborhoodRadius(int neighborhoodRadius) {
        this.neighborhoodRadius = neighborhoodRadius;
    }

    public boolean getWrap() {
        return wrap;
    }

    void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public int getHeight() {
        return height;
    }

    void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    void setWidth(int width) {
        this.width = width;
    }

    void setNeighborhood(CellNeighborhoodType neighborhood) {
        this.neighborhoodType = neighborhood;
    }

    public String getFormattedRules() {
        return surviveFactors.stream().map(t -> Integer.toString(t)).collect(Collectors.joining()) + "/" +
                comeAliveFactors.stream().map(t -> Integer.toString(t)).collect(Collectors.joining());
    }

    void setFormattedRules(String str) throws IllegalRulesFormatException {
        if (str.equals("") ||
                asList(str).stream().filter(t -> t == '/').count() != 1 ||
                asList(str).stream().filter(t -> !Character.isDigit(t) && t != '/').findAny().isPresent())
            throw new IllegalRulesFormatException();

        String splited[] = str.split("/");
        surviveFactors = new HashSet<>(asList(splited[0]).stream().map(Character::getNumericValue).collect(Collectors.toList()));
        comeAliveFactors = new HashSet<>(asList(splited[1]).stream().map(Character::getNumericValue).collect(Collectors.toList()));
    }

    private List<Character> asList(final String string) {
        return new AbstractList<Character>() {
            public int size() {
                return string.length();
            }

            public Character get(int index) {
                return string.charAt(index);
            }
        };
    }
}

