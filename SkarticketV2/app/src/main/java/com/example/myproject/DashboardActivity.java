package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.widget.TextView;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class DashboardActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private static final String BT_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private Handler handler; // handler that gets info from Bluetooth service

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    //Bluetooth
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothDevice remoteDevice = null;
    String remoteDeviceMAC;

    // NFC
    NfcAdapter nfcAdapter = null;
    PendingIntent pendingIntent = null;
    IntentFilter[] intentFiltersArray = null;
    String[][] techListsArray = null;

    ConnectedThread connectedThread;
    ConnectThread connectThread;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

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

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("pos/mac");
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
        connectedThread.cancel();
        connectThread.cancel();
    }

    @SuppressLint("MissingPermission")
    private void onBluetooth() {
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }
    }

    @SuppressLint("MissingPermission")
    private void offBluetooth() {
        if(bluetoothAdapter.isEnabled())
        {
            bluetoothAdapter.disable();
        }
    }

    // recuperer les données du tag et se connecter
    @SuppressLint("MissingPermission")
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String payload = new String(msg.getRecords()[0].getPayload());
        if (bluetoothAdapter.checkBluetoothAddress(payload.toUpperCase())) {
            remoteDeviceMAC = payload.toUpperCase();
            //Toast.makeText(this, "Bluetooth Device: "+payload,Toast.LENGTH_LONG).show();
            bluetoothAdapter.cancelDiscovery();
            try {
                remoteDevice = bluetoothAdapter.getRemoteDevice(remoteDeviceMAC);
            }
            catch (Exception e) {
                Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
            }

            //Toast.makeText(this, "Bluetooth Device ready",Toast.LENGTH_SHORT).show();
            if (remoteDevice != null) {
                connectThread = new ConnectThread(remoteDevice);
                //Toast.makeText(this, "ConnectedThread ready",Toast.LENGTH_SHORT).show();
                connectThread.run();
            }
        }
        else {
            Toast.makeText(this, "Bluetooth Device not valid",Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        private String mMessageFromServer = "";

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("BT", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("BT", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            /*progressDialog = ProgressDialog.show(getApplicationContext(), "",
                    "Chargement du ticket en cours...", true);*/
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                //Toast.makeText(getApplicationContext(), "READING BT",Toast.LENGTH_SHORT).show();
                char c = (char)read();

                if (c == '|') {

                    if (mMessageFromServer.length() > 0) {
                        //progressDialog.dismiss();
                        launchPoPUp(mMessageFromServer);
                        mMessageFromServer = "";
                        break;
                    }
                }
                else {
                    mMessageFromServer += c;
                }
            }
        }

        public void write(byte[] bytes) {
            for (byte b : bytes) {
                write(b);
            }
        }


        private void write(byte b) {
            try {
                mmOutStream.write((int)b);
            }
            catch (IOException e) {
            }
        }

        public int read() {

            int i = -1;

            try {
                i = mmInStream.read();
            }
            catch (IOException e) {
            }

            return i;
        }

        private int available() {

            int n = 0;

            try {
                n = mmInStream.available();
            }
            catch (IOException e) {
            }

            return n;
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("BT", "Could not close the connect socket", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        @SuppressLint("MissingPermission")
        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(BT_UUID));
            } catch (IOException e) {
                Log.e("BT", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.

                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("BT", "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            if (mmSocket != null) {
                connectedThread = new ConnectedThread(mmSocket);
                String msg = "Connection to Client Phone established.\r\n";
                connectedThread.write(msg.getBytes());
                Toast.makeText(getApplicationContext(), "Connected",Toast.LENGTH_SHORT).show();
                connectedThread.run();
            }
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("BT", "Could not close the client socket", e);
            }
        }
    }

    public void launchPoPUp (String message) {
        //Toast.makeText(this, message,Toast.LENGTH_LONG).show();
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        TextView textView = (TextView) popupView.findViewById(R.id.popupView);
        textView.setText(message);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public void btnhome(View view){
        startActivity(new Intent(DashboardActivity.this,DashboardActivity.class));
    }

    public void btnbudget(View view){
        startActivity(new Intent(DashboardActivity.this,BudgetActivity.class));
    }

    public void btnticket(View view){
        startActivity(new Intent(DashboardActivity.this,TicketsActivity.class));
    }
}


