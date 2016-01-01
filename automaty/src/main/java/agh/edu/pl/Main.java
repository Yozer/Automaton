package agh.edu.pl;

import agh.edu.pl.gui.MainWindow;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        System.setProperty("sun.java2d.opengl", "true");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        EventQueue.invokeLater(() -> {
            MainWindow ex = new MainWindow();
            ex.setVisible(true);
        });
    }
}
