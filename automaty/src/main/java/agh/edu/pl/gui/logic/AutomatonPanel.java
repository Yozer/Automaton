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
    private static final double MIN_SCALE = 0.01;
    private static final double SHOW_GRID_MIN_SCALE = 2;
    private static final int BORDER_WIDTH = 2;
    private static final Color BORDER_COLOR = Color.GRAY;

    private final AffineTransform transformCells = new AffineTransform();
    private final AffineTransform transformGrid = new AffineTransform();
    private BufferedImage bufferedImage, bufferedImageGrid;
    private int[] pixels;
    private double previousX;
    private double previousY;
    private double zoomCenterX;
    private double zoomCenterY;

    private BufferedImage structurePreview = null;
    private Point2D previewPoint = null;
    private AffineTransform previewTransform = null;
    private double previewRotation = 0;
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
                if (SwingUtilities.isLeftMouseButton(e)) {
                    transformGrid.translate(e.getX() - previousX, e.getY() - previousY);

                    Point2D adjPreviousPoint = getTranslatedPoint(previousX, previousY);
                    Point2D adjNewPoint = getTranslatedPoint(e.getX(), e.getY());

                    double newX = adjNewPoint.getX() - adjPreviousPoint.getX();
                    double newY = adjNewPoint.getY() - adjPreviousPoint.getY();

                    previousX = e.getX();
                    previousY = e.getY();

                    transformCells.translate(newX, newY);
                    calculateGridTranslation();

                    repaint();
                }
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
        if(transformCells.getScaleX() >= SHOW_GRID_MIN_SCALE) {
            double modX = Math.floorMod(round(transformCells.getTranslateX()), cellWidth);
            double modY = Math.floorMod(round(transformCells.getTranslateY()), cellHeight);
            transformGrid.setToTranslation(-cellWidth + modX - 1, -cellHeight + modY - 1);
        }
    }
    private Point2D calculateInsertionPoint(Point2D point) {
        Point2D translatedPoint = getTranslatedPoint(point.getX(), point.getY());
        // jumping
        double x = translatedPoint.getX() % 1;
        double y = translatedPoint.getY() % 1;
        if(x >= 1/2d) {
            x = 1 - x;
        }
        else {
            x = -x;
        }
        if(y >= 1/2d) {
            y = 1 - y;
        }
        else {
            y = -y;
        }

        x += translatedPoint.getX();
        y += translatedPoint.getY();
        return new Point2D.Double(x, y);
    }
    private void calculatePreviewTranslation() {
        Point2D point = calculateInsertionPoint(previewPoint);
        previewTransform = (AffineTransform) transformCells.clone();
        previewTransform.translate(point.getX(), point.getY());
        previewTransform.rotate(previewRotation, 0, 0);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);


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

            synchronized (LOCKER) {
                if (bufferedImage != null) {
                    g2d.drawImage(bufferedImage, transformCells, null);
                }
            }
            // draw struct preview
            if (structurePreview != null) {
                Composite composite = g2d.getComposite();
                g2d.setComposite(compositeStructPreview);
                g2d.drawImage(structurePreview, previewTransform, null);
                g2d.setComposite(composite);

            }
            // draw grid
            if (transformCells.getScaleX() > SHOW_GRID_MIN_SCALE) {
                Composite composite = g2d.getComposite();
                g2d.setComposite(compositeGrid);
                g2d.drawImage(bufferedImageGrid, transformGrid, null);
                g2d.setComposite(composite);
            }
            // draw border for preview
            if(structurePreview != null && Math.abs(transformCells.getScaleX()) >= 0.95) {
                Shape shape = new Rectangle(0, 0, structurePreview.getWidth(), structurePreview.getHeight());
                shape = previewTransform.createTransformedShape(shape);
                g2d.setColor(Color.RED);
                g2d.draw(shape);
            }

            // draw border
            double x = transformCells.getTranslateX();
            double y = transformCells.getTranslateY();

            g2d.setColor(BORDER_COLOR);
            g2d.setStroke(new BasicStroke(BORDER_WIDTH));
            g2d.drawRect(round(x - BORDER_WIDTH), round(y - BORDER_WIDTH),
                    round(getAutomatonWidth() * transformCells.getScaleX() + 2*BORDER_WIDTH),
                    round(getAutomatonHeight() * transformCells.getScaleY() + 2*BORDER_WIDTH));
    }


    int[] getPixelsForDrawing() {
        return pixels;
    }

    private BufferedImage createGrid() {
        if(transformCells.getScaleY() < SHOW_GRID_MIN_SCALE) {
            return null;
        }

        Point2D cellSize = getCellSizeAfterScale();
        cellWidth = round(cellSize.getX());
        cellHeight = round(cellSize.getY());

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

    private Point2D getCellSizeAfterScale() {
        return new Point2D.Double(round(transformCells.getScaleX()), round(transformCells.getScaleY()));

    }

    void createBufferedImage(int cellCount) {
        double ratio = 1;
        double heightD = Math.sqrt(cellCount / ratio);
        double widthD = ratio * heightD;

        int width = round(widthD);
        int height = round(heightD);

        if (bufferedImage != null && bufferedImage.getWidth() == width && bufferedImage.getHeight() == height) {
            // just clear
            clearPlane(Color.BLACK);
            return;
        }

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        previewRotation = 0;

        transformCells.setToIdentity();
        transformGrid.setToIdentity();
        // center plane if needed
        if (width < getWidth())
            transformCells.translate(round((getWidth() - width) / 2f), 0);
        if (height < getHeight())
            transformCells.translate(0, round((getHeight() - height) / 2f));

        bufferedImageGrid = createGrid();
    }

    private void clearPlane(Color color) {
        int black = color.getRGB();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = black;
        }
    }


    public int getAutomatonWidth() {
        return bufferedImage == null ? 0 : bufferedImage.getWidth();
    }

    public int getAutomatonHeight() {
        return bufferedImage == null ? 0 : bufferedImage.getHeight();
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

    private int round(double number) {
        return (int) Math.round(number);
    }

    private void handleZoom(double zoom) {
        final int scaleFactor = 1;
        final double accelerationFactor = 0.05;
        final double scaleZoom = 0.25;
        final double scaleFactorDown = 0.8;

        zoom = scaleZoom * -zoom;
        zoom += 1;

        transformCells.translate(zoomCenterX, zoomCenterY);
        double newScale = transformCells.getScaleX();

        int acceleration = round(newScale * accelerationFactor);
        if (zoom > 1) {
            if(transformCells.getScaleX() > 0.95) {
                newScale = (newScale + scaleFactor + acceleration) / newScale;
            } else {
                newScale = 1/scaleFactorDown;
            }
        }
        else if (zoom < 1) {
            if(transformCells.getScaleX() > 1.05) {
                newScale = (newScale - scaleFactor - acceleration) / newScale;
            }
            else {
                newScale = scaleFactorDown;
            }
        }

        if (transformCells.getScaleX() * newScale >= MIN_SCALE && transformCells.getScaleX() * newScale <= MAX_SCALE) {
            transformCells.scale(newScale, newScale);
        }
        transformCells.translate(-zoomCenterX, -zoomCenterY);

        if (previewTransform != null) {
            calculatePreviewTranslation();
        }

        bufferedImageGrid = createGrid();
        repaint();
    }

    public void setStructurePreview(BufferedImage structurePreview, Point point) {
        previewPoint = point;
        this.structurePreview = structurePreview;
        calculatePreviewTranslation();
        repaint();
    }

    public void disableStructurePreview() {
        this.structurePreview = null;
        this.previewTransform = null;
        repaint();
    }

    private void loopRotation() {
        if(previewRotation < 0) {
            previewRotation = Math.toRadians(270);
        }
        else if(previewRotation >= 2*Math.PI) {
            previewRotation = 0;
        }
        if(structurePreview.getWidth() == 1 && structurePreview.getHeight() == 1) {
            previewRotation = 0;
        }
    }
    public void rotateStructPreviewLeft() {
        previewRotation += Math.toRadians(-90);
        loopRotation();
        calculatePreviewTranslation();
        repaint();
    }

    public void rotateStructPreviewRight() {
        previewRotation += Math.toRadians(90);
        loopRotation();
        calculatePreviewTranslation();
        repaint();
    }

    public double getStructRotation() {
       return previewRotation;
    }

    public Point2D getStructInsertionPoint(Point point) {
        return calculateInsertionPoint(point);
    }
}

