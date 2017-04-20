package com.firebuster.firebusterscontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBtAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    public TextView connectionStateDisplay;

    private BluetoothServerThread aThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionStateDisplay = (TextView) findViewById(R.id.connectionState);
        setContentView(R.layout.activity_main);
        setupBluetoothConnection();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(aThread.isAlive())
        {
            aThread.cancel();
        }
    }

    private void setupBluetoothConnection()
    {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBtAdapter.isEnabled())
        {
            //requesting bluetooth activation
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if(mBtAdapter.isEnabled()){
            //Enabling discoverability
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);

            //Start server socket opening
            aThread = new BluetoothServerThread(this);
            aThread.start();
        }


    }


    private class BluetoothServerThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        private MainActivity mainActivity;
        private BluetoothService btService;

        public BluetoothServerThread(MainActivity mainActivity) {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            this.mainActivity = mainActivity;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBtAdapter.listenUsingRfcommWithServiceRecord("FirebustersSensors", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch (IOException e) {
                Log.e("AcceptThread", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("AcceptThread", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    try {
                        // A connection was accepted. Perform work associated with
                        // the connection in a separate thread.
                        //manageMyConnectedSocket(socket);
                        btService = new BluetoothService(socket, mainActivity);
                        connectionStateDisplay.setText("Connection established!");
                        //mmServerSocket.close();
                        //break;
                    } catch (Exception e) {
                        Log.e("AcceptThread", "Socket's close() method failed", e);
                        break;
                    }
                }

            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
                btService.cancel();
            } catch (IOException e) {
                Log.e("AcceptThread", "Could not close the connect socket", e);
            }
        }


    }

}
