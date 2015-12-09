package agh.edu.pl.automaton;


public abstract class Automaton1Dim extends Automaton
{
    private int size;

    public Automaton1Dim()
    {
        super(neighborhoodStrategy, stateFactory);
    }
}

