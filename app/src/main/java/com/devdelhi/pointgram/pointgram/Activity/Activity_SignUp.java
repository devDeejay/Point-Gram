package com.devdelhi.pointgram.pointgram.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.devdelhi.pointgram.pointgram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class Activity_SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField, namefield;
    private Button mRegisterButton;
    private String email, password, displayName;
    private String TAG = "DEEJAY";
    private android.support.v7.widget.Toolbar mToolbar;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__signup);

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        namefield = findViewById(R.id.display_name);
        mToolbar = findViewById(R.id.signin_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegisterButton = findViewById(R.id.signUpUserButton);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog.setTitle("Please Wait");
                mProgressDialog.setMessage("We are processing your request.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                email = emailField.getText().toString();
                password = passwordField.getText().toString();
                displayName = namefield.getText().toString();

                if(detailsCorrect(email, password, displayName))
                    signUpUser(email, password, displayName);
                else
                    showErrorMessage();
            }
        });

    }

    private boolean detailsCorrect(String email, String password, String displayName) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(displayName))
            return false;
        else
            return true;
    }

    private void showErrorMessage() {
        mProgressDialog.hide();
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Enter Your Email and Password.", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void signUpUser(String email, String password, final String displayName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, displayName);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mProgressDialog.hide();
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sign Up Failed. Maybe You Already Have An Account?", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        mProgressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            startActivity(new Intent(Activity_SignUp.this, Activity_Account_Settings.class));
            finish();
        }
    }

    private void updateUI(FirebaseUser currentUser, String displayName) {
        final FirebaseUser mDatabaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();
        String email = currentUser.getEmail();
        String name = displayName;
        String deviceToken = FirebaseInstanceId.getInstance().getToken();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users_database").child(uid); //Getting Reference To Users Child

        final Map locationMap = new HashMap();
        locationMap.put("lat", "0");
        locationMap.put("lng", "0");
        locationMap.put("alt", "0");
        locationMap.put("speed", "0");
        locationMap.put("provider", "NETWORK");
        locationMap.put("lastUpdate", ServerValue.TIMESTAMP);

        HashMap<String, String> usermap = new HashMap<>();
        usermap.put("user_name", displayName);
        usermap.put("user_email", email);
        usermap.put("user_status", "Hey There, I am on Point Gram.");
        usermap.put("user_image", "Default");
        usermap.put("user_thumbnail", "Default");
        usermap.put("device_token", deviceToken);

        mDatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    mDatabase.child("location").updateChildren(locationMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                Log.d(TAG, "Location Updated On Firebase");
                                startActivity(new Intent(Activity_SignUp.this, MainActivity.class));
                                mProgressDialog.dismiss();
                                finish();
                            }
                        }
                    });
                }

                else {
                    mProgressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "We Couldn't Save Your Details. Please, Check Your Internet Connectivity.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}
