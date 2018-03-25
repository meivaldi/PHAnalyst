package com.meivaldi.phanalyst;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button btConnect;
    BluetoothDevice bluetoothDevice = null;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btConnect = (Button) findViewById(R.id.connect);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBtAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth Tidak Tersedia", Toast.LENGTH_LONG).show();

            finish();
        }
        else if(!mBtAdapter.isEnabled())
        {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectBlueTooth();
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                String address = bluetoothDevice.getAddress();

                if(!address.isEmpty()){
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Perangkat tidak ditemukan!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void connectBlueTooth() {
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                if(bt.getName().equals("HC-05")){
                    bluetoothDevice = bt;
                    break;
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Tidak ada perangkat yang ditemukan", Toast.LENGTH_LONG).show();
        }

    }

}
