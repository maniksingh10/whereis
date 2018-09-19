package com.manik.whereisthebus;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class CurrentLoc implements LocationListener {
    double lat, logi;
    Context context;
    CurrentLoc(Context context){
        this.context = context;
    }
    @Override
    public void onLocationChanged(Location location) {
        lat= location.getLatitude();
        logi = location.getLongitude();
        Toast.makeText(context,lat +" "+logi,Toast.LENGTH_LONG).show();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(context,"Changingggg",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context,"GPS ONNNNN",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context,"GPS OFFFF",Toast.LENGTH_SHORT).show();
    }
}
