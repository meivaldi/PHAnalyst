package com.meivaldi.phanalyst;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ResultActivity extends AppCompatActivity {

    private int[] layouts;
    private MyViewPagerAdapter myViewPagerAdapter;
    private Button map;

    TextView pHLabel;
    ViewPager suggestionPlant;

    Handler bluetoothIn;

    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private static String address;
    private ConnectedThread mConnectedThread;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        suggestionPlant = (ViewPager) findViewById(R.id.suggestionPlant);
        pHLabel = (TextView) findViewById(R.id.pHValue);
        map = (Button) findViewById(R.id.seeMap);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Map.class));
            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);

                        if (recDataString.charAt(0) == '#')
                        {
                            String sensor = recDataString.substring(1, 5);

                            pHLabel.setText(sensor);
                        }
                        recDataString.delete(0, recDataString.length());
                    }
                }
            };

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        String pHValue = pHLabel.getText().toString();
        float pH = Float.parseFloat(pHValue);

        if(pH == 7.0){
            layouts = new int[]{
                    R.layout.plant_cabbage,
                    R.layout.plant_banana,
                    R.layout.plant_broccoli
            };
        } else if(pH == 6.5){
            layouts = new int[]{
                    R.layout.plant_carrot,
                    R.layout.plant_melon,
                    R.layout.plant_mint
            };
        } else if(pH == 6.0){
            layouts = new int[]{
                    R.layout.plant_garlic,
                    R.layout.plant_pakcoy,
                    R.layout.plant_papaya
            };
        } else if(pH == 5.5){
            layouts = new int[]{
                    R.layout.plant_onion,
                    R.layout.plant_pineapple,
                    R.layout.plant_potato
            };
        } else if(pH == 5.0){
            layouts = new int[]{
                    R.layout.plant_watermelon,
                    R.layout.plant_spinach,
                    R.layout.plant_radish,
                    R.layout.plant_strawberry
            };
        } else {
            layouts = new int[]{ R.layout.plant_default };
        }

        myViewPagerAdapter = new MyViewPagerAdapter();
        suggestionPlant.setAdapter(myViewPagerAdapter);
        suggestionPlant.addOnPageChangeListener(viewPagerPageChangeListener);

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        address = intent.getStringExtra(MainActivity.EXTRA_DEVICE_ADDRESS);

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {

            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        mConnectedThread.write("x");
    }

    private void checkBTState() {

        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e2) {

        }
    }
}
