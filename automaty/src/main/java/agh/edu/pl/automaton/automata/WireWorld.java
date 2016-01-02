package agh.edu.pl.automaton.automata;

import agh.edu.pl.automaton.Automaton2Dim;
import agh.edu.pl.automaton.Cell;
import agh.edu.pl.automaton.cells.neighborhoods.CellNeighborhood;
import agh.edu.pl.automaton.cells.neighborhoods.NeighborhoodList;
import agh.edu.pl.automaton.cells.states.CellState;
import agh.edu.pl.automaton.cells.states.WireElectronState;
import agh.edu.pl.automaton.satefactory.CellStateFactory;

/**
 * Implementation of WireWorld automaton.
 * @author Dominik Baran
 * @see Automaton2Dim
 * @see CellNeighborhood
 * @see javax.naming.spi.StateFactory
 * @see <a href="https://en.wikipedia.org/wiki/Wireworld">WireWorld</a>
 */
public class WireWorld extends Automaton2Dim {

    /**
     * @param width Automaton width
     * @param height Automaton height
     * @param cellNeighborhood Neighborhood for automaton
     * @param cellStateFactory State factory for initial state of each cell in automaton
     */
    public WireWorld(int width, int height, CellStateFactory cellStateFactory, CellNeighborhood cellNeighborhood) {
        super(width, height, cellStateFactory, cellNeighborhood);
    }
    /** {@inheritDoc}
     */
    @Override
    protected CellState nextCellState(Cell cell, NeighborhoodList neighborsStates) {
        WireElectronState state = ((WireElectronState) cell.getState());

        if (state == WireElectronState.ELECTRON_HEAD) {
            return WireElectronState.ELECTRON_TAIL;
        } else if (state == WireElectronState.ELECTRON_TAIL) {
            return WireElectronState.WIRE;
        } else if (state == WireElectronState.WIRE) {
            int headCount = 0;
            for (int i = 0; i < neighborsStates.getLength(); ++i) {
                if (getCellStateByIndex(neighborsStates.get(i)) == WireElectronState.ELECTRON_HEAD)
                    headCount++;
            }
            if (headCount == 1 || headCount == 2)
                return WireElectronState.ELECTRON_HEAD;
        }

        return cell.getState();
    }

    /** {@inheritDoc}
     */
    @Override
    protected boolean cellIsAlive(CellState state) {
        return state == WireElectronState.ELECTRON_HEAD;
    }
    /** {@inheritDoc}
     */
    @Override
    protected boolean cellChangedStateFromDeadToAlive(CellState newState, CellState oldState) {
        return oldState != WireElectronState.ELECTRON_HEAD && newState == WireElectronState.ELECTRON_HEAD;
    }
    /** {@inheritDoc}
     */
    @Override
    protected boolean cellChangedStateFromAliveToDead(CellState newState, CellState oldState) {
        return oldState == WireElectronState.ELECTRON_HEAD && newState != WireElectronState.ELECTRON_HEAD;
    }
}
