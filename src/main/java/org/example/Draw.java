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
    Draw(){
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
        for(String key :
                data.Stanowiska.keySet()) {

            StanowiskaTemplate stanowisko =
                    data.Stanowiska.get(key);

            y += 30;

            int FirstY = y;

            System.out.println(
                    "Row start: " + key
            );

            for (int j = 0;
                 j < stanowisko.getIloscStanowisk();
                 j++) {

                g2.drawLine(
                        x,
                        y,
                        1500,
                        y
                );

                System.out.println(
                        j + ": " + y
                );

                y = y + 20;

                g2.drawLine(
                        x,
                        y,
                        1500,
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
                    1500,
                    FirstY - 1
            );

            g2.drawLine(
                    x,
                    y - 1,
                    1500,
                    y - 1
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
// ==========================================
// addPositions()
// ==========================================

    void addPositions(){

        for(String key :
                data.DaneSortedByJGS.keySet()) {

            ArrayList<Datatemplate> lista =

                    data.DaneSortedByJGS
                            .get(key);

            Datatemplate Previous = null;

            // stanowiska danego JGS
            StanowiskaTemplate stanowisko =

                    data.Stanowiska
                            .get(key);

            for(Datatemplate d : lista) {

                // =====================================
                // WYLICZENIE STARTU
                // =====================================

                double calculatedStartX;

                if(Previous == null) {

                    calculatedStartX = startX;

                } else {

                    // =================================
                    // POPRZEDNI DŁUŻSZY
                    // =================================

                    if(Previous.getLenght()
                            > d.getLenght()) {

                        calculatedStartX =

                                Previous.endX -

                                        (Previous.getLenghtT()
                                                * scale);
                    }

                    // =================================
                    // AKTUALNY DŁUŻSZY
                    // =================================

                    else {

                        calculatedStartX =

                                Previous.startX +

                                        (Previous.getTpz()
                                                * scale) +

                                        (Previous.getLenghtT()
                                                * scale)

                                        -

                                        (d.getTpz()
                                                * scale)

                                        -

                                        (d.getLenghtT()
                                                * scale);
                    }
                }

                double calculatedEndX =

                        calculatedStartX +

                                (d.getLenght() * scale);

                // =====================================
                // SZUKANIE STANOWISKA
                // =====================================

                int bestStanowisko = -1;

                double minEnd = Double.MAX_VALUE;

                boolean znaleziono = false;

                for(int stanowiskoIndex = 0;

                    stanowiskoIndex
                            < stanowisko
                            .getIloscStanowisk();

                    stanowiskoIndex++) {

                    boolean kolizja = false;

                    ArrayList<TimeSlot> slots =

                            stanowisko.stanowiska
                                    .get(stanowiskoIndex);

                    // =================================
                    // SPRAWDZANIE KOLIZJI
                    // =================================

                    for(TimeSlot slot : slots) {

                        boolean collision =

                                calculatedStartX
                                        < slot.endX

                                        &&

                                        calculatedEndX
                                                > slot.startX;

                        if(collision) {

                            kolizja = true;

                            break;
                        }
                    }

                    // =================================
                    // WOLNE STANOWISKO
                    // =================================

                    if(!kolizja) {

                        bestStanowisko =
                                stanowiskoIndex;

                        znaleziono = true;

                        break;
                    }

                    // =================================
                    // ZAPAMIĘTANIE
                    // =================================

                    if(stanowisko
                            .lastEndTime
                            [stanowiskoIndex]

                            < minEnd) {

                        minEnd =

                                stanowisko
                                        .lastEndTime
                                        [stanowiskoIndex];

                        bestStanowisko =
                                stanowiskoIndex;
                    }
                }

                // =====================================
                // JEŚLI BRAK MIEJSCA
                // =====================================

                if(!znaleziono) {

                    calculatedStartX =
                            minEnd + scale;

                    calculatedEndX =

                            calculatedStartX +

                                    (d.getLenght()
                                            * scale);
                }

                // =====================================
                // ZAPIS KOORDYNATÓW
                // =====================================

                d.startX = calculatedStartX;

                d.endX = calculatedEndX;

                d.startY =

                        stanowisko
                                .wymiaryStanowisk
                                [bestStanowisko][0];

                d.endY =

                        stanowisko
                                .wymiaryStanowisk
                                [bestStanowisko][1];

                // =====================================
                // ZAPIS ZAJĘTOŚCI
                // =====================================

                stanowisko.stanowiska
                        .get(bestStanowisko)
                        .add(

                                new TimeSlot(
                                        d.startX,
                                        d.endX
                                )
                        );

                // =====================================
                // AKTUALIZACJA CZASU
                // =====================================

                stanowisko.lastEndTime
                        [bestStanowisko]

                        = d.endX;

                Previous = d;
            }
        }
    }
    void Procesy(Graphics2D g2){
        for(String key : data.DaneSortedByJGS.keySet()){
            ArrayList<Datatemplate> lista =
                    data.DaneSortedByJGS.get(key);
            for (int i = 0; i < lista.size(); i++) {
                System.out.println(i + ". " + lista.get(i).Przedmiot + " " + lista.get(i).startX + " " + lista.get(i).lenght + " " + lista.get(i).startY );
                switch (lista.get(i).Przedmiot){
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

                }
                g2.fillRect(
                        (int)lista.get(i).startX,
                        (int)lista.get(i).startY,
                        (int)lista.get(i).getLenght() *scale,
                        height
                );
            }

        }
    }
    void generateStanowiskaLayout() {

        int y = startY;

        for(String key :
                data.Stanowiska.keySet()) {

            y += 30;

            StanowiskaTemplate stanowisko =
                    data.Stanowiska.get(key);

            for(int j = 0;
                j < stanowisko.getIloscStanowisk();
                j++) {

                stanowisko
                        .wymiaryStanowisk[j][0] = y;

                y += 20;

                stanowisko
                        .wymiaryStanowisk[j][1] = y;
            }
        }
    }
}
