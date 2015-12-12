package agh.edu.pl;


import agh.edu.pl.gui.MainWindow;

import java.awt.*;

public class Main
{
    public static void main(String[] args)
    {
        System.setProperty("sun.java2d.opengl","True");
        EventQueue.invokeLater(() -> {
            MainWindow ex = new MainWindow();
            ex.setVisible(true);
        });
    }
}
