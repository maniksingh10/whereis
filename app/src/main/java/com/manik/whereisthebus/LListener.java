package com.manik.whereisthebus;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class LListener implements LocationListener {

    private Context context;
    private GoogleMap mMap;
    private String currentDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa", Locale.getDefault()).format(new Date());

    private DatabaseReference databaseReference;
    LListener(Context context, GoogleMap mMap, DatabaseReference databaseReference){
        this.context = context;
        this.mMap = mMap;
        this.databaseReference = databaseReference;
    }


    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(context,"Changingggg",Toast.LENGTH_SHORT).show();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        String key = databaseReference.push().getKey();
        ULocation uLocation = new ULocation(latitude, longitude, currentDate);

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

    }
}
