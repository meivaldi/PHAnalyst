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

    private String name;
    private String address;
    private String id;
    private LatLng latlng;
    private double phValue;

    public PlaceInfo(String name, String address, String id, LatLng latlng,
                     double value) {
        this.name = name;
        this.address = address;
        this.id = id;
        this.latlng = latlng;
        this.phValue = value;
    }

    public PlaceInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", latlng=" + latlng +
                ", phValue=" + phValue +'\'' +
                '}';
    }
}
