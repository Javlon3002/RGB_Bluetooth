package com.example.rgbbluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity { public String TAG = "-----------";

    private String macAddress = "";
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public int red, green, blue;

    private TextView txtRed, txtGreen, txtBlue;
    private SeekBar RED, GREEN, BLUE;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtRed = findViewById(R.id.txtRed);
        txtGreen = findViewById(R.id.txtGreen);
        txtBlue = findViewById(R.id.txtBlue);

        RED = findViewById(R.id.RED);
        GREEN = findViewById(R.id.GREEN);
        BLUE = findViewById(R.id.BLUE);


        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT > 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        }


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            System.out.println("Name->" + device.getName() + "    " + "MAC->" + device.getAddress());
            if (device.getName().equals("HC-06")) {
                macAddress = device.getAddress();
                System.out.println("MAC - > " + macAddress);
            }
        }


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);

        // Connecting to bluetooth device in a separate thread;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT > 31) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                        return;
                    }
                }

                try {
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                    Log.d("Message", "Connected to HC-06");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Bluetooth successfully connected", Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (IOException e) {
                    Log.d("Message", "Turn on bluetooth and restart the app");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Turn on bluetooth and restart the app", Toast.LENGTH_SHORT).show();
                        }
                    });
                    throw new RuntimeException(e);

                }

            }
        }).start();




        RED.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


                red = (int) (((float) 255 / 100) * i);
                txtRed.setText(Integer.toString(red));
                sendCommand(1, red);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        GREEN.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                green = (int) (((float) 255 / 100) * i);
                txtGreen.setText(Integer.toString(green));
                sendCommand(2, green);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        BLUE.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blue = (int) (((float) 255 / 100) * i);
                txtBlue.setText(Integer.toString(blue));
                sendCommand(3, blue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    private void sendCommand(int LED_index, int value) {

        if (outputStream == null) {
            Log.d(TAG, "Output stream error");
            return;
        }

        try {

            String command = "";
            if (LED_index == 1) {
                // if function is being used by RED LED
                command = "R_" + Integer.toString(value);



            }
            if (LED_index == 2) {
                // if function is being used by GREEN LED
                command = "G_" + Integer.toString(value);



            }
            if (LED_index == 3) {
                // if function is being used by BLUE LED
                command = "B_" + Integer.toString(value);

            }

            command = command + '\n';


            outputStream.write(command.getBytes());
            Log.d("Command- >", command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
                Log.d(TAG, "Connection closed");
            } catch (IOException e) {
                Log.d(TAG, "Error while closing the connection");
            }
        }
    }
}