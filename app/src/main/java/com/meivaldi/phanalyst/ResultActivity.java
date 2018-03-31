package com.meivaldi.phanalyst;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ResultActivity extends AppCompatActivity {

    private MyPageAdapter pagerAdapter = null;

    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String VALUE_KEY = "value";
    private static final String ADDRESS_KEY = "address";
    private int[] layouts;
    //private MyViewPagerAdapter myViewPagerAdapter;
    private Button map, saveValue;

    private TextView pHLabel;
    private ViewPager suggestionPlant = null;

    private Handler bluetoothIn;

    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private static String address;
    private ConnectedThread mConnectedThread;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final String TAG = "ResultActivity";
    private double latitude;
    private double longitude;
    private double value;

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

    private Geocoder geoCoder;
    private List<Address> addresses;
    private ProgressDialog progressDialog, dialog;

    //private static float pH;

    private RelativeLayout baseLayout;
    private LayoutInflater inflater;

    private List<RelativeLayout> previousLayout = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getLocationPermission();

        suggestionPlant = (ViewPager) findViewById(R.id.suggestionPlant);
        pHLabel = (TextView) findViewById(R.id.pHValue);
        map = (Button) findViewById(R.id.seeMap);
        saveValue = (Button) findViewById(R.id.simpanNilai);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Tunggu Sebentar");
        progressDialog.setIcon(R.drawable.sensor);
        progressDialog.setTitle("Sedang Memroses...");


        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Maps.class));
            }
        });

        saveValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                getLatLng();
            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);

                    int endOfLineIndex = recDataString.indexOf("~");
                    if (endOfLineIndex > 0) {
                        if (recDataString.charAt(0) == '#') {
                            String sensor = recDataString.substring(1, 5);
                            pHLabel.setText(sensor);

                            float ph = Float.parseFloat(pHLabel.getText().toString());
                            checkLayout(ph);
                        }
                        recDataString.delete(0, recDataString.length());
                        }
                    }
                }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        pagerAdapter = new MyPageAdapter();
        suggestionPlant = (ViewPager) findViewById(R.id.suggestionPlant);
        suggestionPlant.setAdapter(pagerAdapter);

        inflater = this.getLayoutInflater();
        baseLayout = (RelativeLayout) inflater.inflate(R.layout.plant_default, null);
        previousLayout.add(baseLayout);
        pagerAdapter.addView(baseLayout, 0);
        pagerAdapter.notifyDataSetChanged();
    }

    private void checkLayout(float pH) {
        removeLayout();
        if(pH == 6.0){
            RelativeLayout firstLayout = (RelativeLayout) inflater.inflate(R.layout.plant_cabbage, null);
            RelativeLayout secondLayout = (RelativeLayout) inflater.inflate(R.layout.plant_banana, null);
            RelativeLayout thirdLayout = (RelativeLayout) inflater.inflate(R.layout.plant_broccoli, null);
            previousLayout.add(firstLayout);
            previousLayout.add(secondLayout);
            previousLayout.add(thirdLayout);
            pagerAdapter.addView(firstLayout, 0);
            pagerAdapter.addView(secondLayout, 1);
            pagerAdapter.addView(thirdLayout, 2);
            pagerAdapter.notifyDataSetChanged();
        } else if(pH >= 5.5 && pH < 6.0){
            RelativeLayout firstLayout = (RelativeLayout) inflater.inflate(R.layout.plant_carrot, null);
            RelativeLayout secondLayout = (RelativeLayout) inflater.inflate(R.layout.plant_melon, null);
            RelativeLayout thirdLayout = (RelativeLayout) inflater.inflate(R.layout.plant_mint, null);
            previousLayout.add(firstLayout);
            previousLayout.add(secondLayout);
            previousLayout.add(thirdLayout);
            pagerAdapter.addView(firstLayout, 0);
            pagerAdapter.addView(secondLayout, 1);
            pagerAdapter.addView(thirdLayout, 2);
            pagerAdapter.notifyDataSetChanged();
        } else if(pH >= 5.0 && pH < 5.5){
            RelativeLayout firstLayout = (RelativeLayout) inflater.inflate(R.layout.plant_garlic, null);
            RelativeLayout secondLayout = (RelativeLayout) inflater.inflate(R.layout.plant_pakcoy, null);
            RelativeLayout thirdLayout = (RelativeLayout) inflater.inflate(R.layout.plant_papaya, null);
            previousLayout.add(firstLayout);
            previousLayout.add(secondLayout);
            previousLayout.add(thirdLayout);
            pagerAdapter.addView(firstLayout, 0);
            pagerAdapter.addView(secondLayout, 1);
            pagerAdapter.addView(thirdLayout, 2);
            pagerAdapter.notifyDataSetChanged();
        } else if(pH >= 4.5 && pH < 5.0){
            RelativeLayout firstLayout = (RelativeLayout) inflater.inflate(R.layout.plant_onion, null);
            RelativeLayout secondLayout = (RelativeLayout) inflater.inflate(R.layout.plant_pineapple, null);
            RelativeLayout thirdLayout = (RelativeLayout) inflater.inflate(R.layout.plant_potato, null);
            previousLayout.add(firstLayout);
            previousLayout.add(secondLayout);
            previousLayout.add(thirdLayout);
            pagerAdapter.addView(firstLayout, 0);
            pagerAdapter.addView(secondLayout, 1);
            pagerAdapter.addView(thirdLayout, 2);
            pagerAdapter.notifyDataSetChanged();
        } else if(pH >= 4.0 && pH < 4.5){
            RelativeLayout firstLayout = (RelativeLayout) inflater.inflate(R.layout.plant_watermelon, null);
            RelativeLayout secondLayout = (RelativeLayout) inflater.inflate(R.layout.plant_spinach, null);
            RelativeLayout thirdLayout = (RelativeLayout) inflater.inflate(R.layout.plant_radish, null);
            RelativeLayout fourthLayout = (RelativeLayout) inflater.inflate(R.layout.plant_strawberry, null);
            previousLayout.add(firstLayout);
            previousLayout.add(secondLayout);
            previousLayout.add(thirdLayout);
            previousLayout.add(fourthLayout);
            pagerAdapter.addView(firstLayout, 0);
            pagerAdapter.addView(secondLayout, 1);
            pagerAdapter.addView(thirdLayout, 2);
            pagerAdapter.addView(fourthLayout, 3);
            pagerAdapter.notifyDataSetChanged();
        } else if(pH != 0.0){
            Toast.makeText(getApplicationContext(), "Nilai diluar jangkauan!", Toast.LENGTH_LONG).show();
            RelativeLayout defaultLayout = (RelativeLayout) inflater.inflate(R.layout.plant_default, null);
            previousLayout.add(defaultLayout);
            pagerAdapter.addView(defaultLayout, 0);
            pagerAdapter.notifyDataSetChanged();
        }
    }

    private void removeLayout() {
        for(RelativeLayout layout: previousLayout){
            pagerAdapter.removeView(suggestionPlant, layout);
        }
    }

    /*public void addView (View newPage)
    {
        int pageIndex = pagerAdapter.addView (newPage);
        suggestionPlant.setCurrentItem (pageIndex, true);
    }

    public void removeView (View defunctPage)
    {
        int pageIndex = pagerAdapter.removeView (suggestionPlant, defunctPage);
        if (pageIndex == pagerAdapter.getCount())
            pageIndex--;
        suggestionPlant.setCurrentItem (pageIndex);
    }*/

    private void getLatLng() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ResultActivity.this);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                            try {
                                addresses = geoCoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String myAddress = addresses.get(0).getAddressLine(0);

                            Map<String, Object> dataToSave = new HashMap<String, Object>();
                            dataToSave.put(ADDRESS_KEY, myAddress);
                            dataToSave.put(LATITUDE_KEY, currentLocation.getLatitude());
                            dataToSave.put(LONGITUDE_KEY, currentLocation.getLongitude());
                            dataToSave.put(VALUE_KEY, pHLabel.getText().toString());

                            mFireStore.collection("PHAnalyst").document(myAddress).set(dataToSave)
                                    .addOnCompleteListener(ResultActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.hide();
                                                Toast.makeText(getApplicationContext(), "Berhasil Menyimpan data", Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressDialog.hide();
                                                Toast.makeText(getApplicationContext(), "Gagal Menyimpan data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Log.d(TAG, "onComplete: current location is null!");
                            Toast.makeText(ResultActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Security Exception: " + e.getMessage());
        }
    }

    private void getLocationPermission() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /*public class MyViewPagerAdapter extends PagerAdapter {
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
    }*/

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
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
