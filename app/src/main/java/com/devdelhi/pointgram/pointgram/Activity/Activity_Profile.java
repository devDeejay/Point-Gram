package com.devdelhi.pointgram.pointgram.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devdelhi.pointgram.pointgram.R;
import com.devdelhi.pointgram.pointgram.Support.TimeAgo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activity_Profile extends AppCompatActivity implements OnMapReadyCallback{

    private TextView userNameTV, userStatusTV, friendsCountTV;
    private Button addRemoveFriendsButton, declineRequestButton;
    private ImageView userImageView;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mUserLocationDatabase;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendRequestSentDatabase;
    private DatabaseReference mFriendRequestReceivedDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mRootDatabaseReference;
    private FirebaseUser mCurrentUser;

    private LinearLayout userLocationLayout;
    private TextView profileLocationTV;
    private TextView profileAltitudeTV;
    private TextView profileSpeedTV;
    private TextView profileProviderTV;
    private TextView profileLastKnownTimeTV;
    private GoogleMap mMap;
    private Marker marker;
    private MarkerOptions mMarkerOptions;

    private ProgressDialog mProgressDialog;
    private String TAG = "DEEJAY";
    private String currentFriendsState;
    private static final String NOT_FRIENDS = "not_friends";
    private static final String REQUEST_SENT = "request_sent";
    private static final String REQUEST_RECEIVED = "request_received";
    private static final String FRIENDS = "friends";
    private SupportMapFragment mapFragment;
    private double mapLng = 0;
    private double mapLat = 0;
    private String displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__profile);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.profileMapView);
        mapFragment.getMapAsync(this);

        mMarkerOptions = new MarkerOptions().position(new LatLng(mapLat, mapLng)).title("Alarm Location");

        final String userID = getIntent().getStringExtra("UserID");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users_database").child(userID);
        mUserLocationDatabase = FirebaseDatabase.getInstance().getReference().child("users_database").child(userID).child("location");
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests");

        mFriendRequestSentDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests").child("sent");
        mFriendRequestReceivedDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests").child("received");

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();

        declineRequestButton  = findViewById(R.id.declineRequestButton);
        declineRequestButton.setAlpha(0f);

        userLocationLayout = findViewById(R.id.locationDetailsLayout);
        userLocationLayout.setVisibility(View.GONE);

        profileLocationTV = findViewById(R.id.profileLocationTV);
        profileAltitudeTV = findViewById(R.id.profileAltitudeTV);
        profileSpeedTV = findViewById(R.id.profileSpeedTV);
        profileProviderTV = findViewById(R.id.profileProviderTV);
        profileLastKnownTimeTV = findViewById(R.id.profileLastKnownTimeTV);

        profileLocationTV.setText("");
        profileAltitudeTV.setText("");
        profileSpeedTV.setText("");
        profileProviderTV.setText("");

        Log.d(TAG, "Retrieved Data " + userID);

        userNameTV = findViewById(R.id.user_display_name);
        userStatusTV = findViewById(R.id.user_display_status);
        userImageView = findViewById(R.id.user_display_image);
        addRemoveFriendsButton = findViewById(R.id.addRemoveFriendButton);

        currentFriendsState = NOT_FRIENDS;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please Wait");
        mProgressDialog.setMessage("We are processing your request.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        if (mCurrentUser.getUid().equals(userID)) {
            addRemoveFriendsButton.setAlpha(0f);
            declineRequestButton.setAlpha(0f);
            displayUserLocationData();
        }

        mUsersDatabase.keepSynced(true);
        mFriendRequestSentDatabase.keepSynced(true);
        mFriendRequestReceivedDatabase.keepSynced(true);
        mFriendDatabase.keepSynced(true);

        //Getting Details Of User To Display In Profile Page
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayName = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                userNameTV.setText(displayName);
                userStatusTV.setText(status);
                Picasso.with(Activity_Profile.this).load(image).placeholder(R.drawable.avatar).into(userImageView);

                //---------------------------FRIEND LIST / REQUEST FEATURE--------------------------//

                mFriendRequestSentDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userID)) { //This user has sent request to this user.
                            currentFriendsState = REQUEST_SENT;
                            addRemoveFriendsButton.setText("Cancel Friend Request");
                        }

                        else {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(userID)) {
                                        currentFriendsState = FRIENDS;
                                        addRemoveFriendsButton.setText("Unfriend This Person");

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                        showSnackBarMessage("Something Went Wrong!");
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showSnackBarMessage("Something Went Wrong!");
                    }
                });



                mFriendRequestReceivedDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userID)) {
                            //User Has Received Request From This User
                            currentFriendsState = REQUEST_RECEIVED;
                            addRemoveFriendsButton.setText("Accept Friend Request");
                            declineRequestButton.setAlpha(1f);
                        }

                        else {
                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(userID)) {
                                        currentFriendsState = FRIENDS;
                                        addRemoveFriendsButton.setText("Unfriend This Person");
                                        displayUserLocationData();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    showSnackBarMessage("Something Went Wrong!");
                                }
                            });
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showSnackBarMessage("Something Went Wrong!");
                        mProgressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.dismiss();
                showSnackBarMessage("Failed To Retrieve User Information. Please Check Your Internet Connectivity.");
            }
        });

        addRemoveFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.show();

                //---------------------------NOT FRIENDS STATE--------------------------//

                if (currentFriendsState == NOT_FRIENDS) {
                    Log.d(TAG, "Current State Not Friends");
                    //Not Friends
                    addRemoveFriendsButton.setEnabled(false);

                    mFriendRequestSentDatabase.child(mCurrentUser.getUid()).child(userID).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendRequestReceivedDatabase.child(userID).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        addRemoveFriendsButton.setEnabled(true);
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Request Sent", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        currentFriendsState = REQUEST_SENT;
                                        addRemoveFriendsButton.setText("Cancel Friend Request");
                                    }
                                });
                            }

                            else {
                                addRemoveFriendsButton.setEnabled(true);
                            }
                            mProgressDialog.dismiss();
                        }
                    });
                }

                //---------------------------REQUEST SENT STATE--------------------------//


                else if (currentFriendsState == REQUEST_SENT) {
                    //Want To Cancel Friend Request

                    Log.d(TAG, "Request Already Sent, Trying To Cancel");

                    mFriendRequestSentDatabase.child(mCurrentUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mFriendRequestReceivedDatabase.child(userID).child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        currentFriendsState = NOT_FRIENDS;
                                        addRemoveFriendsButton.setText("Send Friend Request");
                                        showSnackBarMessage("Request Cancelled");
                                    }
                                    else {
                                        showSnackBarMessage("Something Went Wrong!");
                                    }
                                    mProgressDialog.dismiss();
                                }
                            });
                        }
                    });
                }

                //---------------------------REQUEST RECEIVED STATE--------------------------//

                else if (currentFriendsState == REQUEST_RECEIVED) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendMap = new HashMap();
                    friendMap.put("friends/" + mCurrentUser.getUid() + "/" + userID + "/date", currentDate);
                    friendMap.put("friends/" + userID + "/" + mCurrentUser.getUid() + "/date", currentDate);

                    final Map requestMap = new HashMap();
                    requestMap.put("friend_requests/received/" + mCurrentUser.getUid() + "/" + userID + "/request_type", null);
                    requestMap.put("friend_requests/sent/" + userID + "/" + mCurrentUser.getUid() + "/request_type", null);

                    //Adding Friends To Database

                    mRootDatabaseReference.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {

                                //Removing Requests From Database

                                mRootDatabaseReference.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            currentFriendsState = FRIENDS;
                                            displayUserLocationData();
                                            declineRequestButton.setAlpha(0);
                                            addRemoveFriendsButton.setText("Unfriend This Person");
                                        }
                                        else {
                                            showSnackBarMessage("Cannot Add The User Right Now, Please Try Again Later");
                                        }
                                    }
                                });
                            }
                            else {
                                showSnackBarMessage("Cannot Add The User Right Now, Please Try Again Later");
                            }
                        }
                    });
                    mProgressDialog.dismiss();
                }

                else if (currentFriendsState == FRIENDS) {
                    showSnackBarMessage("Trying To Unfriend The User");

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("friends/" + mCurrentUser.getUid() + "/" + userID + "/date", null);
                    unfriendMap.put("friends/" + userID + "/" + mCurrentUser.getUid() + "/date", null);

                    mRootDatabaseReference.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                showSnackBarMessage("User Removed From Your Friends");
                                currentFriendsState = NOT_FRIENDS;
                                hideUserLocationData();
                                addRemoveFriendsButton.setText("Add As Friend");
                            }
                            else {
                                showSnackBarMessage("Cannot Remove The User Right Now, Please Try Again Later");
                            }
                        }
                    });
                    mProgressDialog.dismiss();
                }

                mProgressDialog.dismiss();
            }
        });

        declineRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Decline Request
                showSnackBarMessage("Trying To Decline The User");
                mFriendRequestSentDatabase.child(userID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendRequestReceivedDatabase.child(mCurrentUser.getUid()).child(userID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                addRemoveFriendsButton.setEnabled(true);
                                currentFriendsState = NOT_FRIENDS;
                                declineRequestButton.setAlpha(0f);
                                addRemoveFriendsButton.setText("Send A Friend Request");
                                mProgressDialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }
    private void hideUserLocationData() {
        userLocationLayout.setVisibility(View.GONE);
    }

    private void displayUserLocationData() {
        userLocationLayout.setVisibility(View.VISIBLE);
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
                    String lastKnown = timeAgo.getTimeAgo(lastTime, getApplicationContext());

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

                    moveCamera(new LatLng(mapLat, mapLng), 16, displayName + "'s Location");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showSnackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(mMarkerOptions);
        Toast.makeText(this, "Map Is Ready", Toast.LENGTH_LONG).show();
    }

    private void moveCamera(LatLng latLng, float zoom, String addressLine) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        addMarkerTo(latLng, addressLine);
    }

    private void addMarkerTo(LatLng latLng, String addressLine) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(addressLine);

        mMap.clear();
        mMap.addMarker(options);
    }

}
