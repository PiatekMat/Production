package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class Main{

    public static void main(String[] args) {

        JFrame frame = new JFrame("Production Scheduler");

        frame.setSize(1200, 700);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new Draw());

        frame.setVisible(true);
    }
}
