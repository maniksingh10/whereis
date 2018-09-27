package com.manik.whereisthebus;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "Hereeee";
    private GoogleMap mMap;
    private DatabaseReference databaseReference;
    private double lat, logi;
    private Switch aSwitch;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isUpdateOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        aSwitch = findViewById(R.id.onoff);
        databaseReference = FirebaseDatabase.getInstance().getReference("BusLocation");
        checkPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);*/


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    isUpdateOn = true;
                    startLocation();
                    //sendLocation();
                } else {
                    isUpdateOn = false;
                    stopLocation();
                    // whereis();
                }
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        String currentDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa", Locale.getDefault()).format(new Date());

                        Toast.makeText(getApplicationContext(), "New Lat is " + String.valueOf(location.getLatitude()) + " \nlong is " +
                                String.valueOf(location.getLongitude()) + " \nAcc is " + String.valueOf(location.getAccuracy()) + " \nAlt is "  +
                                        String.valueOf(location.getAltitude()), Toast.LENGTH_SHORT).show();

                        String key = databaseReference.push().getKey();
                        ULocation uLocation = new ULocation(location.getLatitude(),location.getLongitude(), currentDate);
                        databaseReference.child(key).setValue(uLocation);
                        databaseReference.child("locate").setValue(uLocation);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Time was " + convertDate(location.getTime(), "dd/MM/yyyy hh:mm:ss")));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                    }
                }
            }
        };
    }

    private void stopLocation() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
whereis();
       }

    private void startLocation() {
        if(checkPermission()){
            displayLocationSettingsRequest(this);
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        }else{
            checkPermission();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //lListener = new LListener(this, mMap, databaseReference);
        mMap.setTrafficEnabled(true);
        mMap.setMaxZoomPreference(16.0f);
stopLocation();


    }

    private void whereis() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ULocation location = snapshot.getValue(ULocation.class);
                    lat = location.getLat();
                    logi = location.getLongi();
                }
                if (!isUpdateOn) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, logi)).title("I am Hereee").icon(BitmapDescriptorFactory.fromResource(R.drawable.img)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, logi)));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }































    public String convertDate(long dateInMilliseconds, String dateFormat) {
        return String.valueOf(DateFormat.format(dateFormat, dateInMilliseconds));
    }


    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        aSwitch.setChecked(false);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        aSwitch.setChecked(false);
                        break;
                }
            }
        });
    }


    public boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    aSwitch.setChecked(false);
                    displayLocationSettingsRequest(this);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    aSwitch.setChecked(false);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


//    private void sendLocation() {
//        mMap.clear();
//        displayLocationSettingsRequest(this);
//        if (checkPermission()) {
//
//            mMap.setMyLocationEnabled(true);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, lListener);
//        }
//
//    }
//
//

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  locationManager.removeUpdates(lListener);


    }





















    /*
    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        String key = databaseReference.push().getKey();
        ULocation uLocation = new ULocation(latitude, longitude, currentDate);

        databaseReference.child("locate").setValue(uLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(getApplicationContext(), provider + "  Changing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(), "GPS ONNNNN", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        aSwitch.setChecked(false);
        Toast.makeText(getApplicationContext(), "GPS OFFFF", Toast.LENGTH_SHORT).show();
        displayLocationSettingsRequest(this);
    }

*/

}
