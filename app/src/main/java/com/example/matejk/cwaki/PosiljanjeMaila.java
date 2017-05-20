package com.example.matejk.cwaki;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PosiljanjeMaila extends AppCompatActivity {

    private static final String PREVZEMNICA = "Prevzemnica", DOBAVNICA="Dobavnica", LIST="Odkupni_List";
    //data
    private ArrayList<String> hlodi;
    //buttons and stuff
    Button posljiDobavnico, posljiPrevzemnico, posljiList;
    EditText lastnikNaziv, lastnikNaslov1, lastnikNaslov2, prevoznikNaziv, prevoznikReg, prevoznikKraj, kupecNaziv, kupecNaslov1, kupecNaslov2, stevilkaDobavnice;
    ScrollView scrollView;


    DecimalFormatSymbols otherSymbols;
    HashMap<String,Double> cenePoSortahKlasah;

    DecimalFormat df;
    private boolean pavsal = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posiljanje_maila);

        otherSymbols = new DecimalFormatSymbols(getResources().getConfiguration().locale);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        df = new DecimalFormat("0.00", otherSymbols);

        cenePoSortahKlasah = new HashMap<>();
        //getdata
        Intent intent = getIntent();
        hlodi = new ArrayList<String>();

        hlodi= intent.getStringArrayListExtra(MainActivity.SEZNAM_COKOV);

        //getviews
        scrollView = (ScrollView) findViewById(R.id.scrollView3M);
        lastnikNaziv = (EditText) findViewById(R.id.lastnikImeM);
        lastnikNaslov1 = (EditText) findViewById(R.id.lastnikNaslovM);
        lastnikNaslov2 = (EditText) findViewById(R.id.lastnikNaslov2M);

        prevoznikNaziv = (EditText) findViewById(R.id.prevoznikNazivM);
        prevoznikReg = (EditText) findViewById(R.id.prevoznikRegM);
        prevoznikKraj = (EditText) findViewById(R.id.prevoznikKrajM);

        kupecNaziv = (EditText) findViewById(R.id.imeStrankeM);
        kupecNaslov1 = (EditText) findViewById(R.id.strankaNaslovM);
        kupecNaslov2 = (EditText) findViewById(R.id.strankaNaslov2M);
        stevilkaDobavnice = (EditText) findViewById(R.id.stevilkaDobavniceM);
        posljiList = (Button) findViewById(R.id.buttonOdkupniListM);
        posljiDobavnico = (Button) findViewById(R.id.buttonDobavnicaM);
        posljiPrevzemnico = (Button) findViewById(R.id.buttonPrevzemnicaM);

        posljiDobavnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndSendXLSFile(ListOfLogs.parseList(hlodi), DOBAVNICA);
            }
        });

        posljiPrevzemnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndSendXLSFile(ListOfLogs.parseList(hlodi), PREVZEMNICA);
            }
        });

        posljiList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(PosiljanjeMaila.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(PosiljanjeMaila.this);
                }
                builder.setTitle("Pavšal")
                        .setMessage("Dodam 8% pavšal?")
                        .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                pavsal = true;
                                createAndSendXLSFile(ListOfLogs.parseList(hlodi), LIST);
                            }
                        })
                        .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                pavsal = false;
                                createAndSendXLSFile(ListOfLogs.parseList(hlodi), LIST);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        naloziShranjenePodatke();
    }

    private void naloziShranjenePodatke(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        lastnikNaziv.setText(sharedPref.getString(getString(R.string.saved_lastnikNaziv), "Matjaz Krebelj"));
        lastnikNaslov1.setText(sharedPref.getString(getString(R.string.saved_lastnikNaslov1), "Gabrije 5"));
        lastnikNaslov2.setText(sharedPref.getString(getString(R.string.saved_lastniklastnikNaslov2), "6250 Ilirska Bistrica"));

        prevoznikNaziv.setText(sharedPref.getString(getString(R.string.saved_prevoznikNaziv), "Esimit doo"));
        prevoznikReg.setText(sharedPref.getString(getString(R.string.saved_prevoznikReg), "KP6707M / 1730NM"));
        prevoznikKraj.setText(sharedPref.getString(getString(R.string.saved_prevoznikKraj), "Sviscaki"));

        stevilkaDobavnice.setText(sharedPref.getString(getString(R.string.saved_stevilkaDobavnice), "1/2017"));
        kupecNaziv.setText(sharedPref.getString(getString(R.string.saved_kupecNaziv), "Lesonit doo"));
        kupecNaslov1.setText(sharedPref.getString(getString(R.string.saved_kupecNaslov1), ""));
        kupecNaslov2.setText(sharedPref.getString(getString(R.string.saved_kupecNaslov2), "6250 Ilirska Bistrica"));


        //sorte
        SharedPreferences sharedPref2 = this.getSharedPreferences(getString(R.string.nastavitve_cwaki),Context.MODE_PRIVATE);
        String trenutneSorte = sharedPref2.getString(getString(R.string.saved_sorten), "Smreka,1,2,3,4;");
        //zapis: "SortaIme,cenak1,cenak2,cenak3,cenak4;" - delimiter , in ; za novo vrstico
        String[] sorte = trenutneSorte.split(";");
        String[][] sortePodrobno = new String[sorte.length][5];

        String[] prebraneSorteIme = new String[sorte.length];
        for(int i = 0; i<sorte.length; i++)
        {
            sortePodrobno[i] = sorte[i].split(",");
            prebraneSorteIme[i] = sortePodrobno[i][0];
            //nastavi cenik
            cenePoSortahKlasah.put(prebraneSorteIme[i]+"-I",Double.parseDouble(sortePodrobno[i][1]));
            cenePoSortahKlasah.put(prebraneSorteIme[i]+"-II",Double.parseDouble(sortePodrobno[i][2]));
            cenePoSortahKlasah.put(prebraneSorteIme[i]+"-III",Double.parseDouble(sortePodrobno[i][3]));
            cenePoSortahKlasah.put(prebraneSorteIme[i]+"-IV",Double.parseDouble(sortePodrobno[i][4]));
        }


    };

    private void shraniVnesenePodatke(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.saved_lastnikNaziv), lastnikNaziv.getText().toString());
        editor.putString(getString(R.string.saved_lastnikNaslov1), lastnikNaslov1.getText().toString());
        editor.putString(getString(R.string.saved_lastniklastnikNaslov2), lastnikNaslov2.getText().toString());
        editor.putString(getString(R.string.saved_prevoznikNaziv), prevoznikNaziv.getText().toString());
        editor.putString(getString(R.string.saved_prevoznikReg), prevoznikReg.getText().toString());
        editor.putString(getString(R.string.saved_prevoznikKraj), prevoznikKraj.getText().toString());
        editor.putString(getString(R.string.saved_kupecNaziv), kupecNaziv.getText().toString());
        editor.putString(getString(R.string.saved_kupecNaslov1), kupecNaslov1.getText().toString());
        editor.putString(getString(R.string.saved_kupecNaslov2), kupecNaslov2.getText().toString());
        editor.putString(getString(R.string.saved_stevilkaDobavnice), stevilkaDobavnice.getText().toString());


        editor.commit();
    }


    private void createAndSendXLSFile(ListOfLogs seznamZaPosiljanje, String vrstaMaila)
    {
        //najprej shrani vnesene podatke
        shraniVnesenePodatke();

        if(isExternalStorageReadable() && isExternalStorageWritable()) {
            Calendar fileCreateTime = new GregorianCalendar();
            fileCreateTime.setTimeInMillis(System.currentTimeMillis());
            String dateTimeString = fileCreateTime.get(Calendar.DAY_OF_MONTH) + "-" +
                    fileCreateTime.get(Calendar.MONTH) + "-" +
                    fileCreateTime.get(Calendar.YEAR) + "-" +
                    fileCreateTime.get(Calendar.HOUR) + ":" +
                    fileCreateTime.get(Calendar.MINUTE);

            System.out.println("Seznamcic ima elementov " + seznamZaPosiljanje.kubiki.size());

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet(vrstaMaila);

            //USTVARI GLAVO
            int rownum = 0;
            HSSFRow row = sheet.createRow((short) rownum++);
            HSSFCell cell = row.createCell(0);

            sheet.addMergedRegion(new CellRangeAddress(
                    0, //first row (0-based)
                    0, //last row (0-based)
                    0, //first column (0-based)
                    8 //last column (0-based)
            ));
            HSSFCellStyle style2 = workbook.createCellStyle();
            style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style2.setVerticalAlignment(
                    HSSFCellStyle.VERTICAL_CENTER);
            cell.setCellValue(vrstaMaila+(vrstaMaila.equals(DOBAVNICA)?"-"+stevilkaDobavnice.getText().toString():""));
            cell.setCellStyle(style2);
            row = sheet.createRow(rownum++);
            row.createCell(0).setCellValue("Datum");
            row.createCell(1).setCellValue(dateTimeString);
            rownum++;


            Map<String, Object[]> data = new HashMap<String, Object[]>();


            switch (vrstaMaila){
                case DOBAVNICA:
                {
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue("Lastnik");
                    row.createCell(6).setCellValue("Stranka");
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(lastnikNaziv.getText().toString());
                    row.createCell(6).setCellValue(kupecNaziv.getText().toString());
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(lastnikNaslov1.getText().toString());
                    row.createCell(6).setCellValue(kupecNaslov1.getText().toString());
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(lastnikNaslov2.getText().toString());
                    row.createCell(6).setCellValue(kupecNaslov2.getText().toString());
                    rownum++;

                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue("Sorta");
                    row.createCell(0).setCellValue("Klasa");
                    row.createCell(0).setCellValue("Dolžina (m)");
                    row.createCell(0).setCellValue("Premer (cm)");
                    row.createCell(0).setCellValue("Volumen (m3)");

                    double skupajKubikov = 0;
                    for (int i = 0; i < seznamZaPosiljanje.kubiki.size(); i++) {
                        data.put((i+1)+ "", new Object[]{i + 1, seznamZaPosiljanje.sorte.get(i), seznamZaPosiljanje.klase.get(i), seznamZaPosiljanje.dolzine.get(i), seznamZaPosiljanje.premeri.get(i), seznamZaPosiljanje.kubiki.get(i)});
                        skupajKubikov += seznamZaPosiljanje.kubiki.get(i);
                    }


                    //napolni XLS
                    Set<String> keyset = data.keySet();

                    for (String key : keyset) {
                        row = sheet.createRow(rownum++);
                        Object[] objArr = data.get(key);
                        int cellnum = 0;
                        for (Object obj : objArr) {
                            cell = row.createCell(cellnum++);
                            if (obj instanceof Date)
                                cell.setCellValue((Date) obj);
                            else if (obj instanceof Boolean)
                                cell.setCellValue((Boolean) obj);
                            else if (obj instanceof String)
                                cell.setCellValue((String) obj);
                            else if (obj instanceof Double)
                                cell.setCellValue((Double) obj);
                        }
                    }
                    row = sheet.createRow(rownum++);
                    row.createCell(4).setCellValue("Skupaj");
                    row.createCell(5).setCellValue(df.format(skupajKubikov));
                }
                    ;break;
                case PREVZEMNICA:
                {
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue("Lastnik");
                    row.createCell(6).setCellValue("Prevoznik");
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(lastnikNaziv.getText().toString());
                    row.createCell(6).setCellValue(prevoznikNaziv.getText().toString());
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(lastnikNaslov1.getText().toString());
                    row.createCell(6).setCellValue(prevoznikReg.getText().toString());
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(lastnikNaslov2.getText().toString());
                    row.createCell(6).setCellValue("Lokacija: "+prevoznikKraj.getText().toString());
                    rownum++;

                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue("Sorta");
                    row.createCell(0).setCellValue("Klasa");
                    row.createCell(0).setCellValue("Dolžina (m)");
                    row.createCell(0).setCellValue("Premer (cm)");
                    row.createCell(0).setCellValue("Volumen (m3)");


                    double skupajKubikov = 0;
                    for (int i = 0; i < seznamZaPosiljanje.kubiki.size(); i++) {
                        data.put((i + 1)+ "", new Object[]{i + 1, seznamZaPosiljanje.sorte.get(i), seznamZaPosiljanje.klase.get(i), seznamZaPosiljanje.dolzine.get(i), seznamZaPosiljanje.premeri.get(i), seznamZaPosiljanje.kubiki.get(i)});
                        skupajKubikov += seznamZaPosiljanje.kubiki.get(i);
                    }


                    //napolni XLS
                    Set<String> keyset = data.keySet();

                    for (String key : keyset) {
                        row = sheet.createRow(rownum++);
                        Object[] objArr = data.get(key);
                        int cellnum = 0;
                        for (Object obj : objArr) {
                            cell = row.createCell(cellnum++);
                            if (obj instanceof Date)
                                cell.setCellValue((Date) obj);
                            else if (obj instanceof Boolean)
                                cell.setCellValue((Boolean) obj);
                            else if (obj instanceof String)
                                cell.setCellValue((String) obj);
                            else if (obj instanceof Double)
                                cell.setCellValue((Double) obj);
                        }
                    }
                    row = sheet.createRow(rownum++);
                    row.createCell(4).setCellValue("Skupaj");
                    row.createCell(5).setCellValue(df.format(skupajKubikov));
                }

                    ;break;
                case LIST:
                {
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue("Lastnik");
                    row.createCell(8).setCellValue("Prevoznik");
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(lastnikNaziv.getText().toString());
                    row.createCell(8).setCellValue(prevoznikNaziv.getText().toString());
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(lastnikNaslov1.getText().toString());
                    row.createCell(8).setCellValue(prevoznikReg.getText().toString());
                    row = sheet.createRow(rownum++);
                    row.createCell(0).setCellValue(lastnikNaslov2.getText().toString());
                    row.createCell(8).setCellValue("Lokacija: "+prevoznikKraj.getText().toString());
                    rownum++;

                    //pripravi podatke po klasah in sortah
                    HashMap<String, Double> kubikiPoSortah = new HashMap<>();
                    String sortaKlasa = "";
                    for(int i = 0; i<seznamZaPosiljanje.sorte.size();i++){
                        sortaKlasa = seznamZaPosiljanje.sorte.get(i)+"-"+seznamZaPosiljanje.klase.get(i);
                        if(kubikiPoSortah.containsKey(sortaKlasa))
                        {
                            //dodaj kubaturo
                            double kubiki = kubikiPoSortah.get(sortaKlasa)+seznamZaPosiljanje.kubiki.get(i);
                            kubikiPoSortah.put(sortaKlasa,kubiki);
                        }
                        else
                            kubikiPoSortah.put(sortaKlasa,seznamZaPosiljanje.kubiki.get(i));

                    }


                    row = sheet.createRow(rownum++);
                    row.createCell(1).setCellValue("Sorta s klaso");
                    row.createCell(2).setCellValue("m3");
                    row.createCell(3).setCellValue("Cena za m3");
                    row.createCell(4).setCellValue("Vrednost");

                    Double koncnaCena = 0.0;
                    Double kubikiSkupaj = 0.0;
                    Double cenaSortaKlasa = 0.0;

                    Iterator it = kubikiPoSortah.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        //dodaj v xml seznamek
                        row = sheet.createRow(rownum++);
                        row.createCell(1).setCellValue((String) pair.getKey());
                        row.createCell(2).setCellValue((Double) pair.getValue());
                        kubikiSkupaj += (Double) pair.getValue();
                        row.createCell(3).setCellValue(cenePoSortahKlasah.get((String) pair.getKey()));
                        cenaSortaKlasa = cenePoSortahKlasah.get((String) pair.getKey())*(Double)pair.getValue();
                        koncnaCena += cenaSortaKlasa;
                        row.createCell(4).setCellValue(df.format(cenaSortaKlasa));
                        System.out.println(pair.getKey() + " = " + pair.getValue());
                        it.remove(); // avoids a ConcurrentModificationException

                }
                    rownum++;
                    row = sheet.createRow(rownum++);
                    row.createCell(1).setCellValue("Skupaj");
                    row.createCell(2).setCellValue(df.format(kubikiSkupaj));
                    row.createCell(4).setCellValue(df.format(koncnaCena));

                    if(pavsal) {
                        rownum++;
                        row = sheet.createRow(rownum++);
                        row.createCell(1).setCellValue("+pavsal 8%=");
                        Double pavsal8posto = koncnaCena*0.08;
                        row.createCell(2).setCellValue(df.format(pavsal8posto));
                        row.createCell(4).setCellValue(df.format(pavsal8posto+koncnaCena));
                    }
                    pavsal = false;
                }
                    break;
                default: Toast.makeText(getBaseContext(),"Napaka neka",Toast.LENGTH_SHORT).show();
                    break;
            }




            File noviXLS = new File(getApplicationContext().getFilesDir(), vrstaMaila+"-" + dateTimeString + ".xls");
            //File noviXLS = new File(getStorageDir("excel"), );
            if(noviXLS.exists()) {
                noviXLS.delete();
                try {
                    noviXLS.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            try {

                FileOutputStream out =
                        new FileOutputStream(noviXLS);
                workbook.write(out);
                out.close();

                System.out.println(noviXLS.getAbsolutePath());
                Toast.makeText(getApplicationContext(), "Excel datoteka kreirana", Toast.LENGTH_SHORT).show();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(noviXLS);
                out = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), vrstaMaila+"-" + dateTimeString + ".xls"));
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
                e.printStackTrace();
            }


            //Send the file
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, vrstaMaila+ dateTimeString);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"m.krebelj@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_TEXT, "V priponki je avtomatsko generirana "+vrstaMaila.toLowerCase());
            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), vrstaMaila+"-" + dateTimeString + ".xls"));
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Izberi poštnega odjemalca"));


        }
        else
            Toast.makeText(getApplicationContext(),"Jok brate, nemozem da pisem ili berem bre",Toast.LENGTH_SHORT).show();
    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
