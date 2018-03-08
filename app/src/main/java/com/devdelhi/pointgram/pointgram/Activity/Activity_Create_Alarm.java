package com.devdelhi.pointgram.pointgram.Activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devdelhi.pointgram.pointgram.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Create_Alarm extends AppCompatActivity {

    private EditText alarmName, alarmDescription, alarmRadius;
    private TextView alarmPlace;
    private Double lat = 0d,lng = 0d;
    private Button chooseLocationButton, addFriendsButton, previewAlarmButton;
    private static final int PLACE_PICKER_REQUEST = 1;
    private String userID = null;

    private static final String TAG = "CREATE_ALARM";
    private String nameOfPlaceSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__create__alarm);

        alarmName = findViewById(R.id.alarm_title);
        alarmDescription = findViewById(R.id.alarm_description);
        alarmRadius = findViewById(R.id.alarm_radius);
        alarmPlace = findViewById(R.id.alarmPlaceTV);

        chooseLocationButton = findViewById(R.id.chooseLocationButton);
        addFriendsButton = findViewById(R.id.addFriendsButton);
        previewAlarmButton = findViewById(R.id.previewAlarmButton);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        chooseLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int PLACE_PICKER_REQUEST = 1;
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(Activity_Create_Alarm.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        previewAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = alarmName.getText().toString(),
                        desc = alarmDescription.getText().toString(),
                        range = alarmRadius.getText().toString();

                if (isDataValid(name, desc, range, userID)) {
                    processDataForAlarm(name, desc, range, userID);
                }
                else {
                    showSnackBarMessage("Please Enter All The Required Details For The Alarm");
                }
            }
        });
    }

    private void processDataForAlarm(String name, String desc, String range, String userID) {
        Intent intent = new Intent(Activity_Create_Alarm.this, Activity_PreviewAlarm.class);

        Bundle bundle = new Bundle();

        bundle.putString("Name", name);
        bundle.putString("NameOfPlace", nameOfPlaceSelected);
        bundle.putString("ButtonText", "Confirm Alarm");
        bundle.putString("Desc", desc);
        bundle.putString("UserID", userID);
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        bundle.putInt("range", Integer.parseInt(range));

        Log.d(TAG, name);
        Log.d(TAG, desc);
        Log.d(TAG, range + "");
        Log.d(TAG, userID);
        Log.d(TAG, lat + "");
        Log.d(TAG, lng + "");

        intent.putExtras(bundle);

        startActivity(intent);
    }

    private boolean isDataValid(String name, String desc, String range, String userID) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(range) || userID == null ) {
            return false;
        }
        return true;
    }


    private void showSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                nameOfPlaceSelected = place.getName().toString();
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;
                alarmPlace.setText(nameOfPlaceSelected);
            }
        }
    }
}
