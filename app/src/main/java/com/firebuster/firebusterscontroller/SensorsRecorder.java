package com.firebuster.firebusterscontroller;

import android.bluetooth.BluetoothSocket;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.nio.charset.Charset;

/**
 * Created by Léo on 05/04/2017.
 */

public class SensorsRecorder implements SensorEventListener {

    public BluetoothService connectedThread;

    public SensorsRecorder(BluetoothService connectedThread){
        this.connectedThread = connectedThread;
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
        {
            byte[] rVectorData = (">" + event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n").getBytes(Charset.forName("UTF-8"));
            connectedThread.write(rVectorData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}
