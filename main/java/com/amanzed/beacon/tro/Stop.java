package com.amanzed.beacon.tro;

/**
 * Created by Amanze on 5/27/2016.
 */
public class Stop {
    String stop_id, stop_name;
    double stop_lat, stop_lon_orig;

    public Stop(){}

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public double getStop_lat() {
        return stop_lat;
    }

    public void setStop_lat(double stop_lat) {
        this.stop_lat = stop_lat;
    }

    public double getStop_lon_orig() {
        return stop_lon_orig;
    }

    public void setStop_lon_orig(double stop_lon_orig) {
        this.stop_lon_orig = stop_lon_orig;
    }

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }
}
