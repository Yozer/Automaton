package agh.edu.pl.automaton.automata.langton;

import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.awt.*;

public class Ant implements Cloneable
{
    private static int ID_COUNTER = 0;

    private int id;
    private Coords2D coordinates;
    private AntState antState;
    private Color antColor;

    private int automatonWidth, automatonHeight;

    Ant(Coords2D coordinates, AntState antState, Color antColor, int automatonWidth, int automatonHeight)
    {
        this(antState, antColor, automatonWidth, automatonHeight);
        this.coordinates = coordinates;
        id = ID_COUNTER++;
    }
    private Ant(AntState antState, Color antColor, int automatonWidth, int automatonHeight)
    {
        this.antState = antState;
        this.antColor = antColor;
        this.automatonWidth = automatonWidth;
        this.automatonHeight = automatonHeight;
    }

    void rotateLeft()
    {
        if(antState == AntState.NORTH)
            antState = AntState.WEST;
        else if(antState == AntState.WEST)
            antState = AntState.SOUTH;
        else if(antState == AntState.SOUTH)
            antState = AntState.EAST;
        else if(antState == AntState.EAST)
            antState = AntState.NORTH;
    }
    void rotateRight()
    {
        if(antState == AntState.NORTH)
            antState = AntState.EAST;
        else if(antState == AntState.WEST)
            antState = AntState.NORTH;
        else if(antState == AntState.SOUTH)
            antState = AntState.WEST;
        else if(antState == AntState.EAST)
            antState = AntState.SOUTH;
    }
    void move()
    {
        int x = coordinates.getX();
        int y = coordinates.getY();

        if(antState == AntState.NORTH)
            y--;
        else if(antState == AntState.SOUTH)
            y++;
        else if(antState == AntState.WEST)
            x--;
        else if(antState == AntState.EAST)
            x++;

        if(x < 0 || x >= automatonWidth)
            x = Math.floorMod(x, automatonWidth);
        if(y < 0 || y >= automatonHeight)
            y = Math.floorMod(y, automatonHeight);

        coordinates = new Coords2D(x, y);
    }

    public Color getAntColor()
    {
        return antColor;
    }
    public AntState getAntState()
    {
        return antState;
    }
    public Coords2D getCoordinates()
    {
        return coordinates;
    }

    public int getId()
    {
        return id;
    }
    @Override
    public Ant clone()
    {
        Ant ant = new Ant(this.getAntState(), this.getAntColor(), this.automatonWidth, this.automatonHeight);
        ant.coordinates = this.getCoordinates();
        ant.id = this.getId();
        return ant;
    }
}
