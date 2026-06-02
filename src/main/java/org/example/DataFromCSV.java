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

    // =====================================
    // HARMONOGRAM
    // =====================================

    public double startX;

    public double endX;

    public double startY;

    public double endY;

    public int stanowiskoID;

    public int repeatID;

    public Datatemplate(Datatemplate d){

        this.JGS = d.JGS;
        this.Przedmiot = d.Przedmiot;
        this.op = d.op;
        this.tpz = d.tpz;
        this.kt = d.kt;
        this.lenghtT = d.lenghtT;
        this.rprzyj = d.rprzyj;
        this.lenght = d.lenght;
        this.repeat = d.repeat;

        this.startX = d.startX;
        this.endX = d.endX;
        this.startY = d.startY;
        this.endY = d.endY;

        this.stanowiskoID = d.stanowiskoID;
    }

    Datatemplate(
            String JGS,
            String Przedmiot,
            int op,
            double tpz,
            int kt,
            double lenghtT,
            int rprzyj,
            double lenght,
            int repeat
    ) {

        this.JGS = JGS;

        this.Przedmiot = Przedmiot;

        this.op = op;

        this.tpz = tpz;

        this.kt = kt;

        this.lenghtT = lenghtT;

        this.rprzyj = rprzyj;

        this.lenght = lenght;

        this.repeat = repeat;

        this.repeatID = 0;

        this.stanowiskoID = -1;
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

// =========================================
// STANOWISKA
// =========================================



class StanowiskaTemplate {

    String nazwaStanowsika;

    int iloscStanowisk;

    public int[][] wymiaryStanowisk;

    StanowiskaTemplate(
            String nazwaStanowsika,
            int iloscStanowisk
    ) {

        this.nazwaStanowsika =
                nazwaStanowsika;

        this.iloscStanowisk =
                iloscStanowisk;

        wymiaryStanowisk =
                new int[iloscStanowisk][2];
    }

    public int getIloscStanowisk() {

        return iloscStanowisk;
    }
}

// =========================================
// CSV
// =========================================

public class DataFromCSV {

    public ArrayList<Datatemplate>
            Dane = new ArrayList<>();

    public ArrayList<Datatemplate>
            Queue = new ArrayList<>();

    public Hashtable<String,
            StanowiskaTemplate>
            Stanowiska = new Hashtable<>();

    public Hashtable<String, ArrayList<Datatemplate>> finalQueue = new Hashtable<>();




    public ArrayList<
            ArrayList<Datatemplate>
            > Groups = new ArrayList<>();

    DataFromCSV() {

        try {

            BufferedReader br =
                    new BufferedReader(
                            new FileReader(
                                    "dane.csv"
                            )
                    );

            String line;

            // =================================
            // POMINIĘCIE NAGŁÓWKA
            // =================================

            br.readLine();

            // =================================
            // WCZYTYWANIE
            // =================================

            while((line = br.readLine())
                    != null) {

                String[] data =
                        line.split(",");

                Datatemplate d =
                        new Datatemplate(

                                data[0],

                                data[1],

                                Integer.parseInt(
                                        data[2]
                                ),

                                Double.parseDouble(
                                        data[3]
                                ),

                                Integer.parseInt(
                                        data[4]
                                ),

                                Double.parseDouble(
                                        data[5]
                                ),

                                Integer.parseInt(
                                        data[6]
                                ),

                                Double.parseDouble(
                                        data[7]
                                ),

                                Integer.parseInt(
                                        data[8]
                                )
                        );

                // =============================
                // POMIJANIE ZEROWYCH
                // =============================

                if(d.lenght == 0)
                    continue;

                Dane.add(d);

                // =============================
                // STANOWISKA
                // =============================

                if(!Stanowiska.containsKey(
                        d.JGS
                )) {

                    Stanowiska.put(

                            d.JGS,

                            new StanowiskaTemplate(
                                    d.JGS,
                                    d.rprzyj
                            )
                    );
                }
            }



            // ======================================
// GRUPOWANIE PO PRZEDMIOCIE
// ======================================

            Hashtable<String,
                    ArrayList<Datatemplate>>
                    byPart = new Hashtable<>();

            for(Datatemplate d : Dane){

                if(!byPart.containsKey(
                        d.getPrzedmiot()
                )){

                    byPart.put(
                            d.getPrzedmiot(),
                            new ArrayList<>()
                    );
                }

                byPart.get(
                        d.getPrzedmiot()
                ).add(d);
            }

// ======================================
// SORTOWANIE OPERACJI
// ======================================

            for(String key : byPart.keySet()){

                ArrayList<Datatemplate> list =
                        byPart.get(key);

                // bubble sort po op
                for(int i = 0; i < list.size(); i++){

                    for(int j = i + 1;
                        j < list.size();
                        j++){

                        if(list.get(i).getOp()
                                >
                                list.get(j).getOp()){

                            Datatemplate temp =
                                    list.get(i);

                            list.set(
                                    i,
                                    list.get(j)
                            );

                            list.set(
                                    j,
                                    temp
                            );
                        }
                    }
                }
            }

// ======================================
// MAKSYMALNY REPEAT
// ======================================

            int maxRepeat = 0;

            for(Datatemplate d : Dane){

                if(d.getRepeat() > maxRepeat){

                    maxRepeat = d.getRepeat();
                }
            }

// ======================================
// BUDOWANIE KOLEJKI
// ======================================

            for(int repeatID = 0; repeatID < maxRepeat; repeatID++){
                // operacje po przedmiotach
                for(String key : byPart.keySet()){

                    ArrayList<Datatemplate> list = byPart.get(key);

                    // jeśli repeat istnieje
                    if(repeatID >= list.get(0).getRepeat()){
                        continue;
                    }

                    // dodawanie operacji
                    for(Datatemplate d : list){

                        Datatemplate copy =
                                new Datatemplate(

                                        d.JGS,
                                        d.Przedmiot,
                                        d.op,
                                        d.tpz,
                                        d.kt,
                                        d.lenghtT,
                                        d.rprzyj,
                                        d.lenght,
                                        d.repeat
                                );

                        copy.repeatID = repeatID;

                        Queue.add(copy);
                    }
                }
            }
            for(Datatemplate d : Queue){

                String k =

                        d.getPrzedmiot()

                                + "_"

                                + d.repeatID;

                // ==============================
                // NOWA LISTA
                // ==============================

                if(!finalQueue.containsKey(k)){

                    finalQueue.put(
                            k,
                            new ArrayList<>()
                    );
                }

                // ==============================
                // DODANIE OPERACJI
                // ==============================

                finalQueue.get(k).add(d);
            }


            // =================================
            // DEBUG
            // =================================


            for (String key : finalQueue.keySet()) {
                System.out.println("===========================================================");
                for (Datatemplate d : finalQueue.get(key)){
                    System.out.println(d.getPrzedmiot() + " " + d.getOp() + " " + d.repeatID + " " + d.repeat);
                }

            }
            System.out.println("Groups: " + finalQueue.size());
            br.close();



        } catch(Exception e) {

            e.printStackTrace();
        }
    }
}