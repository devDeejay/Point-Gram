package com.devdelhi.pointgram.pointgram.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devdelhi.pointgram.pointgram.model.friends;
import com.devdelhi.pointgram.pointgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_Friends extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseRecyclerAdapter adapter;
    private String name,status,image;
    private String date;
    private String TAG = "DEEJAY_FRIENDS";
    private ArrayList<String> usernames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        usernames = new ArrayList<>();
        mFriendsList = findViewById(R.id.all_friends_recyclerView);
        mToolbar = findViewById(R.id.friends_activity_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Your Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users_database");
        Log.d("HEY", mFriendsDatabase +"");

        mFriendsList.setLayoutManager(new LinearLayoutManager(this));

        Query query = mFriendsDatabase.limitToLast(10);

        Log.d("HEY", query+"");

        FirebaseRecyclerOptions<friends> options =
                new FirebaseRecyclerOptions.Builder<friends>()
                        .setQuery(query, friends.class)
                        .build();

        Log.d("HEY", options+"");

        adapter = new FirebaseRecyclerAdapter<friends, Activity_Friends.UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UserViewHolder holder, int position, @NonNull friends model) {

                date = model.getDate();

                holder.setDate(date);

                final String userID = getRef(position).getKey();

                mUsersDatabase.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child("user_name").getValue().toString();
                        status = dataSnapshot.child("user_status").getValue().toString();
                        image = dataSnapshot.child("user_thumbnail").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }

                        holder.setName(name);
                        holder.setStatus(status);
                        holder.setThumbnailImage(image, getApplicationContext());


                        Log.d(TAG, name);
                        Log.d(TAG, status);
                        Log.d(TAG, image);
                        Log.d(TAG, "Done!");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Activity_Friends.this, Activity_Profile.class);
                        intent.putExtra("UserID", userID);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public Activity_Friends.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item

                Log.d("HEY", "OnCreateViewHolder : " + "Working");

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_list_item_user, parent, false);

                return new Activity_Friends.UserViewHolder(view);
            }
        };

        mFriendsList.setAdapter(adapter);
        adapter.startListening();

        Intent intent = new Intent();
        intent.putExtra("FriendsList", usernames);
        setResult(2,intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        private String date;
        private String TAG = "DEEJAY_FRIENDS";

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameTV = mView.findViewById(R.id.nameTV);
            userNameTV.setText(name);
            Log.d(TAG, "Setting Status " + name);
        }

        public void setStatus(String user_stas) {
            TextView statusTextView = mView.findViewById(R.id.statusTV);
            statusTextView.setText(user_stas);
            Log.d(TAG, "Setting Status " + user_stas);
        }

        public void setThumbnailImage(String user_image, Context applicationContext) {
            CircleImageView circleImageView = mView.findViewById(R.id.profileImageView);
            Picasso.with(applicationContext).load(user_image).placeholder(R.drawable.avatar).into(circleImageView);

            Log.d(TAG, "Setting Satus " + user_image);

        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setUserOnline(String userOnline) {
            ImageView userOnlineView = mView.findViewById(R.id.onelineStatus);
            if (userOnline.equals("true")) {
                Log.d(TAG, "User Online " + userOnline);
                userOnlineView.setVisibility(View.VISIBLE);
            }
            else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
