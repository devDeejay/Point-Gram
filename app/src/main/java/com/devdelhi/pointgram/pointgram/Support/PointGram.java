package com.devdelhi.pointgram.pointgram.Support;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


public class PointGram extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
