package com.baobab.user.baobabflyer.server.util;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;

public class GpsTracker extends Service implements LocationListener {

    private final Context context;
    Location location;
    double longitude;
    double latitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATE = 1000 * 60 * 1;
    protected LocationManager locationManager;

    public GpsTracker(Context context) {
        this.context = context;
    }

    public Location getLocation(){
        LoadDialog loading = new LoadDialog(context);
        try{
            loading.showDialog();

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
            }else {
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

                if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){

                }else {
                    return null;
                }

                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if(locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location != null){
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    }
                }

                if(isGPSEnabled){
                    if(location == null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if(locationManager != null){
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location != null){
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                            }
                        }
                    }
                }
            }
            loading.dialogCancel();
        }catch (Exception e){
            Log.d("GPS Tracker", e.toString());
            loading.dialogCancel();
        }
        return location;
    }

    public double getLongitude() {
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

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

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GpsTracker.this);
        }
    }
}
