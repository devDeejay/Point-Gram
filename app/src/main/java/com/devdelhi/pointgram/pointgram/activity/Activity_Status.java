package com.devdelhi.pointgram.pointgram.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.devdelhi.pointgram.pointgram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_Status extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText statusInput;
    private Button submit_status_button;
    private String statusString;

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__status);

        Intent i = getIntent();
        statusString = i.getStringExtra("user_status");

        mToolbar = findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Your Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        statusInput = findViewById(R.id.status_input);
        statusInput.setText(statusString);
        submit_status_button = findViewById(R.id.submit_status_button);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users_database").child(uid);

        submit_status_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusString = statusInput.getText().toString();
                if (!TextUtils.isEmpty(statusString)) {
                    mProgressDialog.setTitle("Please Wait");
                    mProgressDialog.setMessage("We are Chaning Your Status.");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    postStatusToDatabase(statusString);
                }
                else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Sorry, But You Cannot Leave Your Status Empty. Check Your Internet Connectivity.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }

    private void postStatusToDatabase(String statusString) {
        mDatabase.child("user_status").setValue(statusString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mProgressDialog.dismiss();
                    finish();
                }
                else {
                    mProgressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "We Were Unable To Change Your Status. Check Your Internet Connectivity.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }
}
