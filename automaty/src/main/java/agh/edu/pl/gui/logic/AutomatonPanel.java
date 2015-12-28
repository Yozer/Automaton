package agh.edu.pl.gui.logic;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;

public class AutomatonPanel extends JPanel {
    static final Object LOCKER = new Object();
    static final AlphaComposite compositeGrid = AlphaComposite.getInstance(AlphaComposite.DST_OUT);

    private BufferedImage bufferedImage, bufferedImageBorder, bufferedImageGrid;
    private int[] pixels;

    private final AffineTransform transformCells = new AffineTransform();
    private AffineTransform transformBorder = new AffineTransform();

    private double previousX;
    private double previousY;
    private double zoomCenterX;
    private double zoomCenterY;

    private final int borderWidth = 1;
    private final Color borderColor = Color.GRAY;

    public AutomatonPanel() {
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setOpaque(true);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                previousX = e.getX();
                previousY = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {

                Point2D adjPreviousPoint = getTranslatedPoint(previousX, previousY);
                Point2D adjNewPoint = getTranslatedPoint(e.getX(), e.getY());

                double newX = adjNewPoint.getX() - adjPreviousPoint.getX();
                double newY = adjNewPoint.getY() - adjPreviousPoint.getY();

                previousX = e.getX();
                previousY = e.getY();

                synchronized (transformCells) {
                    transformCells.translate(newX, newY);
                    transformBorder = (AffineTransform) transformCells.clone();
                    transformBorder.translate(-borderWidth, -borderWidth);
                }

                repaint();
            }
        });
        addMouseWheelListener(e -> {
            if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                Point2D point2D = getTranslatedPoint(e.getPoint().getX(), e.getPoint().getY());
                zoomCenterX = point2D.getX();
                zoomCenterY = point2D.getY();
                handleZoom(e.getPreciseWheelRotation());
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        synchronized (LOCKER) {
            Graphics2D g2d = ((Graphics2D) g);

            synchronized (transformCells) {
                if (bufferedImage != null) {
                    g2d.drawImage(bufferedImageBorder, transformBorder, null);
                    g2d.drawImage(bufferedImage, transformCells, null);
                }
                if(transformCells.getScaleX() >= 2)
                {
                    g2d.setComposite(compositeGrid);
                    AffineTransform tmp = (AffineTransform) transformCells.clone();
                    tmp.scale(0.5, 0.5);
                    g2d.drawImage(bufferedImageGrid, tmp, null);
                }
            }
        }
    }

    int[] getPixelsForDrawing() {
        return pixels;
    }

    private BufferedImage createGrid(int width, int height) {
        width *= 2;
        height *= 2;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(new Color(0, true));
        graphics2D.fillRect(0, 0, width, height);

        graphics2D.setPaint(Color.BLACK);
        graphics2D.setStroke(new BasicStroke(1));

        for (int x = 0; x < width; x += 2) {
            graphics2D.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < height; y += 2) {
            graphics2D.drawLine(0, y, getWidth(), y);
        }

        graphics2D.dispose();
        return image;

    }

    void createBufferedImage(int cellCount) {
        double ratio = ((double) getWidth())/getHeight();
        double heightD = Math.sqrt(cellCount/ratio);
        double widthD = ratio*heightD;

        int width = (int)(widthD + 0.5);
        int height = (int)(heightD + 0.5);

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        bufferedImageBorder = createBorder(width, height);
        bufferedImageGrid = createGrid(width, height);

        synchronized (transformCells) {
            transformCells.setToIdentity();
            transformCells.setToScale(getWidth() / (double)width, getWidth() / (double)width);
            transformBorder = (AffineTransform) transformCells.clone();
            transformBorder.translate(-borderWidth, -borderWidth);
        }
    }

    private BufferedImage createBorder(int width, int height) {
        BufferedImage image = new BufferedImage(width + 2 * borderWidth, height + 2 * borderWidth, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setStroke(new BasicStroke(borderWidth));
        graphics2D.setColor(borderColor);

        graphics2D.drawLine(0, 0, image.getWidth() - 1, 0);
        graphics2D.drawLine(0, 0, 0, image.getHeight() - 1);
        graphics2D.drawLine(0, image.getHeight() - 1, image.getWidth() - 1, image.getHeight() - 1);
        graphics2D.drawLine(image.getWidth() - 1, 0, image.getWidth() - 1, image.getHeight() - 1);
        graphics2D.dispose();
        return image;
    }

    public int getAutomatonWidth() {
        return bufferedImage.getWidth();
    }

    public int getAutomatonHeight() {
        return bufferedImage.getHeight();
    }
    public Point2D getTranslatedPoint(double panelX, double panelY) {

        Point2D point2d = new Point2D.Double(panelX, panelY);
        try {
            return transformCells.inverseTransform(point2d, null);
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
            return point2d;
        }
    }
    private void handleZoom(double zoom) {
        zoom = 0.25 * -zoom;
        zoom += 1;

        synchronized (transformCells) {
            transformCells.translate(zoomCenterX, zoomCenterY);
            transformCells.scale(zoom, zoom);
            transformCells.translate(-zoomCenterX, -zoomCenterY);

            transformBorder = (AffineTransform) transformCells.clone();
            transformBorder.translate(-borderWidth, -borderWidth);
        }

        repaint();
    }
}

