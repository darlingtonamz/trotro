package com.amanzed.beacon;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amanzed.beacon.map.MyLocationListener;
import com.amanzed.beacon.tro.Edge;
import com.amanzed.beacon.tro.Stop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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

public class TrotroActivity extends AppCompatActivity implements OnClickListener, OnItemSelectedListener {
    final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    LocationManager lm;
    LocationListener ll;
    public static double lat, lon;
    TextView timeTV, distTV, moneyTV;
    Button but, plotBut;
    private GoogleMap googleMap;
    DatabaseReference myRef;
    public static ArrayList<Stop> stops = new ArrayList<Stop>();
    ArrayList<Edge> edges = new ArrayList<Edge>();
    ArrayList<String> stopNames = new ArrayList<String>();
    int from, to;
    LinearLayout theView;
    ArrayAdapter<String> adapter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isPlotted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trotro_activity);

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
                    Log.d("beacon", "BEACON: "+ stop.getStop_name());
                    /*MarkerOptions marker = new MarkerOptions()
                            .position(new LatLng(stop.getStop_lat(), stop.getStop_lon_orig()))
                            .title(stop.getStop_name());
                    googleMap.addMarker(marker);    // adding marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            new LatLng(stop.getStop_lat(), stop.getStop_lon_orig())).zoom(14).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
                }
                for (DataSnapshot snap : snapshot.child("edges").getChildren()) {
                    //Getting the data from snapshot
                    Edge edge = snap.getValue(Edge.class);
                    edges.add(edge);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    //Displaying the token as toast
                    Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new MyLocationListener();
        timeTV = (TextView) findViewById(R.id.timeTV);
        distTV = (TextView) findViewById(R.id.distTV);
        moneyTV = (TextView) findViewById(R.id.moneyTV);
        but = (Button) findViewById(R.id.but);
        plotBut = (Button) findViewById(R.id.plotBut);
        theView = (LinearLayout)findViewById(R.id.theView);
        but.setOnClickListener(this);
        plotBut.setOnClickListener(this);

        checkGPSpermission();
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Spinner sFrom = (Spinner)findViewById(R.id.spinFrom);
        Spinner sTo = (Spinner)findViewById(R.id.spinTo);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stopNames);
        sTo.setAdapter(adapter);
        sFrom.setAdapter(adapter);
        sTo.setOnItemSelectedListener(this);
        sFrom.setOnItemSelectedListener(this);
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
                if (isPlotted) {
                    Intent in = new Intent(this, PaymentActivity.class);
                    startActivity(in);
                }else{
                    Toast.makeText(this, "Please select a valid route", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.plotBut:
                Log.d("beacon", "FROM: "+from+" | TO: "+ to);
                if (from == to)
                    Toast.makeText(TrotroActivity.this, "Make sure your start is different from your destination", Toast.LENGTH_SHORT).show();
                else
                    plotFromTo();
                break;

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

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        Log.e("beacon", "Working");
        Log.d("beacon", "POS: "+pos);

        switch (parent.getId()){
            case R.id.spinFrom:
                Log.d("beacon", "from_POS: "+pos);
                from = pos;
                break;
            case R.id.spinTo:
                Log.d("beacon", "to_POS: "+pos);
                to = pos;
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void plotFromTo(){

        int i = from;
        boolean isBig = (from <= to);
        googleMap.clear();
        double estDist = 0; double estTime = 0;
        while (i <= to){
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(stops.get(i).getStop_lat(), stops.get(i).getStop_lon_orig()))
                    .title(stops.get(i).getStop_name());
            googleMap.addMarker(marker);    // adding marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(stops.get(i).getStop_lat(), stops.get(i).getStop_lon_orig())).zoom(14).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            Edge e = new Edge();
            if (isBig) {
                e = edges.get(i);
                i++;
            }else {
                e = edges.get(i-1);
                i--;
            }

            estDist += e.getDistance();
            estTime += (e.getDistance() / e.getSpeed());

            setInfo(estDist, estTime);
            isPlotted = true;
        }
    }
    public void setInfo(double dis, double time){
        distTV.setText(String.valueOf(dis)+"\nMeters");
        timeTV.setText(String.valueOf(Math.round(time/60)+"\nMins"));
        moneyTV.setText("1.2\nCedi");
        theView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_driver:
                Intent in = new Intent(this, DriverActivity.class);
                startActivity(in);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
