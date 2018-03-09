package com.devdelhi.pointgram.pointgram.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devdelhi.pointgram.pointgram.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activity_PreviewAlarm extends AppCompatActivity implements OnMapReadyCallback {

    String name, desc, userID, nameOfPlace, buttonText;
    int range;
    Double lat, lng;
    private LatLng latLng;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Marker marker;
    private MarkerOptions mMarkerOptions;

    private TextView alarmNameTV, alarmDescriptionTV, alarmRangeTV, alarmPlaceTV;
    private Button confirmAlarmButton;

    private static final String TAG = "DEEJAY";
    private ProgressDialog mProgressDialog;
    private DatabaseReference mAlarmDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__alarm__preview);

        Intent i = getIntent();
        Bundle b = getIntent().getExtras();

        mProgressDialog = new ProgressDialog(this);

        confirmAlarmButton = findViewById(R.id.confirmAlarmButton);
        alarmNameTV = findViewById(R.id.alarmNameTV);
        alarmDescriptionTV = findViewById(R.id.alarmDescTV);
        alarmRangeTV = findViewById(R.id.alarmRangeTV);
        alarmPlaceTV = findViewById(R.id.nameofplaceTV);

        mProgressDialog.setTitle("Please Wait");
        mProgressDialog.setMessage("We are processing your request.");
        mProgressDialog.setCanceledOnTouchOutside(false);

        name = b.getString("Name");
        buttonText = b.getString("ButtonText");
        desc = b.getString("Desc");
        range = b.getInt("range");
        userID = b.getString("UserID");
        lat = b.getDouble("lat");
        lng = b.getDouble("lng");
        nameOfPlace = b.getString("NameOfPlace");

        latLng = new LatLng(lat, lng);

        Log.d(TAG, "Got An Intent");

        Log.d(TAG, name);
        Log.d(TAG, nameOfPlace);
        Log.d(TAG, buttonText);
        Log.d(TAG, desc);
        Log.d(TAG, range + "");
        Log.d(TAG, userID);
        Log.d(TAG, lat + "");
        Log.d(TAG, lng + "");

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_preview);
        mapFragment.getMapAsync(this);

        mMarkerOptions = new MarkerOptions().position(new LatLng(lat, lng)).title("Alarm Location");

        alarmNameTV.setText(name.toUpperCase());
        alarmDescriptionTV.setText(desc+"");
        alarmRangeTV.setText(range+"");
        alarmPlaceTV.setText(nameOfPlace);
        confirmAlarmButton.setText(buttonText);

        confirmAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAlarmDataToServers(name, nameOfPlace, desc, range, userID, lat, lng);
                /*if (buttonText.equals("Confirm Alarm")) {

                }
                else if (buttonText.equals("Edit Alarm")) {
                    proceedToEditAlarm();
                }*/

            }
        });
    }

    private void proceedToEditAlarm() {
        Intent i = new Intent(Activity_PreviewAlarm.this, Activity_Create_Alarm.class);
        Bundle bundle = new Bundle();

        bundle.putString("Name", name);
        bundle.putString("NameOfPlace", nameOfPlace);
        bundle.putString("ButtonText", "Confirm Alarm");
        bundle.putString("Desc", desc);
        bundle.putString("UserID", userID);
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        bundle.putInt("range", range);

        i.putExtras(bundle);

        startActivity(i);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(mMarkerOptions);
        moveCamera(new LatLng(lat, lng), 60, "Alarm Location");
    }


    private void moveCamera(LatLng latLng, float zoom, String addressLine) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!addressLine.equals("My Location")){
            addMarkerTo(latLng, addressLine);
        }
    }

    private void addMarkerTo(LatLng latLng, String addressLine) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(addressLine);

        mMap.addMarker(options);
    }


    private void uploadAlarmDataToServers(String name,String nameOfPlace, String desc, int range, String userID, Double lat, Double lng) {
        mProgressDialog.show();
        mAlarmDatabase = FirebaseDatabase.getInstance().getReference().child("alarms_database").child(userID).child(System.currentTimeMillis()+""); //Getting Reference To Users Child

        Map alarmMap = new HashMap<>();
        alarmMap.put("name", name);
        alarmMap.put("description", desc);
        alarmMap.put("place", nameOfPlace);
        alarmMap.put("range", range);
        alarmMap.put("userID", userID);
        alarmMap.put("lat", lat);
        alarmMap.put("lng", lng);

        mAlarmDatabase.setValue(alarmMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    mProgressDialog.dismiss();
                    showSnackBarMessage("Alarm Saved Successfully");

                    Intent intent = new Intent(Activity_PreviewAlarm.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    showSnackBarMessage("Alarm Not Saved");
                    mProgressDialog.dismiss();
                }
            }
        });
    }
    private void showSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
