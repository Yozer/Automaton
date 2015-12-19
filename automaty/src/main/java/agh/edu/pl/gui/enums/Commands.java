package agh.edu.pl.gui.enums;

/**
 * Created by Dominik on 2015-12-19.
 */
public enum Commands
{
    CHANGE_AUTOMATON("CHANGE_AUTOMATON"),
    CHANGE_CELL_SIZE("CHANGE_CELL_SIZE"),
    START_AUTOMATON("START_AUTOMATON"),
    PAUSE_AUTOMATON("PAUSE_AUTOMATON"),
    RAND_CELLS("RAND_CELLS"),
    CHANGE_SIMULATION_DELAY("CHANGE_SIMULATION_DELAY"),
    INIT("INIT"),
    INSERT_PRIME("INSERT_PRIME");

    private final String text;

    Commands(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
