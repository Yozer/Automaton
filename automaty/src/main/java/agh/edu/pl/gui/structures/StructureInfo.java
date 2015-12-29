package agh.edu.pl.gui.structures;

import agh.edu.pl.automaton.cells.Cell;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Created by Dominik on 2015-12-20.
 */
public class StructureInfo {
    private final String name;
    private final int width;
    private final int height;
    private final String path;
    private final List<Cell> cells;
    private BufferedImage previewImage = null;

    public StructureInfo(String name, int width, int height, String path, List<Cell> cells) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.path = path;
        this.cells = cells;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return name;
    }

    public BufferedImage getPreviewImage()
    {
        if(previewImage == null)
            createImage();
        return previewImage;
    }

    private void createImage() {
        previewImage = new BufferedImage(70, 70, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = previewImage.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, 70, 70);
        g2d.dispose();
    }
}
