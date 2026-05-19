package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class Draw extends JPanel {
    DataFromCSV data = new DataFromCSV();
    static final int windowWidth = 5000;
    static final int windowHeight = 5000;
    // === POZYCJA początkowa wykresu ===
    static final int startX = 100;
    static final int startY = 50;
    int height = 10;
    // === SKALA ===
    static final int scale = 20;
    Draw(){
        setPreferredSize(
                new Dimension(
                        windowWidth,
                        windowHeight
                )
        );
        generateStanowiskaLayout();
        addPositions();

    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Ramy(g2);
        Procesy(g2);
    }

    void Ramy(Graphics2D g2){
        // =========================
        // OŚ CZASU
        // =========================
        int day = 0;
        for (int i = 0; i < windowWidth; i++) {
            int x = startX + (i * scale);
            g2.drawLine(
                    x,
                    50,
                    x,
                    windowHeight
            );

            g2.drawString(
                    String.valueOf(i),
                    x-1,
                    45
            );

            if(i % 16 == 0){
                g2.drawString(
                        String.format("Dzien %d", day),
                        x - 10,
                        35
                );
                day++;
                g2.drawLine(
                        x+1,
                        50,
                        x+1,
                        windowHeight
                );
            }
        }
        int x = startX;
        int y = startY;
        for(String key : data.Stanowiska.keySet()) {

            StanowiskaTemplate stanowisko = data.Stanowiska.get(key);

            y += 30;

            int FirstY = y;

            for (int j = 0; j < stanowisko.getIloscStanowisk(); j++) {
                g2.drawLine(
                        x,
                        y,
                        windowWidth,
                        y
                );
                y = y + 20;

                g2.drawLine(
                        x,
                        y,
                        windowWidth,
                        y
                );
            }

            g2.drawString(
                    stanowisko.nazwaStanowsika,
                    x - 45,
                    ((FirstY + y) / 2)
            );

            g2.drawLine(
                    x,
                    FirstY - 1,
                    windowWidth,
                    FirstY - 1
            );

            g2.drawLine(
                    x,
                    y - 1,
                    windowWidth,
                    y - 1
            );
        }
    }
    void addPositions(){

        // ==========================================
        // ITERACJA PO KOLEJCE
        // ==========================================

        for(int currentIndex = 0;
            currentIndex < data.Queue.size();
            currentIndex++) {

            Datatemplate d =
                    data.Queue.get(currentIndex);

            // ======================================
            // SZUKANIE POPRZEDNIEJ OPERACJI
            // ======================================

            Datatemplate previous = null;

            for(int i = currentIndex - 1; i >= 0; i--) {

                Datatemplate prev = data.Queue.get(i);

                if(prev.getPrzedmiot().equals(d.getPrzedmiot()) && prev.repeatID == d.repeatID && prev.getOp() < d.getOp()) {
                    previous = prev;
                    break;
                }
            }

            // ======================================
            // START TECHNOLOGICZNY
            // ======================================

            double baseStartX;

            if(previous == null) {
                baseStartX = startX;
            } else {
                // ==================================
                // POPRZEDNI DŁUŻSZY
                // ==================================
                if(previous.getLenght() > d.getLenght()) {
                    baseStartX = previous.endX - (previous.getLenghtT() * scale) + scale;
                }
                // ==================================
                // AKTUALNY DŁUŻSZY
                // ==================================
                else {
                    baseStartX = previous.startX + (previous.getTpz() * scale) +
                            (previous.getLenghtT() * scale) - (d.getTpz() * scale) + scale;
                }
            }
            // ======================================
            // STANOWISKA JGS
            // ======================================
            StanowiskaTemplate stanowisko = data.Stanowiska.get(d.getJGS());

            int bestStanowisko = -1;
            double bestStart = Double.MAX_VALUE;
            double finalStartX = baseStartX;
            double finalEndX = baseStartX + (d.getLenght() * scale);

            // ======================================
            // ITERACJA PO STANOWISKACH
            // ======================================

            for(int stanowiskoID = 0; stanowiskoID < stanowisko.getIloscStanowisk(); stanowiskoID++) {

                double tempStart = baseStartX;

                double tempEnd = tempStart + (d.getLenght() * scale);

                boolean changed = true;

                // ==================================
                // SZUKANIE MIEJSCA
                // ==================================

                while(changed) {
                    changed = false;
                    for(Datatemplate placed : data.Queue) {
                        if(placed == d || placed.endX == 0 || !placed.getJGS().equals(d.getJGS()) || placed.stanowiskoID != stanowiskoID)
                            continue;
                        boolean overlap = tempStart < placed.endX && tempEnd > placed.startX;

                        if(overlap) {
                            // ==================
                            // PRZESUNIĘCIE
                            // ==================
                            tempStart = placed.endX;

                            tempEnd = tempStart + (d.getLenght() * scale);
                            changed = true;
                            break;
                        }
                    }
                }

                // ==================================
                // WYBÓR LEPSZEGO
                // ==================================

                if(tempStart < bestStart) {

                    bestStart = tempStart;

                    bestStanowisko = stanowiskoID;

                    finalStartX = tempStart;

                    finalEndX = tempEnd;
                }
            }

            // ======================================
            // ZAPIS
            // ======================================

            d.startX = finalStartX;

            d.endX = finalEndX;

            d.stanowiskoID = bestStanowisko;

            d.startY = stanowisko.wymiaryStanowisk[bestStanowisko][0];

            d.endY = stanowisko.wymiaryStanowisk[bestStanowisko][1];
        }
    }
    void Procesy(Graphics2D g2){
        for(Datatemplate d : data.Queue){
            // =====================================
            // KOLOR
            // =====================================
            switch (d.Przedmiot){
                case "Wał z gwintem":
                    g2.setColor(Color.BLUE);
                    break;

                case "Trzepień":
                    g2.setColor(Color.PINK);
                    break;

                case "Wałek z gwinten":
                    g2.setColor(Color.GREEN);
                    break;

                case "Wałek":
                    g2.setColor(Color.YELLOW);
                    break;

                case "Tuleja":
                    g2.setColor(Color.RED);
                    break;

                default:
                    g2.setColor(Color.GRAY);
                    break;
            }

            // =====================================
            // RYSOWANIE PASKA
            // =====================================

            g2.fillRect(
                    (int)d.startX,
                    (int)d.startY + 5,
                    (int)(d.getLenght() * scale),
                    height
            );
            

            // =====================================
            // NUMER OPERACJI
            // =====================================

            g2.setColor(Color.GRAY);
            g2.fillRect(
                    (int)d.startX,
                    (int)d.startY + 5,
                    (int)(d.getTpz() * scale),
                    height
            );

            g2.setColor(Color.BLACK);
            for (int i = 1; i < d.getKt(); i++) {
                g2.drawLine(
                        (int)(d.startX -d.getTpz() + (d.lenghtT * i* scale)),
                        (int)d.startY+5
                        ,
                        (int)(d.startX-d.getTpz() + (d.lenghtT*i* scale)),
                        (int)d.endY-5
                );
            }

        }
    }
    void generateStanowiskaLayout() {
        int y = startY;
        for(String key : data.Stanowiska.keySet()) {
            y += 30;
            StanowiskaTemplate stanowisko = data.Stanowiska.get(key);
            for(int j = 0; j < stanowisko.getIloscStanowisk(); j++) {
                stanowisko.wymiaryStanowisk[j][0] = y;
                y += 20;
                stanowisko.wymiaryStanowisk[j][1] = y;
            }
        }
    }
}
