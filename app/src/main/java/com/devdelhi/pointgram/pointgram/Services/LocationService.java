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
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class LocationService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private String TAG = "LocationService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mAuth = FirebaseAuth.getInstance();
                mUserRef = FirebaseDatabase.getInstance().getReference().child("users_database").child(mAuth.getCurrentUser().getUid()).child("location");

                Double lat = location.getLatitude();
                Double lng = location.getLongitude();
                Double alt = location.getAltitude();
                float acc = location.getAccuracy();
                float speed = location.getSpeed() * ( 18 / 5 );
                String provider = location.getProvider();

                Log.d(TAG, "Getting Data");
                Log.d(TAG, lat + "");
                Log.d(TAG, lng + "");
                Log.d(TAG, alt + "");
                Log.d(TAG, acc + "");
                Log.d(TAG, speed + "");
                Log.d(TAG, provider + "");

                Map locationMap = new HashMap();
                locationMap.put("lat", lat);
                locationMap.put("lng", lng);
                locationMap.put("alt", alt);
                locationMap.put("acc", acc);
                locationMap.put("speed", speed);
                locationMap.put("provider", provider);

                //Adding Friends To Database

                mUserRef.updateChildren(locationMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Log.d(TAG, "Location Updated On Firebase");
                        }
                    }
                });

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

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationManager != null) {
            Log.d(TAG, "Service Stopped");
            Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_LONG).show();
            locationManager.removeUpdates(listener);
        }
    }
}
