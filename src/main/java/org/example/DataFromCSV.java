package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
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

public class DataFromCSV {
    ArrayList<Datatemplate> Dane = new ArrayList<>();
    public Hashtable<String, Integer> Stanowiska = new Hashtable<>();
    DataFromCSV() {
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
