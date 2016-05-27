package com.amanzed.beacon.map;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.amanzed.beacon.MainActivity;

public class MyLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            MainActivity.lat = location.getLatitude();
            MainActivity.lon = location.getLongitude();
        }
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}