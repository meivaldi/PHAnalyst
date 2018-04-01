package com.meivaldi.phanalyst;

/**
 * Created by root on 25/03/18.
 */

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by User on 10/2/2017.
 */

public class PlaceInfo {

    private String address;
    private double latitude;
    private double longitude;
    private String phValue;

    public PlaceInfo(String address, double latitude,
                     double longitude, String value) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phValue = value;
    }

    public PlaceInfo() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhValue() {
        return phValue;
    }

    public void setPhValue(String phValue) {
        this.phValue = phValue;
    }

    @Override
    public String toString() {
        return "address='" + address + '\'' +
                ", latitude=" + latitude +'\'' +
                ", longitude=" + longitude +'\'' +
                ", phValue=" + phValue +'\'' ;
    }
}
