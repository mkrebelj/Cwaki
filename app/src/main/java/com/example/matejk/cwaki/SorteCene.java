package com.example.matejk.cwaki;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SorteCene extends AppCompatActivity {

    final String LOG_TAG = "SORTE_cene";
    TextView imeSorte, cenak1, cenak2, cenak3, cenak4;
    Button shrani, brisi;
    Spinner izborSorte;
    static  String [] sorteArray;
    static String[][] sortePodrobno;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorte_cene);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imeSorte = (TextView) findViewById(R.id.imeSorte);
        cenak1 = (TextView) findViewById(R.id.priceK1);
        cenak2 = (TextView) findViewById(R.id.priceK2);
        cenak3 = (TextView) findViewById(R.id.priceK3);
        cenak4 = (TextView) findViewById(R.id.priceK4);

        shrani = (Button) findViewById(R.id.shraniSorto);
        brisi = (Button) findViewById(R.id.deleteBtn);

        sorteArray = new String[]{"Smreka","Jelka","Bor"};

        naloziShranjeneSorte();

        izborSorte = (Spinner) findViewById(R.id.spinnerSorta);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, sorteArray);

        izborSorte.setAdapter(adapter);

        izborSorte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                if(sortePodrobno == null)
                    return;

                if(sortePodrobno[position] != null)
                {


                        imeSorte.setText(sortePodrobno[position][0]);
                        cenak1.setText(sortePodrobno[position][1]);
                        cenak2.setText(sortePodrobno[position][2]);
                        cenak3.setText(sortePodrobno[position][3]);
                        cenak4.setText(sortePodrobno[position][4]);


                }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Napaka pri branju", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shrani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodajNovVnos();
            }
        });

        brisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SorteCene.this)
                        .setTitle("Brisanje vnosa")
                        .setMessage("Izbrišem izbran element?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                izbrisiVnos();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
    }

    private void naloziShranjeneSorte(){
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.nastavitve_cwaki),Context.MODE_PRIVATE);
        String trenutneSorte = sharedPref.getString(getString(R.string.saved_sorten), "Smreka,1,2,3,4;");
        //zapis: "SortaIme,cenak1,cenak2,cenak3,cenak4;" - delimiter , in ; za novo vrstico
        String[] sorte = trenutneSorte.split(";");
        sortePodrobno = new String[sorte.length][5];

        String[] prebraneSorteIme = new String[sorte.length];
        for(int i = 0; i<sorte.length; i++)
        {
            sortePodrobno[i] = sorte[i].split(",");
            prebraneSorteIme[i] = sortePodrobno[i][0];
        }
        //Log.d(LOG_TAG, "vsebina zadnjga = "+prebraneSorteIme[prebraneSorteIme.length-1]);
        sorteArray = prebraneSorteIme;



    }

    private void dodajNovVnos()
    {


        try{
            //najdi in posodobi, ce obstaja
            for(int i = 0; i< sortePodrobno.length; i++){
                if(sortePodrobno[i][0].equals(imeSorte.getText().toString()))
                {
                    //posodobi in zakljuci
                    sortePodrobno[i][1] = cenak1.getText().toString();
                    sortePodrobno[i][2] = cenak2.getText().toString();
                    sortePodrobno[i][3] = cenak3.getText().toString();
                    sortePodrobno[i][4] = cenak4.getText().toString();
                    sinhronizirajTrenutnoStanje();
                    return;
                }
            }

            //dodaj novo
            String[][] noveSortePodrobno = new String[sortePodrobno.length+1][5];
            String[] noveSorteArray = new String[noveSortePodrobno.length];
            for(int i = 0; i < sortePodrobno.length ; i++)
            {
                noveSortePodrobno[i]=sortePodrobno[i];
                noveSorteArray[i]=sortePodrobno[i][0];
            }
            noveSortePodrobno[noveSortePodrobno.length-1][0]=imeSorte.getText().toString();
            noveSortePodrobno[noveSortePodrobno.length-1][1]=cenak1.getText().toString()!=null?cenak1.getText().toString():"0";
            noveSortePodrobno[noveSortePodrobno.length-1][2]=cenak2.getText().toString()!=null?cenak2.getText().toString():"0";
            noveSortePodrobno[noveSortePodrobno.length-1][3]=cenak3.getText().toString()!=null?cenak3.getText().toString():"0";
            noveSortePodrobno[noveSortePodrobno.length-1][4]=cenak4.getText().toString()!=null?cenak4.getText().toString():"0";

            //prevezi sorte
            sortePodrobno = noveSortePodrobno;

            noveSorteArray[noveSortePodrobno.length-1]=noveSortePodrobno[noveSortePodrobno.length-1][0];
            sorteArray = noveSorteArray;

            //update spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_item, sorteArray);
            izborSorte.setAdapter(adapter);


        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Napaka pri vnosu!",Toast.LENGTH_SHORT).show();
        }

        sinhronizirajTrenutnoStanje();
    }

    private void izbrisiVnos(){

        String[][] skrajsaneSorte = new String[sortePodrobno.length-1][5];
        String[] imenaSkrajsana = new String[sortePodrobno.length-1];
        int j = 0;
        for(int i = 0; i < sortePodrobno.length ; i++)
        {
            if(!sortePodrobno[i][0].equals(imeSorte.getText().toString()))
            {
               //dodas vse razen tega
                if(j<=skrajsaneSorte.length) {
                    skrajsaneSorte[j] = sortePodrobno[i];
                    imenaSkrajsana[j] = sortePodrobno[i][0];
                }
                else {

                    Toast.makeText(getApplicationContext(), "Ne najdem elementa za brisanje", Toast.LENGTH_SHORT).show();
                    return;
                }
                j++;
            }
        }

        //prevezemo sorte
        sortePodrobno=skrajsaneSorte;
        sorteArray=imenaSkrajsana;
        //update spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, sorteArray);
        izborSorte.setAdapter(adapter);

        sinhronizirajTrenutnoStanje();
    }

    private void sinhronizirajTrenutnoStanje(){
        //shranjevanje v nastavitve

        String trenutnoStanje = new String();
        for(int i=0; i < sortePodrobno.length;i++){
            for(int j = 0; j < sortePodrobno[i].length; j++)
            {
                trenutnoStanje+=sortePodrobno[i][j]+",";
            }
            trenutnoStanje=trenutnoStanje.substring(0,trenutnoStanje.length()-1);
            trenutnoStanje+=";";
        }

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.nastavitve_cwaki),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_sorten), trenutnoStanje);
        Log.d(LOG_TAG,"Shranil sem"+trenutnoStanje);
        editor.commit();

    }


}
