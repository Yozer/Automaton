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

    private final AffineTransform tx = new AffineTransform();

    private double previousX;
    private double previousY;
    private double zoomCenterX;
    private double zoomCenterY;
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

                synchronized (tx) {
                    tx.translate(newX, newY);
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

            synchronized (tx) {
                if (bufferedImage != null) {

                        g2d.drawImage(bufferedImage, tx, null);

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

        synchronized (tx) {
            tx.setToIdentity();
        }

        bufferedImage = new BufferedImage((int)(width + 0.5), (int)(height + 0.5), BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        tx.setToScale(getWidth()/width, getWidth()/width);

//        initGrid();
    }

    public int getAutomatonWidth() {
        return bufferedImage.getWidth();
    }

    public int getAutomatonHeight() {
        return bufferedImage.getHeight();
    }
    private Point2D getTranslatedPoint(double panelX, double panelY) {

        Point2D point2d = new Point2D.Double(panelX, panelY);
        try {
            return tx.inverseTransform(point2d, null);
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public void handleZoom(double zoom) {
        zoom = 0.25 * -zoom;
        zoom += 1;

        synchronized (tx) {
            tx.translate(zoomCenterX, zoomCenterY);
            tx.scale(zoom, zoom);
            tx.translate(-zoomCenterX, -zoomCenterY);
        }

        repaint();
    }
}

