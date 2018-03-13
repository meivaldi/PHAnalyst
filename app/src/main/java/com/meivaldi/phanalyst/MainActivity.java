package com.meivaldi.phanalyst;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button calculate, btConnect;
    TextView labelStatus;
    ImageView imageStatus;
    LinearLayout statusBar;
    BluetoothSocket bluetoothSocket = null;
    BluetoothDevice bluetoothDevice = null;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private static final String TAG = MainActivity.class.getSimpleName();

    private BluetoothAdapter mBtAdapter;
    private BluetoothDevice myDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        btConnect = (Button) findViewById(R.id.btStatus);
        btConnect.setText("Connect");
        calculate = (Button) findViewById(R.id.startProcess);
        imageStatus = (ImageView) findViewById(R.id.bluetoothStatus);
        labelStatus = (TextView) findViewById(R.id.labelStatus);
        statusBar = (LinearLayout) findViewById(R.id.statusBar);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = myDevice.getAddress();

                Intent i = new Intent(MainActivity.this, ResultActivity.class);
                i.putExtra(EXTRA_DEVICE_ADDRESS, address);
                startActivity(i);
            }
        });

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        checkBTState();

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

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

    private void checkBTState() {
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

            }
        }
    }
}
