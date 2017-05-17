package com.example.matejk.cwaki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.ActionBar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SeznamCokov extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    final int PRINT_INTENT = 1;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final String LOG_TAG="SeznamCokov";
    private static final int PRINTANJE_INTENT = 55;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    //moje


    private ListView seznamHlodov;
    private ArrayList<String> hlodi;
    private Integer lastClickedIndex;
    private Button posljiMailDobavnico;
    private Button printanje;
    private ArrayAdapter<String> adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        verifyBTPermissions(this);

        setContentView(R.layout.activity_seznam_cokov);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mVisible = true;

        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });


        init();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void init(){
        Intent intent = getIntent();
        lastClickedIndex = -1;


        seznamHlodov = (ListView) findViewById(R.id.koncniSeznam);

        hlodi = new ArrayList<String>();
        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(MainActivity.SEZNAM_COKOV)) {
                hlodi=intent.getStringArrayListExtra(MainActivity.SEZNAM_COKOV);

            }
        }



        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,hlodi);
        seznamHlodov.setAdapter(adapter2);

        seznamHlodov.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (lastClickedIndex == position){
                    Toast.makeText (getApplication(), "Brisem "+(String) parent.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
                    hlodi.remove(position);
                    adapter2.notifyDataSetChanged();
                    lastClickedIndex = -1;
                }
                else
                {
                    Toast.makeText (getApplication(), "Za brisanje klikni ponovno na isti element",Toast.LENGTH_SHORT).show();
                    lastClickedIndex = position;
                }
            }
        });

        posljiMailDobavnico = (Button) findViewById(R.id.posljiMail);
        posljiMailDobavnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //naredi pdf al nekaj, z glavo vred in prikazSeznama

                createAndSendXLSFile(ListOfLogs.parseList(hlodi));
            }
        });

        printanje = (Button) findViewById(R.id.printanjeButton);
        printanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //printaj

                Intent intent = new Intent(SeznamCokov.this, Printanje.class);

                //podatki po seznamih
                intent.putStringArrayListExtra(MainActivity.SEZNAM_COKOV,hlodi);
                Log.d(LOG_TAG,"Šaljem elementov stevilo"+hlodi.size());
                startActivityForResult(intent,PRINTANJE_INTENT);


                //printanje na klasicne printere
                //printajDobavnico();
            }
        });

    }



    private void createAndSendXLSFile(ListOfLogs seznamZaPosiljanje)
    {
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
            HSSFSheet sheet = workbook.createSheet("Dobavnica");

            Map<String, Object[]> data = new HashMap<String, Object[]>();


            data.put("1", new Object[]{"Dobavnica", dateTimeString});
            data.put("2", new Object[]{" ","Sorta", "Klasa", "Dolžina (m)", "Premer (cm)", "Volumen (m3)"});

            for (int i = 0; i < seznamZaPosiljanje.kubiki.size(); i++) {
                data.put((i + 3)+ "", new Object[]{i + 1, seznamZaPosiljanje.sorte.get(i), seznamZaPosiljanje.klase.get(i), seznamZaPosiljanje.dolzine.get(i), seznamZaPosiljanje.premeri.get(i), seznamZaPosiljanje.kubiki.get(i)});
            }

            Set<String> keyset = data.keySet();
            int rownum = 0;
            for (String key : keyset) {
                Row row = sheet.createRow(rownum++);
                Object[] objArr = data.get(key);
                int cellnum = 0;
                for (Object obj : objArr) {
                    Cell cell = row.createCell(cellnum++);
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


            File noviXLS = new File(getApplicationContext().getFilesDir(), "dobavnica-" + dateTimeString + ".xls");
            //File noviXLS = new File(getStorageDir("excel"), );

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
                out = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "dobavnica-" + dateTimeString + ".xls"));
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
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Dobavnica "+ dateTimeString);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"m.krebelj@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_TEXT, "V priponki je avtomatsko generirana dobavnica");
            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "dobavnica-" + dateTimeString + ".xls"));
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

    public File getStorageDir(String dirName) {


        File f = new File(Environment.getExternalStorageDirectory(), dirName);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_BT = 2;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static void verifyBTPermissions(Activity activity) {
        // Check if we have bt
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_BLUETOOTH,
                    MY_PERMISSIONS_REQUEST_BT
            );
        }
    }


    //za printanje
    private WebView mWebView;
    private void printajDobavnico(){
        // Create a WebView object specifically for printing
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(LOG_TAG, "page finished loading " + url);
                createWebPrintJob(view);
                mWebView = null;
            }
        });

        // Generate an HTML document on the fly:
        String htmlDocument = "<html><body><h1>Dobavnica</h1>";

        for (String h : hlodi){
            htmlDocument = htmlDocument+" <p>" +h+"</p>";
        }
        htmlDocument = htmlDocument+"</body></html>";

        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }

    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

        // Save the job object for later status checking
        //mPrintJobs.add(printJob);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (PRINTANJE_INTENT) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.

                    hlodi= data.getStringArrayListExtra(MainActivity.SEZNAM_COKOV);
                    adapter2 = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1,hlodi);
                    seznamHlodov.setAdapter(adapter2);
                    Log.d(LOG_TAG,"dobu sm nazaj elementov stevilo"+hlodi.size());
                }
                break;
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        output.putStringArrayListExtra(MainActivity.SEZNAM_COKOV, hlodi);
        setResult(RESULT_OK, output);
        finish();
        super.onBackPressed();
    }









}
