package agh.edu.pl;


import agh.edu.pl.gui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class Main
{
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException
    {
        byte[] array = new byte[2000000];
        Random random = new Random();
        long count = 0;

        for(int i = 0;  i < array.length; i++)
        {
            array[i] = (byte)random.nextInt(255);
            if(array[i] > 125)
                count++;
        }

        System.out.println(count);
        long before = System.nanoTime();
        Arrays.fill(array, (byte) 0);
        long after = System.nanoTime();
        System.out.println((after - before)/1000000f);

        count = 0;
        for(int i = 0;  i < array.length; i++)
        {
            array[i] = (byte)random.nextInt(255);
            if(array[i] > 125)
                count++;
        }
        System.out.println(count);
        return;
        /*System.setProperty("sun.java2d.opengl","True");
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        EventQueue.invokeLater(() -> {
            MainWindow ex = new MainWindow();
            ex.setVisible(true);
        });*/
    }
}
