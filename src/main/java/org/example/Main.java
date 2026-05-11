package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

class Datatemplate {

    String JGS;
    String Przedmiot;
    int op;
    double tpz;
    int kt;
    double lenghtT;
    int rprzyj;
    double lenght;
    int repeat;
    //Kordy każdego paska i teraz będziemy mieli szytsko zapisane
    public double startX;
    public double endX;
    public double startY;
    public double endY;

    public Datatemplate(String JGS,
                        String Przedmiot,
                        int op,
                        double tpz,
                        int kt,
                        double lenghtT,
                        int rprzyj,
                        double lenght,
                        int repeat) {

        this.JGS = JGS;
        this.Przedmiot = Przedmiot;
        this.op = op;
        this.tpz = tpz;
        this.kt = kt;
        this.lenghtT = lenghtT;
        this.rprzyj = rprzyj;
        this.lenght = lenght;
        this.repeat = repeat;
    }

    public String getJGS() {
        return JGS;
    }

    public String getPrzedmiot() {
        return Przedmiot;
    }

    public int getOp() {
        return op;
    }

    public double getTpz() {
        return tpz;
    }

    public int getKt() {
        return kt;
    }

    public double getLenghtT() {
        return lenghtT;
    }

    public int getRprzyj() {
        return rprzyj;
    }

    public double getLenght() {
        return lenght;
    }

    public int getRepeat() {
        return repeat;
    }
}

class Dane {
    ArrayList<Datatemplate> Dane = new ArrayList<>();
    public Hashtable<String, Integer> Stanowiska = new Hashtable<>();
    Dane() {
        try {

            BufferedReader br = new BufferedReader(
                    new FileReader("dane.csv")
            );

            String line;

            // pomija nagłówek
            br.readLine();
            String stanowisko = "";
            while((line = br.readLine()) != null) {

                String[] data = line.split(",");
                Datatemplate d = new Datatemplate(
                        data[0],
                        data[1],
                        Integer.parseInt(data[2]),
                        Double.parseDouble(data[3]),
                        Integer.parseInt(data[4]),
                        Double.parseDouble(data[5]),
                        Integer.parseInt(data[6]),
                        Double.parseDouble(data[7]),
                        Integer.parseInt(data[8])
                );
                Dane.add(d);
                if(stanowisko != data[0]){
                    stanowisko = data[0];
                    Stanowiska.put(stanowisko, Integer.parseInt(data[6]));
                }
            }

            br.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

class Rysowanie extends JPanel {
    Dane data = new Dane();
    // === POZYCJA ===
    int startX = 100;
    int startY = 50;
    int height = 10;
    // === SKALA ===
    int scale = 20;
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
                        String.format("Dzień %d", day),
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

public class Main{

    public static void main(String[] args) {

        JFrame frame = new JFrame("Production Scheduler");

        frame.setSize(1200, 700);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new Rysowanie());

        frame.setVisible(true);
    }
}
