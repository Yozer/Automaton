package agh.edu.pl.automaton;

/**
 * Created by Dominik on 2015-11-25.
 */
public abstract class Automaton
{
    private Map<CellCoordinates, CellState> cells;
    private CellNeighborhood neighborhoodStrategy;
    private CellStateFactory stateFactory;

    public abstract Automaton nextState();
    public abstract void insertStructure(Map<? extends  CellCoordinates, ? extends CellState> structure);
    public abstract CellIterator cellIterator();

    protected abstract Automaton newInstance(CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood);
    protected abstract CellCoordinates initialCoordinates(CellCoordinates coordinates);
    protected abstract CellCoordinates nextCoordinates(CellCoordinates coordinates);
    protected abstract CellSate nextCellState(CellState currentState, Set<Cell> neighborsStates);

    private Set<Cell> mapCoordinates(Set<CellCoordinates> coordinates);
}
