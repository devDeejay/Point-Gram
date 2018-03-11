package com.devdelhi.pointgram.pointgram.Activity;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.devdelhi.pointgram.pointgram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_Chat extends AppCompatActivity {

    private String mChatUser;
    private Toolbar mChatoolbar;
    private String mChatUserName;
    private String userImage;

    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;

    private DatabaseReference mRootDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__chat);

        mChatUser = getIntent().getStringExtra("userID");
        mChatUserName = getIntent().getStringExtra("userName");
        userImage = getIntent().getStringExtra("userImage");

        mChatoolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_layout, null);

        actionBar.setCustomView(action_bar_view);

        mTitleView = findViewById(R.id.chatUsernameChatbar);
        mLastSeenView = findViewById(R.id.chatUserOnlineStatus);
        mProfileImage = findViewById(R.id.chatCustomBarImage);

        mTitleView.setText(mChatUserName);

        mRootDatabaseReference.child("users_database").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").toString();
                String image = dataSnapshot.child("users_image").toString();
                if (online.equals("true")) {
                    mLastSeenView.setText("Online");
                }
                else {
                    mLastSeenView.setText(online);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
