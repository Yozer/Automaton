package agh.edu.pl.gui.logic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class AutomatonPanel extends JPanel {
    static final Object LOCKER = new Object();

    private BufferedImage bufferedImage;
    private int[] pixels;
    BufferedImage bufferedImageBorder;

    private final AffineTransform transformCells = new AffineTransform();
    private AffineTransform transformBorder = new AffineTransform();

    private double previousX;
    private double previousY;
    private double zoomCenterX;
    private double zoomCenterY;

    private final int borderWidth = 1;
    private final Color borderColor = Color.GRAY;
//    private BufferedImage bufferedImageBorder;

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
            }
//            if (bufferedImageBorder != null) {
//                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OUT);
//                g2d.setComposite(ac);
//                //g2d.scale(1 / scale, 1 / scale);
//
//                g2d.drawImage(bufferedImageBorder, 0, 0, null);
//            }
        }
    }

//    void initGrid() {
//        bufferedImageBorder = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        Graphics2D graphics2D = bufferedImageBorder.createGraphics();
//        graphics2D.setColor(new Color(0, true));
//        graphics2D.fillRect(0, 0, getWidth(), getHeight());
//
//        graphics2D.setPaint(Color.BLACK);
//        graphics2D.setStroke(new BasicStroke(1));
//
//        int width = bufferedImageBorder.getWidth();
//        int height = bufferedImageBorder.getHeight();
//
//        for (int x = 1; x < width; x++) {
//            graphics2D.drawLine(x, 0, x, getHeight());
//        }
//        for (int y = 1; y < height; y++) {
//            graphics2D.drawLine(0, y, getWidth(), y);
//        }
//
//        graphics2D.dispose();
//    }

    int[] getPixelsForDrawing() {
        return pixels;
    }

    void createBufferedImage(int cellCount) {
        double ratio = ((double) getWidth())/getHeight();
        double height = Math.sqrt(cellCount/ratio);
        double width = ratio*height;

        bufferedImage = new BufferedImage((int)(width + 0.5), (int)(height + 0.5), BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        bufferedImageBorder = createBorder(height, width);

        synchronized (transformCells) {
            transformCells.setToIdentity();
            transformCells.setToScale(getWidth() / width, getWidth() / width);
            transformBorder = (AffineTransform) transformCells.clone();
            transformBorder.translate(-borderWidth, -borderWidth);
        }

//        initGrid();
    }

    private BufferedImage createBorder(double height, double width) {
        BufferedImage image = new BufferedImage((int) (width + 0.5) + 2 * borderWidth, (int) (height + 0.5) + 2 * borderWidth, BufferedImage.TYPE_INT_RGB);
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

