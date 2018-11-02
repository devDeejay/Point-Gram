package com.devdelhi.pointgram.pointgram.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.devdelhi.pointgram.pointgram.R;
import com.devdelhi.pointgram.pointgram.Services.LocationService;
import com.devdelhi.pointgram.pointgram.Support.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Fragment_MyActivity extends Fragment {

    private int MY_PERMISSION_REQUEST_FINE_LOCATION = 1;
    private BroadcastReceiver broadcastReceiver;
    private TextView coordinatesTextView;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private DatabaseReference muserLocationRef;

    private LinearLayout userLocationLayout;
    private TextView profileLocationTV;
    private TextView profileAltitudeTV;
    private TextView profileSpeedTV;
    private TextView profileProviderTV;
    private TextView profileLastKnownTimeTV;
    private TextView helpTextTV;

    private String TAG = "DEEJAY";
    private double mapLng = 0;
    private double mapLat = 0;
    private DatabaseReference mUserLocationDatabase;

    public Fragment_MyActivity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mUserLocationDatabase = FirebaseDatabase.getInstance().getReference().child("users_database")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location");

        View view = inflater.inflate(R.layout.fragment_my_activity, container, false);

        getLocationPermissions();

        muserLocationRef = FirebaseDatabase.getInstance().getReference().child("users_database")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("location").child("live");

        userLocationLayout = view.findViewById(R.id.xlocationDetailsLayout);
        helpTextTV = view.findViewById(R.id.helpText);

        profileLocationTV = view.findViewById(R.id.xprofileLocationTV);
        profileAltitudeTV = view.findViewById(R.id.xprofileAltitudeTV);
        profileSpeedTV = view.findViewById(R.id.xprofileSpeedTV);
        profileProviderTV = view.findViewById(R.id.xprofileProviderTV);
        profileLastKnownTimeTV = view.findViewById(R.id.xprofileLastKnownTimeTV);

        profileLocationTV.setText("");
        profileAltitudeTV.setText("");
        profileSpeedTV.setText("");
        profileProviderTV.setText("");

        Button startServiceButton = view.findViewById(R.id.startMapForUser);
        Button stopServiceButton = view.findViewById(R.id.stopServiceButton);

        showHelpText();

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mLocationPermissionGranted) {
                    startLocationService();
                    muserLocationRef.setValue("true");
                    displayUserLocationData();
                    //Intent firstpage = new Intent(getActivity(),Activity_Maps.class);
                    //getActivity().startActivity(firstpage);
                }
                else {
                    getLocationPermissions();
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Grant The Permissions.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muserLocationRef.setValue("false");
                showHelpText();
                Intent intent = new Intent(getActivity(), LocationService.class);
                getActivity().stopService(intent);
            }
        });

        return view;
    }

    private void showHelpText() {
        userLocationLayout.setVisibility(View.VISIBLE);
        userLocationLayout.setAlpha(0f);
        helpTextTV.setAlpha(1f);
    }

    private void hideHelpText() {
        userLocationLayout.animate().setDuration(1000).alpha(1f);
        helpTextTV.animate().setDuration(1000).alpha(0f);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String lat,lng;
                    lat = intent.getStringExtra("lat");
                    lng = intent.getStringExtra("lng");

                    Log.d("DEEJAY", "Got Data" + lat + " " + lng);

                }
            };
        }

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("User_Update"));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getLocationPermissions() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if((ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            mLocationPermissionGranted = true;
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
        }

    }

    private boolean checkRunTimePermissions() {
        boolean permissionIsGranted = false;
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new
                                String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                permissionIsGranted = true;
            }
        }
        else {
            permissionIsGranted = true;
        }
        return  permissionIsGranted;
    }


    private void displayUserLocationData() {
        hideHelpText();
        mUserLocationDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    double alt = Double.parseDouble(dataSnapshot.child("alt").getValue().toString());
                    double lat = Double.parseDouble(dataSnapshot.child("lat").getValue().toString());
                    double lng = Double.parseDouble(dataSnapshot.child("lng").getValue().toString());
                    double speed = Double.parseDouble(dataSnapshot.child("speed").getValue().toString());
                    String provider = dataSnapshot.child("provider").getValue().toString();
                    String time = dataSnapshot.child("lastUpdate").getValue().toString();

                    TimeAgo timeAgo = new TimeAgo();
                    long lastTime = Long.parseLong(time);
                    String lastKnown = timeAgo.getTimeAgo(lastTime, getActivity().getApplicationContext());

                    profileLastKnownTimeTV.setText("Last Update : " + lastKnown);

                    mapLat = lat;
                    mapLng = lng;

                    String latitude = Math.round(lat) + "";
                    String longitude = Math.round(lng) + "";
                    String userSpeed = Math.round(speed) + " Km / H";
                    String altitude = Math.round(alt) + " Meters";

                    Log.d(TAG, "Update Received");
                    Log.d(TAG, alt + "");
                    Log.d(TAG, latitude);
                    Log.d(TAG, longitude);
                    Log.d(TAG, provider);
                    Log.d(TAG, speed + "");

                    profileLocationTV.setText(latitude + " N " + longitude + " E");
                    profileAltitudeTV.setText(altitude);
                    profileSpeedTV.setText(userSpeed);
                    profileProviderTV.setText(provider.toUpperCase());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void startLocationService() {
        Intent intent = new Intent(getActivity(), LocationService.class);
        getActivity().startService(intent);
    }
}
