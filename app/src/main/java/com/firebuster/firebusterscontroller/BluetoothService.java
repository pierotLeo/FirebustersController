package com.firebuster.firebusterscontroller;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import android.app.Activity;

/**
 * Created by Léo on 05/04/2017.
 */

public class BluetoothService {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private MainActivity mainActivity;
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private SensorsRecorder sensorsRecorder;
    private SensorManager sensorManager;
    private Sensor sensor;
    // Defines several constants used when transmitting messages between the
    // service and the UI.
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;


    public BluetoothService(BluetoothSocket socket, MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }

        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        sensorsRecorder = new SensorsRecorder(this);
        sensorManager = (SensorManager) mainActivity.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if(sensor != null) {
            sensorManager.registerListener(sensorsRecorder, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // Call this from the main activity to send data to the remote device.
    public boolean write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
            //mainActivity.connectionStateDisplay.append("\nCommunicating rotation vector datas");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
            return false;
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

}
