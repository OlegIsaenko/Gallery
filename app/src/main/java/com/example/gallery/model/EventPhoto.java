package com.example.gallery.model;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.Date;
import java.util.UUID;

public class EventPhoto {

    private UUID mUUID;
    private String title;
    private Date mDate;
    private String mDescription;
    private String mLocation;
    private double lat;
    private double lng;

    private static final String TAG = "tag";


    public EventPhoto() {
        this(UUID.randomUUID());
    }

    public EventPhoto(UUID uuid) {
        mUUID = uuid;
        mDate = new Date();
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocationText(String location) {
        mLocation = location;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPhotoFileName() {
        return "IMG_" + getUUID().toString() + ".jpg";
    }

    public void setLatLng(Location location) {
        if (location != null) {
            this.setLat(location.getLatitude());
            this.setLng(location.getLongitude());
        } else {
            Log.i(TAG, "setLatLng: null location");
        }
    }
}
