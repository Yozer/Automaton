package agh.edu.pl.gui;

import javax.swing.*;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class AutomatonPanel extends JPanel
{
    private BufferedImage bufferedImage;
    private int[] pixels;
    private BufferedImage bufferedImageBorder;
    private float scale = 1.0f;

    public static final Object LOCKER = new Object();

    public AutomatonPanel()
    {
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setOpaque(true);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        synchronized (LOCKER)
        {
            Graphics2D g2d = ((Graphics2D) g);
            g2d.scale(scale, scale);

            if (bufferedImage != null)
            {
                g2d.drawImage(bufferedImage, 0, 0, null);
            }
            if (bufferedImageBorder != null)
            {
                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OUT);
                g2d.setComposite(ac);
                g2d.scale(1 / scale, 1 / scale);

                g2d.drawImage(bufferedImageBorder, 0, 0, null);
            }
        }
    }

    public void setScale(float scale)
    {
        if(scale == this.scale)
            return;

        this.scale = scale;
        // create border if cellsize is greater or equal 4

        if (scale >= 2)
        {
            //scale = 3;
            bufferedImageBorder = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = bufferedImageBorder.createGraphics();
            graphics2D.setColor(new Color(0, true));
            graphics2D.fillRect(0, 0, getWidth(), getHeight());

            graphics2D.setPaint(Color.BLACK);
            graphics2D.setStroke(new BasicStroke(1));

            int width = (int) (getWidth() / scale);
            int height = (int) (getHeight() / scale);
            int scaleInt = (int) scale;

            for (int x = 1; x < width; x++)
            {
                graphics2D.drawLine(scaleInt * x, 0, scaleInt * x, getHeight());
            }
            for (int y = 1; y < height; y++)
            {
                graphics2D.drawLine(0, scaleInt * y, getWidth(), scaleInt * y);
            }

            graphics2D.dispose();
        } else
        {
            bufferedImageBorder = null;
        }
    }

    public int[] getPixelsForDrawing()
    {
        return pixels;
    }
    public void createBufferedImage(int width, int height)
    {
        if (bufferedImage != null && bufferedImage.getWidth() == width && bufferedImage.getHeight() == height)
            return;

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
    }
}

