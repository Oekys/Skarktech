package com.example.bluetoothtest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

public class MainActivity extends Activity  {

    private static final int REQUEST_ENABLE_BT = 0;

    TextView textView;

    //Bluetooth
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothDevice remoteDevice = null;

    // NFC
    NfcAdapter nfcAdapter = null;
    PendingIntent pendingIntent = null;
    IntentFilter[] intentFiltersArray = null;
    String[][] techListsArray = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //inialisier l'adaptateur bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "NO Bluetooth Capabilities",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        //Initialiser l'adaptateur nfc
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NO NFC Capabilities",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        //pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/mac");
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndef, };

        techListsArray = new String[][] { new String[] { MifareUltralight.class.getName() } };

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Toast.makeText(this, "Intent read",Toast.LENGTH_SHORT).show();
        setIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        assert nfcAdapter != null;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // allumer le bluetooth
        onBluetooth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // eteindre le bluetooth
        offBluetooth();
    }

    @SuppressLint("MissingPermission")
    private void onBluetooth() {
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            //bluetoothAdapter.enable();
        }
    }

    @SuppressLint("MissingPermission")
    private void offBluetooth() {
        if(bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.disable();
        }
    }

    @SuppressLint("MissingPermission")
    private void pairBluetooth(String address) {
        if(bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.cancelDiscovery();
            try {
                remoteDevice = bluetoothAdapter.getRemoteDevice(address);
            }
            catch (Exception e) {
                Toast.makeText(this, "2. "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
            // error, suite non testé
            if (remoteDevice != null) {
                try {
                    remoteDevice.createBond();
                }
                catch (Exception e) {
                    Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String readTag(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            byte[] payload = mifare.readPages(4);
            return new String(payload, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            Log.e("NFC", "IOException while reading MifareUltralight message...", e);
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                }
                catch (IOException e) {
                    Log.e("NFC", "Error closing tag...", e);
                }
            }
        }
        return null;
    }

    // recuperer les données du tag et se connecter
    void processIntent(Intent intent) {
        textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String payload = new String(msg.getRecords()[0].getPayload());
        textView.setText(payload);
        if (bluetoothAdapter.checkBluetoothAddress(payload.toUpperCase())) {
            Toast.makeText(this, "1. Bluetooth Device: "+payload,Toast.LENGTH_LONG).show();
            pairBluetooth(payload.toLowerCase());
        }
        else {
            Toast.makeText(this, "1. Bluetooth Device not valid",Toast.LENGTH_LONG).show();
        }
    }

}