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
    private static final AlphaComposite compositeGrid = AlphaComposite.getInstance(AlphaComposite.DST_OUT);
    private static final AlphaComposite compositeStructPreview = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

    private static final int MAX_SCALE = 500;
    private static final double MIN_SCALE = 0.95;
    private final int borderWidth = 1;
    private final Color borderColor = Color.GRAY;
    private final AffineTransform transformCells = new AffineTransform();
    private final AffineTransform transformGrid = new AffineTransform();
    private BufferedImage bufferedImage, bufferedImageBorder, bufferedImageGrid;
    private int[] pixels;
    private AffineTransform transformBorder = new AffineTransform();
    private double previousX;
    private double previousY;
    private double zoomCenterX;
    private double zoomCenterY;

    private BufferedImage structurePreview = null;
    private Point2D previewPoint = null;
    private AffineTransform previewTransform = null;
    private int cellWidth;
    private int cellHeight;

    public AutomatonPanel() {
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setOpaque(false);

        initListeners();
    }

    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    previousX = e.getX();
                    previousY = e.getY();
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                transformGrid.translate(e.getX() - previousX, e.getY() - previousY);

                Point2D adjPreviousPoint = getTranslatedPoint(previousX, previousY);
                Point2D adjNewPoint = getTranslatedPoint(e.getX(), e.getY());

                double newX = adjNewPoint.getX() - adjPreviousPoint.getX();
                double newY = adjNewPoint.getY() - adjPreviousPoint.getY();

                previousX = e.getX();
                previousY = e.getY();

                transformCells.translate(newX, newY);
                transformBorder = (AffineTransform) transformCells.clone();
                transformBorder.translate(-borderWidth, -borderWidth);

                calculateGridTranslation();

                repaint();
            }
        });
        addMouseWheelListener(e -> {
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                Point2D point2D = getTranslatedPoint(e.getPoint().getX(), e.getPoint().getY());
                zoomCenterX = point2D.getX();
                zoomCenterY = point2D.getY();
                handleZoom(e.getPreciseWheelRotation());
            }
        });

        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                if(bufferedImage != null) {
                    bufferedImageGrid = createGrid();
                    repaint();
                }
                else {
                    // init plane with default settings
                    createBufferedImage(new AutomatonSettings().getCellCount());
                    repaint();
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    private void calculateGridTranslation() {
        double modX = Math.floorMod((int) (transformCells.getTranslateX() + 0.5), cellWidth);
        double modY = Math.floorMod((int) (transformCells.getTranslateY() + 0.5), cellHeight);
        transformGrid.setToTranslation(-cellWidth + modX - 1, -cellHeight + modY - 1);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        synchronized (LOCKER) {
            Graphics2D g2d = ((Graphics2D) g);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                    RenderingHints.VALUE_COLOR_RENDER_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
                    RenderingHints.VALUE_DITHER_DISABLE);
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            if (bufferedImage != null) {
                g2d.drawImage(bufferedImage, transformCells, null);
            }
            if (transformCells.getScaleX() > 2) {
                Composite composite = g2d.getComposite();
                g2d.setComposite(compositeGrid);
                g2d.drawImage(bufferedImageGrid, transformGrid, null);
                g2d.setComposite(composite);
            }
            if (structurePreview != null) {
                Composite composite = g2d.getComposite();
                g2d.setComposite(compositeStructPreview);

                // draw border
                if(previewTransform.getScaleX() >= 2)
                {
                    double x = previewTransform.getTranslateX();
                    double y = previewTransform.getTranslateY();

                    g2d.setColor(Color.RED);
                    g2d.drawRect((int)(x - 1 + 0.5), (int)(y - 1 + 0.5),
                            (int) (structurePreview.getWidth() * previewTransform.getScaleX() + 2.5),
                            (int) (structurePreview.getHeight() * previewTransform.getScaleY() + 2.5));
                }

                g2d.drawImage(structurePreview, previewTransform, null);
                g2d.setComposite(composite);
            }
            g2d.drawImage(bufferedImageBorder, transformBorder, null);
        }
    }

    int[] getPixelsForDrawing() {
        return pixels;
    }

    private BufferedImage createGrid() {
        Point2D cellSize = getPixelSizeAfterScale();
        cellWidth = (int) (cellSize.getX() + 0.5);
        cellHeight = (int) (cellSize.getY() + 0.5);

        BufferedImage image = new BufferedImage(getWidth() + 2 * (cellWidth), getHeight() + 2 * (cellHeight), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(new Color(0, true));
        graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());

        graphics2D.setPaint(Color.YELLOW);
        graphics2D.setStroke(new BasicStroke(1));

        for (int x = 0; x < image.getWidth(); x += cellWidth) {
            graphics2D.drawLine(x, 0, x, image.getHeight());
        }
        for (int y = 0; y < image.getHeight(); y += cellHeight) {
            graphics2D.drawLine(0, y, image.getWidth(), y);
        }
        transformGrid.setToIdentity();
        calculateGridTranslation();

        graphics2D.dispose();
        return image;
    }

    private Point2D getPixelSizeAfterScale() {
        return new Point2D.Double((int) (transformCells.getScaleX() + 0.5), (int) (transformCells.getScaleY() + 0.5));

    }

    void createBufferedImage(int cellCount) {
        double ratio = 1;
        double heightD = Math.sqrt(cellCount / ratio);
        double widthD = ratio * heightD;

        int width = (int) (widthD + 0.5);
        int height = (int) (heightD + 0.5);

        if (bufferedImage != null && bufferedImage.getWidth() == width && bufferedImage.getHeight() == height) {
            // just clear
            clearPlane(Color.BLACK);
            return;
        }

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        transformCells.setToIdentity();
        // center plane if needed
        if (width < getWidth())
            transformCells.translate((int) ((getWidth() - width) / 2f + 0.5), 0);
        if (height < getHeight())
            transformCells.translate(0, (int) ((getHeight() - height) / 2f + 0.5));

        transformGrid.setToIdentity();
        transformBorder = (AffineTransform) transformCells.clone();
        transformBorder.translate(-borderWidth, -borderWidth);

        bufferedImageBorder = createBorder(width, height);
        bufferedImageGrid = createGrid();
    }

    private void clearPlane(Color color) {
        int black = color.getRGB();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = black;
        }
    }

    private BufferedImage createBorder(int width, int height) {
        BufferedImage image = new BufferedImage(width + 2 * borderWidth, height + 2 * borderWidth, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(new Color(0, true));
        graphics2D.fillRect(0, 0, image.getWidth(), image.getHeight());

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

    public Point2D getTranslatedPoint(double x, double y) {

        return getTranslatedPoint(x, y, transformCells);
    }

    private Point2D getTranslatedPoint(double x, double y, AffineTransform matrix) {

        Point2D point2d = new Point2D.Double(x, y);
        try {
            return matrix.inverseTransform(point2d, null);
        } catch (NoninvertibleTransformException ex) {
            ex.printStackTrace();
            return point2d;
        }
    }

    private void handleZoom(double zoom) {
        zoom = 0.25 * -zoom;
        zoom += 1;

        transformCells.translate(zoomCenterX, zoomCenterY);
        double newScale = transformCells.getScaleX();

        int acceleration = (int) (newScale * 0.05 + 0.5);
        if (zoom > 1)
            newScale = (newScale + 1 + acceleration) / newScale;
        else if (zoom < 1)
            newScale = (newScale - 1 - acceleration) / newScale;

        if (transformCells.getScaleX() * newScale >= MIN_SCALE && transformCells.getScaleX() * newScale <= MAX_SCALE)
            transformCells.scale(newScale, newScale);
        transformCells.translate(-zoomCenterX, -zoomCenterY);

        if (previewTransform != null) {
            previewTransform = (AffineTransform) transformCells.clone();
            Point2D translatedPoint = getTranslatedPoint(previewPoint.getX(), previewPoint.getY());
            previewTransform.translate(translatedPoint.getX(), translatedPoint.getY());
        }

        transformBorder = (AffineTransform) transformCells.clone();
        transformBorder.translate(-borderWidth, -borderWidth);
        bufferedImageGrid = createGrid();

        repaint();
    }

    public void setStructurePreview(BufferedImage structurePreview, Point point) {
        previewTransform = (AffineTransform) transformCells.clone();
        previewPoint = point;

        Point2D translatedPoint = getTranslatedPoint(point.getX(), point.getY());
        previewTransform.translate(translatedPoint.getX(), translatedPoint.getY());
        this.structurePreview = structurePreview;
        repaint();
    }

    public void disableStructurePreview() {
        this.structurePreview = null;
        this.previewTransform = null;
        repaint();
    }
}

