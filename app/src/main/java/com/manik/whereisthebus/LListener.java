package com.manik.whereisthebus;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


public class LListener implements LocationListener{

    private static final String TAG = "LListenser";
    private Context context;
    private GoogleMap mMap;
    private String currentDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa", Locale.getDefault()).format(new Date());
    private double lklat, lklong;
    private DatabaseReference databaseReference;


    LListener(Context context, GoogleMap mMap, DatabaseReference databaseReference) {
        this.context = context;
        this.mMap = mMap;
        this.databaseReference = databaseReference;
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(context, "Coming to Your location", Toast.LENGTH_SHORT).show();
        lklat = location.getLatitude();
        lklong = location.getLongitude();
        LatLng latLng = new LatLng(lklat, lklong);
        String sats = String.valueOf(location.getExtras().getInt("satellites"));
        String acc = String.valueOf(location.getAccuracy());
        Toast.makeText(context, "Accu was "+acc+"%. Satellites used were ("+sats+")",Toast.LENGTH_SHORT).show();
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        String key = databaseReference.push().getKey();
        ULocation uLocation = new ULocation(lklat, lklong, currentDate);
        databaseReference.child(key).setValue(uLocation);
        databaseReference.child("locate").setValue(uLocation);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "Location Turned OFF",Toast.LENGTH_SHORT).show();

    }



    }


