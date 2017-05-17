package com.example.matejk.cwaki;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.RT_Printer.BluetoothPrinter.BLUETOOTH.BluetoothPrintDriver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Printanje extends AppCompatActivity {



    private static final int DOBAVNICA=0, PREVZEMNICA=1, ODKUPNILIST=2;

    private static final String LOG_TAG="PrintanjeActivity";
    private Button buttonPrevzemnica, buttonDobavnica, buttonOdkupniList;

    private ArrayList<String> hlodi;
    ListOfLogs hlodovina;
    EditText lastnikNaziv, lastnikNaslov1, lastnikNaslov2, prevoznikNaziv, prevoznikReg, prevoznikKraj, kupecNaziv, kupecNaslov1, kupecNaslov2;
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printanje);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        scrollView = (ScrollView) findViewById(R.id.scrollView3);
        lastnikNaziv = (EditText) findViewById(R.id.lastnikIme);
        lastnikNaslov1 = (EditText) findViewById(R.id.lastnikNaslov);
        lastnikNaslov2 = (EditText) findViewById(R.id.lastnikNaslov2);

        prevoznikNaziv = (EditText) findViewById(R.id.prevoznikNaziv);
        prevoznikReg = (EditText) findViewById(R.id.prevoznikReg);
        prevoznikKraj = (EditText) findViewById(R.id.prevoznikKraj);

        kupecNaziv = (EditText) findViewById(R.id.imeStranke);
        kupecNaslov1 = (EditText) findViewById(R.id.strankaNaslov);
        kupecNaslov2 = (EditText) findViewById(R.id.strankaNaslov2);


        buttonPrevzemnica = (Button) findViewById(R.id.buttonPrevzemnica);
        buttonPrevzemnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printanjeDokumenta(PREVZEMNICA);
            }
        });

        buttonDobavnica = (Button) findViewById(R.id.buttonDobavnica);
        buttonDobavnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printanjeDokumenta(DOBAVNICA);
            }
        });

        buttonOdkupniList = (Button) findViewById(R.id.buttonOdkupniList);
        buttonOdkupniList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printanjeDokumenta(ODKUPNILIST);
            }
        });



        Intent intent = getIntent();
        hlodi = new ArrayList<String>();

        hlodi= intent.getStringArrayListExtra(MainActivity.SEZNAM_COKOV);

        hlodovina = ListOfLogs.parseList(hlodi);

        Intent serverIntent = null;

        // Launch the DeviceListActivity to see devices and do scan
        serverIntent = new Intent(Printanje.this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);

        //da ostanejo podatki
        Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

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

        kupecNaziv.setText(sharedPref.getString(getString(R.string.saved_kupecNaziv), "Lesonit doo"));
        kupecNaslov1.setText(sharedPref.getString(getString(R.string.saved_kupecNaslov1), ""));
        kupecNaslov2.setText(sharedPref.getString(getString(R.string.saved_kupecNaslov2), "6250 Ilirska Bistrica"));

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


        editor.commit();
    }

    //BT printing

    private void printanjeDokumenta(Integer vrstaIzpisa){

        //shrani trenutne vnose v polja
        shraniVnesenePodatke();

        String timeStamp = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());

        //glava
        String printString = "\n" +
                "ESIMIT d.o.o               datum:" + timeStamp +"\n"+
                "Gabrije 5\n" +
                "6250 Ilirska Bistrica\n" +
                "\n";


        switch(vrstaIzpisa){
        case PREVZEMNICA:
                printString += "\n" + pripraviPrevzemnico();
            break;
        case DOBAVNICA:
                printString += "\n" + pripraviDobavnico();
            break;
        case ODKUPNILIST:
                printString += "\n" + pripraviOdkupniList();
            break;
        }


        printString += "\n\n";




        //nastavitve izpisa

        Boolean enlargeFont=false, underlineFont=false, boldFont=false, characterFont=false, blackInverse=false;

        Log.d(LOG_TAG, "Vsebina:"+printString);

        if(BluetoothPrintDriver.IsNoConnection()){
            Toast.makeText(getApplicationContext(),"Ni povezave s tiskalnikom", Toast.LENGTH_SHORT).show();
            return;
        }
        BluetoothPrintDriver.Begin();
        if(enlargeFont){
            BluetoothPrintDriver.SetFontEnlarge((byte)0x10);
        }
        if(underlineFont){
            BluetoothPrintDriver.SetUnderline((byte)0x02);
        }
        if(boldFont){
            BluetoothPrintDriver.SetBold((byte)0x01);
        }
        if(characterFont){
            BluetoothPrintDriver.SetCharacterFont((byte)0x01);
        }
        if(blackInverse){
            BluetoothPrintDriver.SetBlackReversePrint((byte)0x01);
        }


        String tmpContent = printString;
        BluetoothPrintDriver.BT_Write(tmpContent);
        BluetoothPrintDriver.BT_Write("\r");



    }

    private String pripraviOdkupniList() {

        String odkupniList=
                "_____________ODKUPNI LIST____________\n" +
                "-------------- LASTNIK --------------\n" +
                "Naziv:  "+lastnikNaziv.getText()+"\n" +
                "Naslov: "+lastnikNaslov1.getText()+"\n" +
                "Kraj:   "+lastnikNaslov2.getText()+"\n" +
                "\n" +
                "\n"+
                "------------- PREVOZNIK ------------- \n" +
                "Naziv: "+prevoznikNaziv.getText()+"\n" +
                "Reg št:"+prevoznikReg.getText()+"\n" +
                "Kraj:  "+prevoznikKraj.getText()+"\n" +
                "\n";

        odkupniList+=seznamHlodovineSKapitulacijo();




        return odkupniList;
    }

    private String pripraviDobavnico() {

        String dobavnica="";
        //glava dobavnice

        dobavnica+= "" +
                "______________DOBAVNICA______________\n" +
                "--------------- KUPEC ---------------\n" +
                "Naziv:  "+kupecNaziv.getText()+"\n" +
                "Naslov: "+kupecNaslov1.getText()+"\n" +
                "Kraj:   "+kupecNaslov2.getText()+"\n" +
                "\n" +

                "------------- PREVOZNIK ------------- \n" +
                "Naziv: "+prevoznikNaziv.getText()+"\n" +
                "Reg št:"+prevoznikReg.getText()+"\n" +
                "Kraj:  "+prevoznikKraj.getText()+"\n" +
                "\n" ;


        //dodaj seznam
        dobavnica+=seznamHlodovineSKapitulacijo();

        dobavnica+="_______________________________________________________________\n"+
                "Prevzel                         Prodajalec\n\n\n\n\n";

        return dobavnica;
    }

    private String pripraviPrevzemnico() {
        String prevzemnica="";

        //glava prevzemnice
        prevzemnica+= "" +
                "_____________PREVZEMNICA_____________\n" +
                "-------------- LASTNIK --------------\n" +
                "Naziv:  "+lastnikNaziv.getText()+"\n" +
                "Naslov: "+lastnikNaslov1.getText()+"\n" +
                "Kraj:   "+lastnikNaslov2.getText()+"\n" +
                "\n" +
                "\n"+
                "------------- PREVOZNIK ------------- \n" +
                "Naziv: "+prevoznikNaziv.getText()+"\n" +
                "Reg št:"+prevoznikReg.getText()+"\n" +
                "Kraj:  "+prevoznikKraj.getText()+"\n" +
                "\n";



        //dodaj seznam
        prevzemnica+=seznamHlodovineSKapitulacijo();

        prevzemnica+="_______________________________________________________________\n"+
                "Prevzel                         Lastnik\n\n\n\n\n";

        return prevzemnica;
    }

    public String seznamHlodovineSKapitulacijo(){
        String seznamHlodovine = "";
        //vsebina seznama
        seznamHlodovine+="_______________________________________________________________\n"+
                String.format("%10s %30s %10s %10s","SORTIMENT","D","L","m3\n");

        Map<String, Double> sortimentKapitulacija = new HashMap<String,Double>();
        Map<String, Integer> sortimentKomadi = new HashMap<String, Integer>();
        Double skupajKubiki = 0.0;

        for (int i = 0 ; i < hlodovina.sorte.size(); i++)
        {
            //naredimo za kapitulacijo
            String sortimentKey = hlodovina.sorte.get(i)+"-"+hlodovina.klase.get(i);
            if(!sortimentKapitulacija.containsKey(sortimentKey)) {
                sortimentKapitulacija.put(sortimentKey, hlodovina.kubiki.get(i));
                sortimentKomadi.put(sortimentKey,1);
            }
            else {
                sortimentKapitulacija.put(sortimentKey, sortimentKapitulacija.get(sortimentKey) + hlodovina.kubiki.get(i));
                sortimentKomadi.put(sortimentKey,sortimentKomadi.get(sortimentKey)+1);
            }
            //dodamo kubike
            skupajKubiki+=hlodovina.kubiki.get(i);
            //dodamo na seznam sortimenta
            seznamHlodovine+=String.format("%20s %20s %10s %10s %s",sortimentKey , hlodovina.dolzine.get(i)+"", hlodovina.premeri.get(i)+"",hlodovina.kubiki.get(i)+"","\n");

        }

        seznamHlodovine+="________________________________________________________________\n" +
                "SKUPAJ = "+hlodovina.sorte.size()+"                                     "+skupajKubiki+" m3 \n" +
                "" +
                "--------- REKAPITULACIJA --------- \n";

        Set keys = sortimentKapitulacija.keySet();

        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            String sortaKlasa = (String) i.next();
            String steviloSortaKlasa = (Integer) sortimentKomadi.get(sortaKlasa)+"";
            String kubikiSortaKlasa = (Double) sortimentKapitulacija.get(sortaKlasa)+" m3";
            seznamHlodovine+=String.format("%20s %20s %20s",sortaKlasa ,steviloSortaKlasa+" kos", kubikiSortaKlasa+"\n");
        }


        return seznamHlodovine;
    }


    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private static final boolean D = true;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static int revBytes=0;
    public  static boolean isHex=false;

    public static final int REFRESH = 8;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothPrintDriver mChatService = null;




    private void setupChat() {
        Log.d(LOG_TAG, "setupChat()");
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothPrintDriver(this, mHandler);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(LOG_TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothPrintDriver.STATE_CONNECTED:
                            // mTitle.setText(R.string.title_connected_to);
                            // mTitle.append(mConnectedDeviceName);
                            //setTitle(R.string.title_connected_to);
                            //setTitle(mConnectedDeviceName);
                            break;
                        case BluetoothPrintDriver.STATE_CONNECTING:
                            // mTitle.setText(R.string.title_connecting);
                            //setTitle(R.string.title_connecting);
                            break;
                        case BluetoothPrintDriver.STATE_LISTEN:
                        case BluetoothPrintDriver.STATE_NONE:
                            // mTitle.setText(R.string.title_not_connected);
                            //setTitle(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    String ErrorMsg = null;
                    byte[] readBuf = (byte[]) msg.obj;
                    float Voltage = 0;
                    if(D) Log.i(LOG_TAG, "readBuf[0]:"+readBuf[0]+"  readBuf[1]:"+readBuf[1]+"  readBuf[2]:"+readBuf[2]);
                    if(readBuf[2]==0)
                        ErrorMsg = "NO ERROR!         ";
                    else
                    {
                        if((readBuf[2] & 0x02) != 0)
                            ErrorMsg = "ERROR: No printer connected!";
                        if((readBuf[2] & 0x04) != 0)
                            ErrorMsg = "ERROR: No paper!  ";
                        if((readBuf[2] & 0x08) != 0)
                            ErrorMsg = "ERROR: Voltage is too low!  ";
                        if((readBuf[2] & 0x40) != 0)
                            ErrorMsg = "ERROR: Printer Over Heat!  ";
                    }
                    Voltage = (float) ((readBuf[0]*256 + readBuf[1])/10.0);
                    //if(D) Log.i(TAG, "Voltage: "+Voltage);
                    Toast.makeText(getApplicationContext(), ErrorMsg+"    "+"Battery voltage："+Voltage+" V", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public void onBackPressed() {

        Intent output = new Intent();
        output.putStringArrayListExtra(MainActivity.SEZNAM_COKOV, hlodi);
        setResult(RESULT_OK, output);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(LOG_TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if(D) Log.d(LOG_TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(LOG_TAG, "BT not enabled");
                    //Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }



    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(LOG_TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothPrintDriver.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }


    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(LOG_TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(LOG_TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(LOG_TAG, "--- ON DESTROY ---");
    }

    @SuppressLint("NewApi")
    private void ensureDiscoverable() {
        if(D) Log.d(LOG_TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
}
