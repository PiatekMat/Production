package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

public class Draw extends JPanel {
    DataFromCSV data = new DataFromCSV();
    // === POZYCJA początkowa wykresu ===
    static final int startX = 100;
    static final int startY = 50;
    int height = 10;
    // === SKALA ===
    static final int scale = 20;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        /*// === DANE OPERACJI ===
        double tpz = 0.2;
        double ntTj = 2.28;
        int kt = 2;

        // === SZEROKOŚCI ===
        int tpzWidth = (int) (tpz * scale);
        int partWidth = (int) (ntTj * scale);

        // =========================
        // TPZ
        // =========================

        g2.setColor(Color.GRAY);

        g2.fillRect(
                startX,
                startY,
                tpzWidth,
                height
        );

        // =========================
        // PARTIE TRANSPORTOWE
        // =========================

        g2.setColor(Color.BLUE);

        for (int i = 0; i < kt; i++) {

            int x = startX + tpzWidth + (i * partWidth);

            g2.fillRect(
                    x,
                    startY,
                    partWidth,
                    height
            );
        }

        // =========================
        // TEKST
        // =========================

        g2.setColor(Color.BLACK);

        g2.drawString(
                "Wał z gwintem",
                20,
                startY + 25
        );*/
        Ramy(g2);

    }

    void Ramy(Graphics2D g2){
        // =========================
        // OŚ CZASU
        // =========================
        int day = 0;
        for (int i = 0; i < 100; i++) {

            int x = startX + (i * scale);
            int y = startY + (i * 16);

            g2.drawLine(
                    x,
                    50,
                    x,
                    1000
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
                        1000
                );
            }
        }
        Enumeration<String> k = data.Stanowiska.keys();
        int x = startX;
        int y = startY;
        while (k.hasMoreElements()) {

            String key = k.nextElement();
            System.out.println(key + " " + data.Stanowiska.get(key));
            y += 20;
            int FirstY = y;
            for (int i = 0; i <= data.Stanowiska.get(key); i++) {
                g2.drawLine(
                        x,
                        y,
                        1500,
                        y
                );
                y = y + 16;
            }
            g2.drawString(
                    key,
                    x-45,
                    ((FirstY + y)/2)
            );
        }
    }
}
