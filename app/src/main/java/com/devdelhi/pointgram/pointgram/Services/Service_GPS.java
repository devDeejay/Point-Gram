package com.devdelhi.pointgram.pointgram.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

public class Service_GPS extends Service {

    private static final String TAG = "DEEJAY";
    private LocationListener locationListener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Service Created");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //On Location Being Changed
                Double lat = location.getLatitude();
                Double lng = location.getLongitude();
                Double alt = location.getAltitude();
                float acc = location.getAccuracy();
                float speed = location.getSpeed();
                String provider = location.getProvider();

                //3 Step Process Sending Data back to Main Activity Using Broadcast Receiver
                //1.Create Intent
                Intent intent = new Intent("User_Update");
                intent.putExtra("lat", lat+"");
                intent.putExtra("lng", lng+"");
                Log.d(TAG, "Sent Data " + lat + " , " + lng);

                ///Log.d(TAG,  "Lat " + lat + " Lng " + lng + " Alt " + alt + " Acc " + acc + " Speed " + speed + " Provider " + provider);

                //3.Send As Broadcast
                sendBroadcast(intent);

                Log.d(TAG, "-------Broadcast Sent From GPS Service------");
 }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
             //Called when the provider status changes.
                Log.d(TAG, "Provider Status Changed");
            }

            @Override
            public void onProviderEnabled(String s) {
                //Called when the provider is enabled by the user.
                Log.d(TAG, "Provider Enabled");
            }

            @Override
            public void onProviderDisabled(String s) {
                //Called when the provider is disabled by the user.
                Log.d(TAG, "Provider Disabled");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        if (isGPSOn()) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0 , locationListener); //Already Checked For Permissions
        }
        else {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0 , locationListener); //Already Checked For Permissions
        }
    }

    private boolean isGPSOn() {
        final LocationManager manager = (LocationManager)getApplicationContext().getSystemService    (Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            return false;
        else
            return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Releasing Resources
        if (locationManager != null) {
            //No Inspection For Missing Permission as it is already done at start of App
            locationManager.removeUpdates(locationListener);
        }
        Log.d(TAG, "Service Destroyed");
    }
}
