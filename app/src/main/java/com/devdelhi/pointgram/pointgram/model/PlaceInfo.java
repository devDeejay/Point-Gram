package com.devdelhi.pointgram.pointgram.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {
    private String name;
    private String address;
    private String phonenumber;
    private String id;
    private Uri website;
    private LatLng latLng;
    private Float rating;

    public PlaceInfo(){}

    public PlaceInfo(String name, String address, String phonenumber, String id, Uri website, LatLng latLng, Float rating ) {
        this.name = name;
        this.address = address;
        this.phonenumber = phonenumber;
        this.id = id;
        this.website = website;
        this.latLng = latLng;
        this.rating = rating;
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

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getWebsite() {
        return website;
    }

    public void setWebsite(Uri website) {
        this.website = website;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

}
