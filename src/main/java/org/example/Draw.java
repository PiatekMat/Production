package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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
        int x = startX;
        int y = startY;
        for (int i = 0; i < data.Stanowiska.size(); i++) {
            y += 30;
            int FirstY = y;
            System.out.println("Row start: " + i);
            for (int j = 0; j < data.Stanowiska.get(i).getIloscStanowisk(); j++) {
                g2.drawLine(
                        x,
                        y,
                        1500,
                        y
                );
                System.out.println(j +": " + y);
                data.Stanowiska.get(i).wymiaryStanowisk[j][0] = y;
                y = y + 20;
                g2.drawLine(
                        x,
                        y,
                        1500,
                        y
                );
                System.out.println(y);
                data.Stanowiska.get(i).wymiaryStanowisk[j][1] = y;
            }
            g2.drawString(
                    data.Stanowiska.get(i).nazwaStanowsika,
                    x-45,
                    ((FirstY + y)/2)
            );
            g2.drawLine(
                    x,
                    FirstY -1,
                    1500,
                    FirstY -1
            );
            g2.drawLine(
                    x,
                    y -1,
                    1500,
                    y -1
            );
            System.out.println("Row End");
        }
//        while (k.hasMoreElements()) {
//
//            String key = k.nextElement();
//            System.out.println(key + " " + data.Stanowiska.get(key));
//            y += 20;
//            int FirstY = y;
//            for (int i = 0; i <= data.Stanowiska.get(key); i++) {
//                g2.drawLine(
//                        x,
//                        y,
//                        1500,
//                        y
//                );
//                y = y + 16;
//            }
//            g2.drawString(
//                    key,
//                    x-45,
//                    ((FirstY + y)/2)
//            );
//        }
    }
    void addPositions(){
        int idStanowiska = 0;
        for(String key : data.DaneSortedByPrzedmiot.keySet()) {

            ArrayList<Datatemplate> lista =
                    data.DaneSortedByPrzedmiot.get(key);

            Datatemplate Previous = null;

            StanowiskaTemplate stanowisko =
                    data.Stanowiska.get(idStanowiska);
            idStanowiska++;
            for(Datatemplate d : lista) {

                // =========================================
                // WYLICZENIE STARTU
                // =========================================

                double calculatedStartX;

                if(Previous == null) {

                    calculatedStartX = startX;

                } else {

                    // =====================================
                    // POPRZEDNI DŁUŻSZY
                    // =====================================

                    if(Previous.getLenght() > d.getLenght()) {

                        calculatedStartX =

                                Previous.endX -

                                        (Previous.getLenghtT() * scale);

                    }

                    // =====================================
                    // AKTUALNY DŁUŻSZY
                    // =====================================

                    else {

                        calculatedStartX =

                                Previous.startX +

                                        (Previous.getTpz() * scale) +

                                        (Previous.getLenghtT() * scale)

                                        -

                                        (d.getTpz() * scale)

                                        -

                                        (d.getLenghtT() * scale);
                    }
                }

                double calculatedEndX =

                        calculatedStartX +

                                (d.getLenght() * scale);

                // =========================================
                // SZUKANIE STANOWISKA
                // =========================================

                int bestStanowisko = -1;

                double minEnd = Double.MAX_VALUE;

                boolean znaleziono = false;

                for(int stanowiskoIndex = 0;
                    stanowiskoIndex < stanowisko.getIloscStanowisk();
                    stanowiskoIndex++) {

                    boolean kolizja = false;

                    ArrayList<TimeSlot> slots =

                            stanowisko.stanowiska
                                    .get(stanowiskoIndex);

                    // =====================================
                    // SPRAWDZANIE KOLIZJI
                    // =====================================

                    for(TimeSlot slot : slots) {

                        boolean collision =

                                calculatedStartX < slot.endX &&
                                        calculatedEndX > slot.startX;

                        if(collision) {

                            kolizja = true;

                            break;
                        }
                    }

                    // =====================================
                    // WOLNE STANOWISKO
                    // =====================================

                    if(!kolizja) {

                        bestStanowisko = stanowiskoIndex;

                        znaleziono = true;

                        break;
                    }

                    // =====================================
                    // ZAPAMIĘTANIE NAJKRÓTSZEGO
                    // =====================================

                    if(stanowisko.lastEndTime[stanowiskoIndex]
                            < minEnd) {

                        minEnd =
                                stanowisko.lastEndTime
                                        [stanowiskoIndex];

                        bestStanowisko = stanowiskoIndex;
                    }
                }

                // =========================================
                // JEŚLI NIE MA MIEJSCA
                // =========================================

                if(!znaleziono) {

                    calculatedStartX =

                            minEnd + scale;

                    calculatedEndX =

                            calculatedStartX +

                                    (d.getLenght() * scale);
                }

                // =========================================
                // ZAPIS KOORDYNATÓW
                // =========================================

                d.startX = calculatedStartX;

                d.endX = calculatedEndX;

                d.startY =

                        startY +

                                (bestStanowisko * 80);

                d.endY = d.startY + 30;

                // =========================================
                // ZAPIS ZAJĘTOŚCI
                // =========================================

                stanowisko.stanowiska
                        .get(bestStanowisko)
                        .add(
                                new TimeSlot(
                                        d.startX,
                                        d.endX
                                )
                        );

                // =========================================
                // AKTUALIZACJA CZASU KOŃCA
                // =========================================

                stanowisko.lastEndTime[bestStanowisko] =
                        d.endX;

                Previous = d;
            }
        }
    }
    void Procesy(Graphics2D g2){

    }
}
