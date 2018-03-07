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
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;
    BluetoothDevice bluetoothDevice = null;
    ProgressDialog progress;
    String address;
    InputStream inputStream;
    OutputStream outputStream;

    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth Tidak Tersedia", Toast.LENGTH_LONG).show();

            finish();
        }
        else if(!bluetoothAdapter.isEnabled())
        {
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ResultActivity.class));
            }
        });

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btConnect.getText().toString().equals("Connect")){
                    connectBlueTooth();
                    new ConnectBT().execute();
                } else if(btConnect.getText().toString().equals("Disconnect")){
                    disconnectBluetooth();
                }
            }
        });

    }

    private void readData() throws IOException{
        int bytes = 0;
        byte[] buffer = new byte[256];

        bytes = inputStream.read(buffer);

        String data = new String(buffer, 0, bytes);
        dataStore.setText(data);
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
    }


    private void disconnectBluetooth() {
        try {
            inputStream.close();
            outputStream.close();
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btConnect.setText("Connect");
        labelStatus.setText("Disconnected");
        imageStatus.setImageResource(R.drawable.ic_bluetooth_disabled_white_24dp);
        statusBar.setBackgroundColor(R.color.red);
    }

    private void connectBlueTooth() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

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

        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btConnect.setText("Disconnect");
        labelStatus.setText("Connected");
        imageStatus.setImageResource(R.drawable.ic_bluetooth_white_24dp);
        statusBar.setBackgroundColor(R.color.green);

    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {

        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                readData();
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

}
