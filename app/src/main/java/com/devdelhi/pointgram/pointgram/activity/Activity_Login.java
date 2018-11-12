package com.devdelhi.pointgram.pointgram.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.devdelhi.pointgram.pointgram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Activity_Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField, passwordField;
    private Button mLoginButton;
    private String email, password, device_token;
    private String TAG = "DEEJAY";
    private ProgressDialog mProgressDialog;
    private DatabaseReference mUserDatabase;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__login);

        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.signUpUserButton);
        device_token = FirebaseInstanceId.getInstance().getToken();
        mUserDatabase = FirebaseDatabase.getInstance().getReference();

        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog.setTitle("Please Wait");
                mProgressDialog.setMessage("We are processing your request.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                email = emailField.getText().toString();
                password = passwordField.getText().toString();

                if(detailsCorrect(email, password))
                    loginUser(email, password);
                else
                    showErrorMessage();
            }
        });
    }


    private boolean detailsCorrect(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            return false;
        else
            return true;
    }

    private void showErrorMessage() {
        mProgressDialog.hide();
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Enter Your Email and Password.", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mProgressDialog.hide();

                            String currentUserId = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            mUserDatabase.child("users_database").child(currentUserId).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    takeUserToMainActivity(user);
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            mProgressDialog.hide();
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Login Failed.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            takeUserToMainActivity(currentUser);
        }
    }

    private void takeUserToMainActivity(FirebaseUser currentUser) {
        startActivity(new Intent(Activity_Login.this, MainActivity.class));
        finish();
    }

}
