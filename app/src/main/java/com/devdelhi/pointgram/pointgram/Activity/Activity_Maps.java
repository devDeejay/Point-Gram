package com.devdelhi.pointgram.pointgram.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devdelhi.pointgram.pointgram.Model.PlaceInfo;
import com.devdelhi.pointgram.pointgram.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Activity_Maps extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int DEFAULT_ZOOM = 50;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private MarkerOptions mMarkerOptions;
    private Marker marker;
    private TextView tv;
    private Double lat, lng, alt;
    private Float speed, acc;
    private String provider;
    private BroadcastReceiver broadcastReceiver;
    private String TAG = "DEEJAY";
    private ImageView moreInfoAboutPlace;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleAPIClient;
    private Marker mMarker;
    private AutoCompleteTextView mSearchText;
    private LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71,136));

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {

        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess())  {
                Log.d(TAG, "Place Query Not Completed" + places.getStatus().toString());
                places.release();
                return;
            }

            PlaceInfo placeInfo = new PlaceInfo();
            final Place place = places.get(0);

            try{
                placeInfo.setName(place.getName().toString());
                placeInfo.setAddress(place.getAddress().toString());
                placeInfo.setId(place.getId());
                placeInfo.setPhonenumber(place.getPhoneNumber().toString());
                placeInfo.setRating(place.getRating());
                placeInfo.setWebsite(place.getWebsiteUri());
                placeInfo.setLatLng(place.getLatLng());

                Log.d(TAG, placeInfo.toString());
            }
            catch (NullPointerException e) {
                Log.d(TAG, "Null Pointer Exception Occured");
                showSnackBarMessage("Something Went Wrong. Please Try Again.");
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, placeInfo);
            places.release();
        }
    };

    private AdapterView.OnItemClickListener mAutoCompleteListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideKeyboard();

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(i);
            final String placeID = item.getPlaceId();

            PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.getPlaceById(mGoogleAPIClient, placeID);
            placeBufferPendingResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.devdelhi.pointgram.pointgram.R.layout.activity__maps);

        lat = 0d;
        lng = 0d;

        moreInfoAboutPlace = findViewById(R.id.moreInfoAboutPlace);
        mSearchText = findViewById(R.id.inputSearch);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMarkerOptions = new MarkerOptions().position(new LatLng(lat, lng)).title("Current Location");

        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }

        if (!isLocationEnabled()) {
            showAlert(1);
        }
    }

    private void init() {
        Log.d(TAG, "init");

        mGoogleAPIClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutoCompleteListener) ;

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleAPIClient, LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(placeAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH
                        || actionID == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    Log.d(TAG, "GeoLocate Fired!");

                    //Execute Our Method To Search
                    geoLocate();
                }

                Log.d(TAG, "Button Not Pressed Yet");
                return false;
            }
        });

        moreInfoAboutPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicked Place Info");
                try{
                    if (mMarker.isInfoWindowShown()) {
                        mMarker.hideInfoWindow();
                    }
                    else {
                        Log.d(TAG, "OnClick Place Info");
                        mMarker.showInfoWindow();
                    }
                }
                catch (NullPointerException e) {
                    Log.d(TAG, "OnClick NullPointerException " + e.getMessage());
                }
            }
        });

        hideKeyboard();
    }

    private void geoLocate() {
        Log.d(TAG, "Geo Locating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(Activity_Maps.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        }
        catch (IOException e) {
            Log.d(TAG, "Geo Location : IO EXCEPTION " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "Geolocate : Found A Location : " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

        }
    }

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (isPermissionGranted()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                final Task location = fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Found Location");
                            showSnackBarMessage("Found Your Location");

                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");

                        } else {
                            Log.d(TAG, "Location is Null");
                            showSnackBarMessage("Cannot Get Your Location");
                        }
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    private void moveCamera(LatLng latLng, float zoom, String addressLine) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!addressLine.equals("My Location")){
            addMarkerTo(latLng, addressLine);
        }
    }

    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(Activity_Maps.this));

        if (placeInfo!=null){
            try {
                String snippet = "Address : " + placeInfo.getAddress() + "\n" +
                        "Phone Number : " + placeInfo.getPhonenumber() + "\n" +
                        "Website: " + placeInfo.getWebsite() + "\n" +
                        "Rating : + " + placeInfo.getRating() + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);

                mMarker = mMap.addMarker(options);
                mMap.addMarker(options);

            }
            catch (Exception e){
                showSnackBarMessage("Something Went Wrong!");
            }
        }
        else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
    }

    private void addMarkerTo(LatLng latLng, String addressLine) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(addressLine);

        mMap.addMarker(options);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //When Map Is Ready To Be Shown

        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");

        mMap = googleMap;

        getDeviceLocation();
        requestLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        marker = mMap.addMarker(mMarkerOptions);

        init();
    }

    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Your Location Settings is Off. Please Turn It Back On";
            title = "Can't Find Your Location";
            btnText = "Grant Permission";
        }
        else {
            message = "Please Allow Us To Access Device's Location";
            title = "Permission Access";
            btnText = "Grant Permission";
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (status == 1) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                        else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(PERMISSIONS, PERMISSION_ALL);
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission Granted");
                return true;
            }
            else {
                Log.d(TAG, "Permission Not Granted");
                return false;
            }
        }
        return false;
    }

    private void updateMapPointer(Double lat, Double lng) {

        Log.d(TAG, "Map Updated");

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mMarkerOptions = new MarkerOptions().position(new LatLng(lat,lng)).title("Current Location");
    }

    private void showSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        marker.setPosition(myCoordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void requestLocation() {
        Criteria criteria = new Criteria(); //helps to get Location Data on various Criteria
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        String provider = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            //    here to request the missing permissions, and then overriding
            //    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(provider, 2000, 10, this);
    }

    //---------------------------GOOGLE PLACES API AUTOCOMPLETE

    @Override
    protected void onResume() {
        super.onResume();

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Log.d(TAG, "Broadcast Received");

                    //2.Attach Data
                    lat = intent.getDoubleExtra("lat", 0);
                    lng = intent.getDoubleExtra("lng", 0);
                    alt = intent.getDoubleExtra("alt", 0);
                    speed = intent.getFloatExtra("speed", 0);
                    acc = intent.getFloatExtra("acc", 0);
                    provider = intent.getStringExtra("provider");

                    updateMapPointer(lat,lng);

                    Log.d(TAG, "Data : " + lat + ", " + lng + " " + speed);
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("User_Update")); // Calls onReceive Method
    }

    private void hideKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

}
