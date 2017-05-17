package com.example.matejk.cwaki;

import java.util.ArrayList;

/**
 * Created by matejk on 15.05.2017.
 */

public class ListOfLogs {

    public ArrayList<String> sorte;
    public ArrayList<String> klase;
    public ArrayList<Double> dolzine;
    public ArrayList<Double> premeri;
    public ArrayList<Double> kubiki;

        public ListOfLogs(){
            this.sorte = new ArrayList<String>();
            this.klase = new ArrayList<String>();
            this.dolzine = new ArrayList<Double>();
            this.premeri = new ArrayList<Double>();
            this.kubiki = new ArrayList<Double>();
        }
        public boolean addLogsFromString(String entry){
            String delimiter = "[ ]+";
            String[] data = entry.split(delimiter);
            for(int i=0; i<data.length; i++)
            {
                System.out.println(i+". "+data[i]);
            }
            sorte.add(data[0]);
            klase.add(data[1]);
            dolzine.add(Double.parseDouble(data[2]));
            premeri.add(Double.parseDouble(data[4]));
            kubiki.add(Double.parseDouble(data[6]));
            return true;
        }




    public static ListOfLogs parseList(ArrayList<String> hlodi){
        ListOfLogs seznamcic= new ListOfLogs();
        for(String vnos : hlodi)
            seznamcic.addLogsFromString(vnos);
        return  seznamcic;
    }
}
