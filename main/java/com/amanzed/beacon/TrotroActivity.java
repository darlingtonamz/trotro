package com.amanzed.beacon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amanzed.beacon.map.MyLocationListener;
import com.amanzed.beacon.tro.Stop;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class TrotroActivity extends AppCompatActivity implements OnClickListener {
    final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    LocationManager lm;
    LocationListener ll;
    public static double lat, lon;
    TextView latText, lonText;
    Button but;
    private GoogleMap googleMap;
    DatabaseReference myRef;
    ArrayList<Stop> stops = new ArrayList<Stop>();
    ArrayList<String> stopNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new MyLocationListener();
        latText = (TextView) findViewById(R.id.lat);
        lonText = (TextView) findViewById(R.id.lon);
        but = (Button) findViewById(R.id.but);
        but.setOnClickListener(this);
        checkGPSpermission();
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("benchmark");

        //myRef.setValue("Hello, World!");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.child("stops").getChildren()) {
                    //Getting the data from snapshot
                    Stop stop = snap.getValue(Stop.class);
                    stops.add(stop);
                    stopNames.add(stop.getStop_name());
                    /*Log.d("beacon", "BEACON: "+ stop.getStop_name());
                    MarkerOptions marker = new MarkerOptions()
                            .position(new LatLng(stop.getStop_lat(), stop.getStop_lon_orig()))
                            .title(stop.getStop_name());
                    googleMap.addMarker(marker);    // adding marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            new LatLng(stop.getStop_lat(), stop.getStop_lon_orig())).zoom(14).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Spinner sFrom = (Spinner)findViewById(R.id.spinFrom);
        Spinner sTo = (Spinner)findViewById(R.id.spinTo);
        ArrayAdapter<String> karant_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stopNames);
        sTo.setAdapter(karant_adapter);
        sFrom.setAdapter(karant_adapter);
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void checkGPSpermission(){
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                return;
            }
        }else{
            startGPS();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.but:
                /*latText.setText(String.valueOf(lat));
                lonText.setText(String.valueOf(lon));
                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon)).title("Current location ");
                googleMap.addMarker(marker);    // adding marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(lat, lon)).zoom(16).build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                /*HashMap<String, String> out = new HashMap<>();
                out.put("driver_id", "1");
                out.put("lat", "1.34567876");
                out.put("lon", "4.23455432");
                out.put("pickup_id", "1");
                out.put("time", "14232324248");*/
                //myRef.push().setValue(out);

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGPS();
                }
                return;
            }
        }
    }

    @SuppressWarnings({"MissingPermission"})
    public void startGPS(){
        Log.d("beacon","START GPS");
        // (provider, mintime, mindistance, locationListener)
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, ll);
    }
}
