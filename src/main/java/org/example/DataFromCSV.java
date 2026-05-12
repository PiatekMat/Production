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
class TimeSlot {

    public double startX;
    public double endX;

    TimeSlot(double startX, double endX) {

        this.startX = startX;
        this.endX = endX;
    }
}
class StanowiskaTemplate{
    String nazwaStanowsika;
    int iloscStanowisk;
    public int[][] wymiaryStanowisk;
    public ArrayList<ArrayList<TimeSlot>> stanowiska;
    public double[] lastEndTime;
    Hashtable<Integer, ArrayList<Integer>> zajecieStanowisk = new Hashtable<>();
    StanowiskaTemplate(String nazwaStanowsika, int iloscStanowisk){
        this.nazwaStanowsika = nazwaStanowsika;
        this.iloscStanowisk = iloscStanowisk;
        stanowiska = new ArrayList<>();

        for(int i = 0; i < iloscStanowisk; i++) {

            stanowiska.add(
                    new ArrayList<>()
            );
        }
        wymiaryStanowisk =
                new int[iloscStanowisk][2];
        lastEndTime = new double[iloscStanowisk];
    }

    public int getIloscStanowisk() {
        return iloscStanowisk;
    }
}

public class DataFromCSV {
    static final int startX = 100;
    static final int startY = 50;
    static final int scale = 20;
    ArrayList<Datatemplate> Dane = new ArrayList<>();
    // Potrzebuje y stanowisk i x gdzie te stanowiką są zajęte, czyli najlepiej nazwa stanowiska - hash table ilość stanowsik - y - zajajęte x na tych stanowiskach
    //stanowsika.get("klucz").get(0) - ilość
    public ArrayList<StanowiskaTemplate> Stanowiska = new ArrayList<>();
    public Hashtable<String, ArrayList<Datatemplate>> DaneSortedByPrzedmiot = new Hashtable<>();

    DataFromCSV() {
        try {

            BufferedReader br = new BufferedReader(
                    new FileReader("dane.csv")
            );

            String line;

            // pomija nagłówek
            br.readLine();
            String stanowisko = "";int test = 0;
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
                if(d.lenght == 0)
                    continue;
                Dane.add(d);

                if(!stanowisko.equals(data[0])){
                    stanowisko = data[0];
                    Stanowiska.add(new StanowiskaTemplate(stanowisko, Integer.parseInt(data[6])));
                    test++;
                    //System.out.println(test);
                }

                if(DaneSortedByPrzedmiot.containsKey(data[1])){
                    DaneSortedByPrzedmiot.get(data[1]).add(d);
                }else{
                    DaneSortedByPrzedmiot.put(data[1], new ArrayList<Datatemplate>());
                    DaneSortedByPrzedmiot.get(data[1]).add(d);
                }

            }
            br.close();
            for(StanowiskaTemplate t : Stanowiska){
                //System.out.println(t.wymiaryStanowisk.length);
            }
            System.out.println();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
