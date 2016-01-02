package agh.edu.pl.gui.structures;

import agh.edu.pl.automaton.automata.langton.AntDirection;
import agh.edu.pl.automaton.Cell;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Struct representation for Langton automaton.
 * @author Dominik Baran
 * @see agh.edu.pl.automaton.automata.langton.Ant
 * @see Cell
 * @see StructureInfo
 */
public class AntStructureInfo extends StructureInfo {

    private final AntDirection state;

    AntStructureInfo(String name, AntDirection state) {
        super(name, 1, 1, null);
        this.state = state;
    }

    /**
     * Not used for single ant
     * @param x not used
     * @param y not used
     * @param rotation not used
     * @return not used
     * @deprecated Not used for single ant
     */
    @Override
    public List<Cell> getCells(int x, int y, double rotation) {
        throw new UnsupportedOperationException("Cannot get cells list for single ant");
    }

    /** {@inheritDoc}
     */
    @Override
    protected void createImage() {
        super.previewImage = new BufferedImage(super.getWidth(), super.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = previewImage.createGraphics();
        g2d.setColor(Color.YELLOW);
        g2d.fillRect(0, 0, super.getWidth(), super.getHeight());
        g2d.dispose();
    }

    /**
     * @return Ant direction for loaded ant
     */
    public AntDirection getState() {
        return state;
    }
}
