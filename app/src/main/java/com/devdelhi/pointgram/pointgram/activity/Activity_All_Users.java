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
import android.widget.TextView;

import com.devdelhi.pointgram.pointgram.R;
import com.devdelhi.pointgram.pointgram.model.users_database;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_All_Users extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private DatabaseReference mUserDatabase;
    private FirebaseRecyclerAdapter adapter;
    private String name,status,image;
    private String TAG = "Friends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_);

        mUsersList = findViewById(R.id.recyclerView);
        mToolbar = findViewById(R.id.allusers_toolbars);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add More Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users_database");
        Log.d("HEY", mUserDatabase+"");

        mUsersList.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseDatabase.getInstance().getReference().child("users_database")
                .limitToLast(10);

        Log.d("HEY", query+"");

        FirebaseRecyclerOptions<users_database> options =
                new FirebaseRecyclerOptions.Builder<users_database>()
                        .setQuery(query, users_database.class)
                        .build();

        Log.d("HEY", options+"");

        adapter = new FirebaseRecyclerAdapter<users_database, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull users_database model) {
                name = model.getUser_name();
                status = model.getUser_status();
                image = model.getUser_thumbnail();

                holder.setName(name);
                holder.setStatus(status);
                holder.setThumbnailImage(image, getApplicationContext());

                final String userID = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Activity_All_Users.this, Activity_Profile.class);
                        intent.putExtra("UserID", userID);
                        startActivity(intent);
                    }
                });

                Log.d("HEY","OnBindViewHolder : " + model.getUser_name());
            }

            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item

                Log.d("HEY", "OnCreateViewHolder : " + "Working");

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_list_item_user, parent, false);

                return new UserViewHolder(view);
            }
        };

        mUsersList.setAdapter(adapter);
        adapter.startListening();
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

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameTV = mView.findViewById(R.id.nameTV);
            userNameTV.setText(name);

        }

        public void setStatus(String user_status) {
            TextView statusTextView = mView.findViewById(R.id.statusTV);
            statusTextView.setText(user_status);
        }

        public void setThumbnailImage(String user_status, Context applicationContext) {
            CircleImageView circleImageView = mView.findViewById(R.id.profileImageView);
            Picasso.with(applicationContext).load(user_status).placeholder(R.drawable.avatar).into(circleImageView);
        }
    }
}
