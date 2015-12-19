package agh.edu.pl.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AutomatonPanel extends JPanel
{
    private BufferedImage bufferedImage;
    private BufferedImage bufferedImageBorder;
    private Lock bitmapLocker = new ReentrantLock();
    private float scale;

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

        lockBitmap();
        Graphics2D g2d = ((Graphics2D) g);
        g2d.scale(scale, scale);
        g2d.drawImage(bufferedImage, 0, 0, null);
        if(bufferedImageBorder != null)
        {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OUT);
            g2d.setComposite(ac);
            g2d.scale(1 / scale, 1 / scale);
            g2d.drawImage(bufferedImageBorder, 0, 0, null);
        }
        unlockBitmap();
    }

    private void lockBitmap()
    {
        bitmapLocker.lock();
    }
    private void unlockBitmap()
    {
        bitmapLocker.unlock();
    }
    public void setScale(float scale)
    {
        lockBitmap();
        this.scale = scale;
        // create border if cellsize is greater or equal 4
        if(scale >= 2)
        {
            //scale = 3;
            bufferedImageBorder = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = bufferedImageBorder.createGraphics();
            graphics2D.setColor(new Color(0, true));
            graphics2D.fillRect(0, 0, getWidth(), getHeight());

            graphics2D.setPaint(Color.BLACK);
            graphics2D.setStroke(new BasicStroke(1));

            int width = (int) (getWidth() / scale);
            int height = (int)(getHeight() / scale);
            int scaleInt = (int) scale;

            for(int x = 1; x < width; x++)
            {
                graphics2D.drawLine(scaleInt * x, 0, scaleInt *x, getHeight());
            }
            for(int y = 1; y < height; y++)
            {
                graphics2D.drawLine(0, scaleInt * y, getWidth(), scaleInt * y);
            }

            graphics2D.dispose();
        }
        else
        {
            bufferedImageBorder = null;
        }
        unlockBitmap();
    }
    public BufferedImage getBitmapForDrawing()
    {
        lockBitmap();
        return bufferedImage;
    }
    public void releaseBitmapAfterDrawing()
    {
        unlockBitmap();
    }

    public void createBufferedImage(int width, int height)
    {
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }
}

