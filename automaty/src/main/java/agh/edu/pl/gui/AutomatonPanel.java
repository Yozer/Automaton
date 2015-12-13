package agh.edu.pl.gui;

import javax.swing.*;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class AutomatonPanel extends JPanel
{
    private BufferedImage bufferedImage;
    private Lock bitmapLocker = new ReentrantLock();
    private int scale;

    public AutomatonPanel()
    {
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setOpaque(true);

        // TODO create bufferedImage, handle screen resize?

    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        lockBitmap();
        Graphics2D g2d = ((Graphics2D) g);
        g2d.scale(scale, scale);
        g2d.drawImage(bufferedImage, 0, 0, null);
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
    public void setScale(int scale)
    {
        lockBitmap();
        this.scale = scale;
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

