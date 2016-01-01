package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.awt.*;

public class Ant extends Cell {
    private final Color antColor;
    private final int automatonWidth;
    private final int automatonHeight;
    private Coords2D coordinates;
    private int id;

    public Ant(Coords2D coordinates, AntState antState, Color antColor, int automatonWidth, int automatonHeight, int id) {
        super(antState, coordinates);
        this.antColor = antColor;
        this.automatonWidth = automatonWidth;
        this.automatonHeight = automatonHeight;
        this.coordinates = coordinates;
        this.id = id;
    }

    void rotateLeft() {
        AntState antState = (AntState) getState();
        if (antState == AntState.NORTH)
            antState = AntState.WEST;
        else if (antState == AntState.WEST)
            antState = AntState.SOUTH;
        else if (antState == AntState.SOUTH)
            antState = AntState.EAST;
        else if (antState == AntState.EAST)
            antState = AntState.NORTH;
        setState(antState);
    }

    void rotateRight() {
        AntState antState = (AntState) getState();
        if (antState == AntState.NORTH)
            antState = AntState.EAST;
        else if (antState == AntState.WEST)
            antState = AntState.NORTH;
        else if (antState == AntState.SOUTH)
            antState = AntState.WEST;
        else if (antState == AntState.EAST)
            antState = AntState.SOUTH;
        setState(antState);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    void move() {
        int x = coordinates.getX();
        int y = coordinates.getY();

        AntState antState = (AntState) getState();

        if (antState == AntState.NORTH)
            y--;
        else if (antState == AntState.SOUTH)
            y++;
        else if (antState == AntState.WEST)
            x--;
        else if (antState == AntState.EAST)
            x++;

        if (x < 0 || x >= automatonWidth)
            x = Math.floorMod(x, automatonWidth);
        if (y < 0 || y >= automatonHeight)
            y = Math.floorMod(y, automatonHeight);

        coordinates = new Coords2D(x, y);
    }

    public Color getAntColor() {
        return antColor;
    }

    public AntState getAntState() {
        return (AntState) getState();
    }

    public Coords2D getCoordinates() {
        return coordinates;
    }

    public int getId() {
        return id;
    }

    Ant cloneAnt() {
        Coords2D coords2D = new Coords2D(coordinates.getX(), coordinates.getY());
        return new Ant(coords2D, getAntState(), getAntColor(), this.automatonWidth, this.automatonHeight, this.id);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return obj == this || ((Ant) obj).coordinates.equals(this.coordinates);
    }
    @Override
    public int hashCode() {
        return coordinates.hashCode();
    }
}
