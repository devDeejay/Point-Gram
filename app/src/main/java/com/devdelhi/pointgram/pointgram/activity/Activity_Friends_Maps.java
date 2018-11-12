package com.devdelhi.pointgram.pointgram.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.devdelhi.pointgram.pointgram.R;

public class Activity_Friends_Maps extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__friends__maps);

        startActivity(new Intent(Activity_Friends_Maps.this, Activity_Maps.class));
        finish();
    }
}
