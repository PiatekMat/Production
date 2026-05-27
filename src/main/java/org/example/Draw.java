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
        System.out.println(generujPermutacjeArrayList());
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
        int st_cr = 0;
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
                g2.drawString(
                        java.lang.String.valueOf(++st_cr),
                        x - scale,
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
    void addPositions(ArrayList<ArrayList<Datatemplate>> Queue){

        // ==========================================
        // ITERACJA PO KOLEJCE
        // ==========================================

        for(int currentIndex = 0; currentIndex < Queue.size(); currentIndex++) {

            ArrayList<Datatemplate> SingleQueue = Queue.get(currentIndex);
            for(int i = 0; i < SingleQueue.size(); i++){
                Datatemplate Current = SingleQueue.get(i);
                Datatemplate previous;
                if(i - 1 < 0){
                    previous = null;
                }else{
                    previous = SingleQueue.get(i-1);
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
                    if(previous.getLenght() > Current.getLenght()) {
                        baseStartX = previous.endX - (previous.getLenghtT() * scale) + scale;
                    }
                    // ==================================
                    // AKTUALNY DŁUŻSZY
                    // ==================================
                    else {
                        baseStartX = previous.startX + (previous.getTpz() * scale) + (previous.getLenghtT() * scale) - (Current.getTpz() * scale) + scale;
                    }
                }

                // ======================================
                // STANOWISKA JGS
                // ======================================

                StanowiskaTemplate stanowisko = data.Stanowiska.get(Current.getJGS());

                int bestStanowisko = -1;

                double bestStart = Double.MAX_VALUE;

                double finalStartX = baseStartX;

                double finalEndX = baseStartX + (Current.getLenght() * scale);

                // ======================================
                // ITERACJA PO STANOWISKACH
                // ======================================

                for(int stanowiskoID = 0; stanowiskoID < stanowisko.getIloscStanowisk(); stanowiskoID++) {
                    double tempStart = baseStartX;
                    double tempEnd = tempStart + (Current.getLenght() * scale);
                    boolean changed = true;
                    // ==================================
                    // SZUKANIE MIEJSCA
                    // ==================================

                    while(changed) {
                        changed = false;
                        for(Datatemplate placed : data.Queue) {
                            if(placed == Current || placed.endX == 0 || !placed.getJGS().equals(Current.getJGS()) || placed.stanowiskoID != stanowiskoID)
                                continue;

                            boolean overlap = tempStart < placed.endX && tempEnd > placed.startX;

                            if(overlap) {

                                // ==================
                                // PRZESUNIĘCIE
                                    // ==================

                                tempStart = placed.endX;
                                tempEnd = tempStart + (Current.getLenght() * scale);
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

                Current.startX = finalStartX;

                Current.endX = finalEndX;

                Current.stanowiskoID = bestStanowisko;

                Current.startY = stanowisko
                            .wymiaryStanowisk
                            [bestStanowisko][0];

                Current.endY = stanowisko.wymiaryStanowisk[bestStanowisko][1];
            }
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
            g2.drawString(
                    String.valueOf(d.getOp()),
                    (int)d.startX,
                    (int)d.startY+10
            );
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
    Double JSGLenght(ArrayList<Datatemplate> stanowisko){
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double lenght = 0;
        for (Datatemplate d : stanowisko){
            if(d.startX < minX){
                minX = d.startX;
            }
            if(d.endX > maxX){
                maxX = d.endX;
            }
        }
        lenght = maxX - minX;
        return lenght;
    }
    // TODO: Zrobić sprawdzanie, czy mieści sie w gnieździe
    boolean testAllLenght(ArrayList<ArrayList<Datatemplate>> lists, int maxRythm){
        boolean fitIn = false;

        ArrayList<Double> len = new ArrayList<>();
        for (ArrayList<Datatemplate> Single)
        for(String key : data.finalQueue.keySet()){
            len.add(JSGLenght(data.finalQueue.get(key)));
        }

        for(Double d : len){
            System.out.println(d);
        }
        return fitIn;
    }
    public ArrayList<ArrayList<ArrayList<Datatemplate>>> generujPermutacjeArrayList() {
        // Pobieramy same ArrayList z Hashtable
        ArrayList<ArrayList<Datatemplate>> lists = new ArrayList<>(data.finalQueue.values());
        ArrayList<ArrayList<ArrayList<Datatemplate>>> wynik = new ArrayList<>();
        permutuj(lists, 0, wynik);
        return wynik;
    }
    // TODO: Dorobić wywalenie algorytmu jak znajdzie
    private void permutuj(ArrayList<ArrayList<Datatemplate>> lists, int index, ArrayList<ArrayList<ArrayList<Datatemplate>>> wynik) {

        if (index == lists.size()) {
            addPositions(lists);
            return;
        }
        for (int i = index; i < lists.size(); i++) {
            zamien(lists, index, i);
            permutuj(lists, index + 1, wynik);
            // Backtracking
            zamien(lists, index, i);
        }
    }
    private void zamien(ArrayList<ArrayList<Datatemplate>> lists, int i, int j) {
        ArrayList<Datatemplate> temp = lists.get(i);
        lists.set(i, lists.get(j));
        lists.set(j, temp);
    }

}
