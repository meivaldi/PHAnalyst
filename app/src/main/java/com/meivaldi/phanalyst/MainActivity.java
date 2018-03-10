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
    TextView labelStatus, dataStore;
    ImageView imageStatus;
    LinearLayout statusBar;
    BluetoothSocket bluetoothSocket = null;
    BluetoothDevice bluetoothDevice = null;
    ProgressDialog progress;
    InputStream inputStream;
    OutputStream outputStream;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;


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
        dataStore = (TextView) findViewById(R.id.dataStore);
        statusBar = (LinearLayout) findViewById(R.id.statusBar);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResultActivity.class));
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
    }
}
