package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.awt.*;

/**
 * Represent ant which is used in LangtonAnt automaton
 * @see LangtonAnt
 * @author Dominik Baran
 */
public class Ant extends Cell {
    private final Color antColor;
    private final int automatonWidth;
    private final int automatonHeight;
    private final int id;
    private Coords2D coordinates;

    /**
     *
     * @param coordinates Initial coordinates
     * @param antDirection Initial direction
     * @param antColor Color that ant will be painting alive cells
     * @param automatonWidth Width of automaton in which ant will be traveling
     * @param automatonHeight Height of automaton in which ant will be traveling
     * @param id Unique id for ant
     */
    public Ant(Coords2D coordinates, AntDirection antDirection, Color antColor, int automatonWidth, int automatonHeight, int id) {
        super(antDirection, coordinates);
        this.antColor = antColor;
        this.automatonWidth = automatonWidth;
        this.automatonHeight = automatonHeight;
        this.coordinates = coordinates;
        this.id = id;
    }

    void rotateLeft() {
        AntDirection antDirection = (AntDirection) getState();
        if (antDirection == AntDirection.NORTH)
            antDirection = AntDirection.WEST;
        else if (antDirection == AntDirection.WEST)
            antDirection = AntDirection.SOUTH;
        else if (antDirection == AntDirection.SOUTH)
            antDirection = AntDirection.EAST;
        else if (antDirection == AntDirection.EAST)
            antDirection = AntDirection.NORTH;
        setState(antDirection);
    }

    void rotateRight() {
        AntDirection antDirection = (AntDirection) getState();
        if (antDirection == AntDirection.NORTH)
            antDirection = AntDirection.EAST;
        else if (antDirection == AntDirection.WEST)
            antDirection = AntDirection.NORTH;
        else if (antDirection == AntDirection.SOUTH)
            antDirection = AntDirection.WEST;
        else if (antDirection == AntDirection.EAST)
            antDirection = AntDirection.SOUTH;
        setState(antDirection);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    void move() {
        int x = coordinates.getX();
        int y = coordinates.getY();

        AntDirection antDirection = (AntDirection) getState();

        if (antDirection == AntDirection.NORTH)
            y--;
        else if (antDirection == AntDirection.SOUTH)
            y++;
        else if (antDirection == AntDirection.WEST)
            x--;
        else if (antDirection == AntDirection.EAST)
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

    public AntDirection getAntState() {
        return (AntDirection) getState();
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
        return !(obj == null || getClass() != obj.getClass()) && (obj == this || ((Ant) obj).coordinates.equals(this.coordinates));
    }

    @Override
    public int hashCode() {
        return coordinates.hashCode();
    }
}
