package agh.edu.pl.gui.structures;

import agh.edu.pl.automaton.cells.Cell;
import agh.edu.pl.automaton.cells.coordinates.Coords1D;
import agh.edu.pl.automaton.cells.coordinates.Coords2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dominik on 2015-12-20.
 */
public class StructureInfo {
    private final String name;
    private final int width;
    private final int height;
    private final List<Cell> cells;
    protected BufferedImage previewImage = null;

     StructureInfo(String name, int width, int height, List<Cell> cells) {
         this.name = name;
         this.width = width;
         this.height = height;
         this.cells = cells;
     }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    protected void createImage() {
        previewImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = previewImage.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();

        int[] array = ((DataBufferInt) previewImage.getRaster().getDataBuffer()).getData();
        if(cells.get(0).getCoords() instanceof Coords2D) {
            Coords2D coords2D;

            for(Cell cell : cells) {
                coords2D = (Coords2D) cell.getCoords();
                array[coords2D.getY() * width + coords2D.getX()] = cell.getState().toColor().getRGB();
            }
        } else if(cells.get(0).getCoords() instanceof Coords1D) {
            Coords1D coords1D;

            for(Cell cell : cells) {
                coords1D = (Coords1D) cell.getCoords();
                array[coords1D.getX()] = cell.getState().toColor().getRGB();
            }
        }

    }

    public List<Cell> getCells(int x, int y) {
        List<Cell> result = new ArrayList<>(cells.size());

        if(cells.get(0).getCoords() instanceof Coords2D) {
            Coords2D coords2D;

            for(Cell cell : cells) {
                coords2D = (Coords2D) cell.getCoords();
                coords2D = new Coords2D(coords2D.getX() + x, coords2D.getY() + y);
                result.add(new Cell(cell.getState(), coords2D));
            }
        } else if(cells.get(0).getCoords() instanceof Coords1D) {
            Coords1D coords1D;

            for(Cell cell : cells) {
                coords1D = (Coords1D) cell.getCoords();
                coords1D = new Coords1D(coords1D.getX() + x);
                result.add(new Cell(cell.getState(), coords1D));
            }
        }
        return result;
    }
}
