package agh.edu.pl.gui.structures;

import agh.edu.pl.automaton.automata.langton.AntState;
import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.states.CellState;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class AntStructureInfo extends StructureInfo {

    private final AntState state;

    AntStructureInfo(String name, AntState state) {
        super(name, 1, 1, null);
        this.state = state;
    }

    @Override
    public List<Cell> getCells(int x, int y, double rotation) {
        throw new UnsupportedOperationException("Cannot get cells list for single ant");
    }
    @Override
    protected void createImage() {
        super.previewImage = new BufferedImage(super.getWidth(), super.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = previewImage.createGraphics();
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(0, 0, super.getWidth(), super.getHeight());
        g2d.dispose();
    }

    public AntState getState() {
        return state;
    }
}
