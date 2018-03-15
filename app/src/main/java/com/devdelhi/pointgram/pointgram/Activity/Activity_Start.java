package com.devdelhi.pointgram.pointgram.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;

import com.devdelhi.pointgram.pointgram.R;


public class Activity_Start extends AppCompatActivity {

    private Button mLoginButton, mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__start);

        mLoginButton = findViewById(R.id.signInActivityButton);
        mSignUpButton = findViewById(R.id.registerActivityButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_Start.this, Activity_Login.class));
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Activity_Start.this, Activity_SignUp.class));
            }
        });
    }
}
