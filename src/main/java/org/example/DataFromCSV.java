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
    public Datatemplate(){
        this.JGS = null;
        this.Przedmiot = null;
        this.op = 0;
        this.tpz = 0;
        this.kt = 0;
        this.lenghtT = 0;
        this.rprzyj = 0;
        this.lenght = 0;
        this.repeat = 0;
    }
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
    static final int startX = 100;
    static final int startY = 50;
    static final int scale = 20;
    ArrayList<Datatemplate> Dane = new ArrayList<>();
    public Hashtable<String, Integer> Stanowiska = new Hashtable<>();
    public Hashtable<String, ArrayList<Datatemplate>> DaneSortedByPrzedmiot = new Hashtable<>();

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

                if(DaneSortedByPrzedmiot.containsKey(data[1])){
                    DaneSortedByPrzedmiot.get(data[1]).add(d);
                }else{
                    DaneSortedByPrzedmiot.put(data[1], new ArrayList<Datatemplate>());
                    DaneSortedByPrzedmiot.get(data[1]).add(d);
                }
            }

            br.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    void addPositions(){
        for(String key : DaneSortedByPrzedmiot.keySet()){
            ArrayList<Datatemplate> lista = DaneSortedByPrzedmiot.get(key);
            Datatemplate Previous = new Datatemplate();
            for(Datatemplate d : lista) {
                if(Previous.endX == 0){
                    d.startX = startX;
                }else{
                    if(Previous.getLenght() > d.getLenght()){
                        d.startX = Previous.endX - (Previous.getLenghtT() * scale);
                    } else if (Previous.getLenght() < d.getLenght()) {
                        d.startX = Previous.startX + Previous.tpz;
                    }

                }
                d.endX = d.startX + (scale * d.lenght);
                Previous.endX = d.endX;

            }
        }
    }
}
