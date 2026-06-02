package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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


    AtomicBoolean found = new AtomicBoolean(false);
    AtomicLong checked = new AtomicLong(0);
    ArrayList<ArrayList<Datatemplate>> Queue = new ArrayList<>();

    Draw(){
        setPreferredSize(
                new Dimension(
                        windowWidth,
                        windowHeight
                )
        );
        generateStanowiskaLayout();
        startMultithread();
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
        for(ArrayList<Datatemplate> group : Queue){

            for(Datatemplate d : group){

                d.startX = 0;
                d.endX = 0;
                d.startY = 0;
                d.endY = 0;
                d.stanowiskoID = -1;
            }
        }

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
                        for(ArrayList<Datatemplate> group : Queue) {
                            for(Datatemplate placed : group){
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
        for(ArrayList<Datatemplate> SingleQueue : Queue){
            for(Datatemplate d : SingleQueue){
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
                            (int)(d.startX + d.getTpz() + (d.lenghtT * i* scale)),
                            (int)d.startY+5
                            ,
                            (int)(d.startX + d.getTpz() + (d.lenghtT*i* scale)),
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

    double bestWorst = Double.MAX_VALUE;
    boolean testAllLenght(ArrayList<ArrayList<Datatemplate>> lists, double maxRythm){

        double worst = 0;
        String worstJGS = "";

        Hashtable<String, Double> minStart =
                new Hashtable<>();

        Hashtable<String, Double> maxEnd =
                new Hashtable<>();

        boolean valid = true;

        for(ArrayList<Datatemplate> group : lists){

            for(Datatemplate d : group){

                String jgs = d.getJGS();

                if(!minStart.containsKey(jgs)){
                    minStart.put(jgs, d.startX);
                    maxEnd.put(jgs, d.endX);
                }

                if(d.startX < minStart.get(jgs)){
                    minStart.put(jgs, d.startX);
                }

                if(d.endX > maxEnd.get(jgs)){
                    maxEnd.put(jgs, d.endX);
                }
            }
        }

        for(String jgs : minStart.keySet()){

            double lenght =
                    (maxEnd.get(jgs) - minStart.get(jgs))
                            / scale;

            //System.out.println(
                    //jgs + " -> " + lenght + "h"
            //);

            if(lenght > worst){
                worst = lenght;
                worstJGS = jgs;

            }
            if(lenght > maxRythm){
                valid =  false;
            }
        }
        if(worst < bestWorst){

            bestWorst = worst;

            System.out.println(
                    "NOWY REKORD: "
                            + bestWorst + " JGS: " + worstJGS
            );
            for(String jgs : minStart.keySet()){

                double lenght =
                        (maxEnd.get(jgs) - minStart.get(jgs))
                                / scale;

                System.out.println(
                        jgs + " -> " + lenght
                );
            }
        }
        return valid;
    }
    ArrayList<ArrayList<Datatemplate>> deepCopy(
            ArrayList<ArrayList<Datatemplate>> src){

        ArrayList<ArrayList<Datatemplate>>
                copy =
                new ArrayList<>();

        for(ArrayList<Datatemplate> group : src){

            ArrayList<Datatemplate>
                    newGroup =
                    new ArrayList<>();

            for(Datatemplate d : group){

                newGroup.add(
                        new Datatemplate(d)
                );
            }

            copy.add(newGroup);
        }

        return copy;
    }
    void startMultithread(){

        ArrayList<ArrayList<Datatemplate>>
                lists =
                new ArrayList<>(
                        data.finalQueue.values()
                );

        int threads =
                Runtime.getRuntime()
                        .availableProcessors();

        System.out.println(
                "THREADS: "
                        + threads
        );

        ExecutorService pool =
                Executors.newFixedThreadPool(
                        threads
                );

        for(int i = 0;
            i < lists.size();
            i++){

            int fixed = i;

            pool.submit(() -> {

                ArrayList<
                        ArrayList<Datatemplate>
                        > local =

                        deepCopy(lists);

                zamien(
                        local,
                        0,
                        fixed
                );

                permutuj(
                        local,
                        1,
                        new ArrayList<>()
                );
            });
        }

        pool.shutdown();

        try{

            pool.awaitTermination(
                    Long.MAX_VALUE,
                    TimeUnit.DAYS
            );

        }catch(Exception e){

            e.printStackTrace();
        }
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
        if(found.get()){
            return;
        }
        addPositions(lists);
        if (index == lists.size()) {

            long current = checked.incrementAndGet();

            if(current % 10000 == 0){
                System.out.println(
                        "Checked: " + current
                );
            }

            if(testAllLenght(lists, 42)){
                found.set(true);
                Queue = lists;
                return;
            }
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
