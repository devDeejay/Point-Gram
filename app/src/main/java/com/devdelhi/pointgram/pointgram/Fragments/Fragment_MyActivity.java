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
import android.widget.TextView;
import android.widget.Toast;

import com.devdelhi.pointgram.pointgram.Activity.Activity_Create_Alarm;
import com.devdelhi.pointgram.pointgram.Activity.Activity_Maps;
import com.devdelhi.pointgram.pointgram.Manifest;
import com.devdelhi.pointgram.pointgram.R;
import com.devdelhi.pointgram.pointgram.Services.LocationService;
import com.devdelhi.pointgram.pointgram.Services.Service_GPS;


public class Fragment_MyActivity extends Fragment {

    private int MY_PERMISSION_REQUEST_FINE_LOCATION = 1;
    private BroadcastReceiver broadcastReceiver;
    private TextView coordinatesTextView;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    public Fragment_MyActivity() {
        // Required empty public constructor
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
                    coordinatesTextView.append("\n" + lat + " , " + lng);

                }
            };
        }

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("User_Update"));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
        }
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
        getActivity().unregisterReceiver(broadcastReceiver); // Calls onReceive Method

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_activity, container, false);
        coordinatesTextView = view.findViewById(R.id.coordinatesTextView);
        Button myCustomButton = view.findViewById(R.id.startMapForUser);
        Button stopServiceButton = view.findViewById(R.id.stopServiceButton);

        getLocationPermissions();

        myCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mLocationPermissionGranted) {
                    startLocationService();
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
                Intent intent = new Intent(getActivity(), LocationService.class);
                getActivity().stopService(intent);
            }
        });

        return view;
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

    public void startLocationService() {
        Intent intent = new Intent(getActivity(), LocationService.class);
        getActivity().startService(intent);
    }
}
