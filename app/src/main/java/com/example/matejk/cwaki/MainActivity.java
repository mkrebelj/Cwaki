package com.example.matejk.cwaki;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity extends Activity {

    final String LOG_TAG = "CwakiMainActivity";
    final int SEZNAM_INTENT = 1, NASTAVITVE_INTENT = 2;
    final static public String SEZNAM_COKOV = "seznam";
    final static  String filenameBackup = "backupfile.txt";
    Button no1,no2,no3,no4,no5,no6,no7,no8,no9,no0,k1,k2,k3,k4,delete,vejca, prikazSeznama,brisiZadnjega, nastavitve;
    TextView dolzina,premer,zadnjiVnos;
    Spinner sorta;
    private String[] sorteArray;
   // private ListView seznamHlodov;
    private ArrayList<String> hlodi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }


    private void init(){

        zadnjiVnos = (TextView) findViewById(R.id.tzadnjiVnos) ;

        sorta = (Spinner) findViewById(R.id.sorta);
        //nafilaj spinner
        naloziShranjeneSorte();


      //  seznamHlodov = (ListView) findViewById(R.id.seznamHlodov);

        hlodi = new ArrayList<String>();




//        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,hlodi);
       // seznamHlodov.setAdapter(adapter2);

//        seznamHlodov.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//                Toast.makeText (getApplication(), "Brisem "+(String) parent.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
//                hlodi.remove(position);
//                adapter2.notifyDataSetChanged();
//            }
//        });

        dolzina = (TextView) findViewById(R.id.dolzina);
        premer = (TextView) findViewById(R.id.premer);

        dolzina.setRawInputType(InputType.TYPE_NULL);
        premer.setRawInputType(InputType.TYPE_NULL);

        no0 = (Button) findViewById(R.id.no0);
        no1 = (Button) findViewById(R.id.no1);
        no2 = (Button) findViewById(R.id.no2);
        no3 = (Button) findViewById(R.id.no3);
        no4 = (Button) findViewById(R.id.no4);
        no5 = (Button) findViewById(R.id.no5);
        no6 = (Button) findViewById(R.id.no6);
        no7 = (Button) findViewById(R.id.no7);
        no8 = (Button) findViewById(R.id.no8);
        no9 = (Button) findViewById(R.id.no9);
        delete = (Button) findViewById(R.id.delete);
        vejca = (Button) findViewById(R.id.vejca);
        prikazSeznama = (Button) findViewById(R.id.printaj);
        brisiZadnjega = (Button) findViewById(R.id.deleteLast);
        nastavitve = (Button) findViewById(R.id.sorteNastavitve);

        k1 = (Button) findViewById(R.id.klasa1);
        k2 = (Button) findViewById(R.id.klasa2);
        k3 = (Button) findViewById(R.id.klasa3);
        k4 = (Button) findViewById(R.id.klasa4);

        dolzina.setBackgroundResource(R.drawable.texthi);
        premer.setBackgroundResource(R.drawable.texthi);

        no0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"0");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"0");
                }
                else return;
            }
        });

        no1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"1");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"1");
                }
                else return;
            }
        });

        no2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"2");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"2");
                }
                else return;
            }
        });

        no3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"3");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"3");
                }
                else return;
            }
        });

        no4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"4");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"4");
                }
                else return;
            }
        });

        no5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"5");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"5");
                }
                else return;
            }
        });

        no6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"6");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"6");
                }
                else return;
            }
        });

        no7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"7");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"7");
                }
                else return;
            }
        });

        no8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"8");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"8");
                }
                else return;
            }
        });

        no9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+"9");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+"9");
                }
                else return;
            }
        });

        vejca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina))
                {
                    dolzina.setText(dolzina.getText()+".");
                }
                else if(getCurrentFocus().equals(premer))
                {
                    premer.setText(premer.getText()+".");
                }
                else return;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentFocus().equals(dolzina) && dolzina.getText().length() > 0)
                {
                    dolzina.setText(dolzina.getText().subSequence(0,dolzina.getText().length()-1));
                }
                else if(getCurrentFocus().equals(premer) && premer.getText().length() > 0)
                {
                    premer.setText(premer.getText().subSequence(0,premer.getText().length()-1));
                }
                else return;
            }
        });




        k1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dodajNaSeznam("I"))
                {
//                    adapter2.notifyDataSetChanged();
                    premer.setText("");
                }

                else return;
            }
        });

        k2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dodajNaSeznam("II"))
                {
//                    adapter2.notifyDataSetChanged();
                    premer.setText("");
                }

                else return;
            }
        });
        k3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dodajNaSeznam("III"))
                {
//                    adapter2.notifyDataSetChanged();
                    premer.setText("");
                }

                else return;
            }
        });

        k4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dodajNaSeznam("IV"))
                {
//                    adapter2.notifyDataSetChanged();
                    premer.setText("");
                }

                else return;
            }
        });


        brisiZadnjega.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(hlodi.size()>0) {
                     hlodi.remove(hlodi.size() - 1);
                     updateZadnjiVnos(hlodi.size() > 0? hlodi.get(hlodi.size()-1):"Seznam hlodovine je prazen");
                     Toast.makeText(getApplicationContext(),"Zadnji vnos je bil odstranjen",Toast.LENGTH_SHORT).show();
                 }
                 else {
                     updateZadnjiVnos("Seznam hlodovine je prazen");
                     Toast.makeText(getApplicationContext(), "Ni vnosov za izbris", Toast.LENGTH_SHORT).show();
                 }
             }
         }
        );
        brisiZadnjega.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Brisanje seznama")
                        .setMessage("Izbrišem vse?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                hlodi.clear();
                                prikazSeznama.setText("Seznam (0)");
                                zadnjiVnos.setText("Seznam je prazen");
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });


        prikazSeznama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //save progress before

                saveToBackupFile();

                Intent intent = new Intent(MainActivity.this, SeznamCokov.class);
                intent.putStringArrayListExtra(SEZNAM_COKOV,hlodi);
                startActivityForResult(intent,SEZNAM_INTENT);

            }
        });

        nastavitve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save progress before
                saveToBackupFile();

                Intent intent = new Intent(MainActivity.this, SorteCene.class);
                startActivityForResult(intent,NASTAVITVE_INTENT);
            }
        });


        //vprasaj za obnovo zadnjega shranjenega seznama, ce se app skrsa
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(hlodi.size()==0) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Obnova seznama")
                            .setMessage("Obnovim zadnji seznam?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    readFromBackupFile();
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
            }
        }, 1000);

        //nastavi dimenzije za vse gumbe "tipkovnice"
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int button_dimension = (int)height/4;
//
//        k1.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        k2.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        k3.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        k4.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no0.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no1.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no2.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no3.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no4.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no5.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no6.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no7.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no8.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        no9.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        delete.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
//        vejca.setLayoutParams(new RelativeLayout.LayoutParams(button_dimension,button_dimension));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (SEZNAM_INTENT) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    hlodi = data.getStringArrayListExtra(SEZNAM_COKOV);
                    updateZadnjiVnos(hlodi.size()>0? hlodi.get(hlodi.size()-1) :"");
                }
                break;
            }
            case (NASTAVITVE_INTENT) : {
                if (resultCode == Activity.RESULT_OK){
                    //sinhroniziraj spinner
                    naloziShranjeneSorte();
                }
            }
        }
    }

    private boolean dodajNaSeznam(String klasa){

        Double r = getR();
        Double l = getL();
        if(sorta.getSelectedItem() != null && r > 0 && l > 0) {
            //da deluje parsanje
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(getResources().getConfiguration().locale);
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("0.00", otherSymbols);
            //DecimalFormat df = new DecimalFormat("0.00");
            String volumen = df.format(Math.PI * Math.pow(r, 2) * l) + "";
            hlodi.add(((String) sorta.getSelectedItem()) + " " + klasa + " " + df.format(l) + " m, " + df.format(r * 2 * 100) + " cm, " + volumen + " m3");

            updateZadnjiVnos("Sorta: "+sorta.getSelectedItem()+ " "+klasa+"\n "+
                    "dolzina: "+df.format(l)+" m, premer:"+df.format(r*2*100)+" cm"+"\n"+
                    "volumen: "+volumen+" m3");

            return true;
        }
        Toast.makeText(getApplicationContext(),"Vnesi vse podatke!",Toast.LENGTH_SHORT).show();
        return false;
    }

    private void updateZadnjiVnos(String vsebina){
        zadnjiVnos.setText(vsebina);
        prikazSeznama.setText("Seznam("+hlodi.size()+")");
    }


    private Double getR()
    {
        if(premer.getText().toString().length() > 0)
            return (Double.parseDouble(premer.getText().toString()) / 2) / 100;
        else
            Toast.makeText(this, "Vnesi premer",Toast.LENGTH_SHORT).show();
        return 0.0;
    }

    private Double getL(){
        if(dolzina.getText().toString().length() > 0){
            return Double.parseDouble(dolzina.getText().toString());
        }
        else
            Toast.makeText(this, "Vnesi dolzino",Toast.LENGTH_SHORT).show();
        return 0.0;
    }


    //branje iz nastavitev
    private boolean naloziShranjeneSorte(){

        Log.d(LOG_TAG, "nakladam shranjene sorte");
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.nastavitve_cwaki),Context.MODE_PRIVATE);
        String trenutneSorte = sharedPref.getString(getString(R.string.saved_sorten), "Smreka,1,2,3,4;");
        //zapis: "SortaIme,cenak1,cenak2,cenak3,cenak4;" - delimiter , in ; za novo vrstico
        String[] sorte = trenutneSorte.split(";");
        String[][] sortePodrobno = new String[sorte.length][5];

        String[] prebraneSorteIme = new String[sorte.length];
        for(int i = 0; i<sorte.length; i++)
        {
            sortePodrobno[i] = sorte[i].split(",");
            prebraneSorteIme[i] = sortePodrobno[i][0];
        }


        sorteArray=prebraneSorteIme;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, sorteArray);
        sorta.setAdapter(adapter);

        return true;

    }

    private void saveToBackupFile()
    {
        String string = "";
        for(String s : hlodi)
        {
            string+=s+";";
        }
        if(string.length()>0)
            string = string.substring(0,string.length()-1);


        FileOutputStream outputStream;
        File file = new File(filenameBackup);
        file.delete(); //clears old data
        try {

            outputStream = openFileOutput(filenameBackup, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFromBackupFile()
    {
        String resultFromFile="";
        try {
            FileInputStream fis = null;
            fis = getApplicationContext().openFileInput(filenameBackup);
            String line;
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();


            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            resultFromFile=sb.toString();

        }catch (Exception e) {
            e.printStackTrace();
        }


        if(resultFromFile.equals(""))
            return;

        //parse and add to hlodi
        hlodi.addAll((Arrays.asList(resultFromFile.split(";"))));
        updateZadnjiVnos(hlodi.get(hlodi.size()-1));
    }



    }
