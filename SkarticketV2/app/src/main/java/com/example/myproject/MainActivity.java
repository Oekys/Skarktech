package com.example.myproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class MainActivity extends Activity  {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final String BT_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private Handler handler; // handler that gets info from Bluetooth service

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    TextView textView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            ndef.addDataType("pos/mac");
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndef, };

        techListsArray = new String[][] { new String[] { MifareUltralight.class.getName() } };

        // Register for broadcasts when a device is discovered.
        //IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(receiver, filter);

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
        //unregisterReceiver(receiver);
        connectedThread.cancel();
        connectThread.cancel();
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
    @SuppressLint("MissingPermission")
    void processIntent(Intent intent) {
        //textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String payload = new String(msg.getRecords()[0].getPayload());
        //textView.setText(payload);
        if (bluetoothAdapter.checkBluetoothAddress(payload.toUpperCase())) {
            remoteDeviceMAC = payload.toUpperCase();
            Toast.makeText(this, "Bluetooth Device: "+payload,Toast.LENGTH_LONG).show();
            //pairBluetooth(remoteDeviceMAC);
            bluetoothAdapter.cancelDiscovery();
            try {
                remoteDevice = bluetoothAdapter.getRemoteDevice(remoteDeviceMAC);
            }
            catch (Exception e) {
                Toast.makeText(this, "2. "+e.getMessage(),Toast.LENGTH_LONG).show();
            }

            Toast.makeText(this, "Bluetooth Device ready",Toast.LENGTH_SHORT).show();
            if (remoteDevice != null) {
                connectThread = new ConnectThread(remoteDevice);
                Toast.makeText(this, "ConnectedThread ready",Toast.LENGTH_SHORT).show();
                connectThread.run();
            }
        }
        else {
            Toast.makeText(this, "1. Bluetooth Device not valid",Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

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
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    textView.setText(new String(String.valueOf(numBytes)));
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d("BT", "Input stream was disconnected", e);
                    textView.setText("Input stream was disconnected");
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            textView.setText("Writing in socket");
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e("BT", "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
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
                connectedThread.run();
                String msg = "Test app: Sockets";
                connectedThread.write(msg.getBytes());
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


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceHardwareAddress.toUpperCase() == remoteDeviceMAC.toUpperCase()) {

                }
            }
        }
    };

}