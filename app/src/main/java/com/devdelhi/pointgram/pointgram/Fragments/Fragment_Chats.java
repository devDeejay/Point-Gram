package com.devdelhi.pointgram.pointgram.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devdelhi.pointgram.pointgram.Activity.Activity_All_Users;
import com.devdelhi.pointgram.pointgram.Activity.Activity_Profile;
import com.devdelhi.pointgram.pointgram.Model.users_database;
import com.devdelhi.pointgram.pointgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Fragment_Chats extends Fragment {

    private RecyclerView mUsersList;
    private DatabaseReference mUserDatabase;
    private FirebaseRecyclerAdapter adapter;
    private String name,status,image;

    public Fragment_Chats() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        mUsersList = view.findViewById(R.id.recyclerView);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users_database");
        Log.d("HEY", mUserDatabase+"");

        mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = FirebaseDatabase.getInstance().getReference().child("users_database")
                .limitToLast(10);

        Log.d("HEY", query+"");

        FirebaseRecyclerOptions<users_database> options =
                new FirebaseRecyclerOptions.Builder<users_database>()
                        .setQuery(query, users_database.class)
                        .build();

        Log.d("HEY", options+"");

        adapter = new FirebaseRecyclerAdapter<users_database, Activity_All_Users.UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Activity_All_Users.UserViewHolder holder, int position, @NonNull users_database model) {
                name = model.getUser_name();
                status = model.getUser_status();
                image = model.getUser_thumbnail();

                holder.setName(name);
                holder.setStatus(status);
                holder.setThumbnailImage(image, getActivity().getApplicationContext());

                final String userID = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), Activity_Profile.class);
                        intent.putExtra("UserID", userID);
                        startActivity(intent);
                    }
                });

                Log.d("HEY","OnBindViewHolder : " + model.getUser_name());
            }

            @Override
            public Activity_All_Users.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item

                Log.d("HEY", "OnCreateViewHolder : " + "Working");

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_list_item_user, parent, false);

                return new Activity_All_Users.UserViewHolder(view);
            }
        };

        mUsersList.setAdapter(adapter);
        adapter.startListening();

        return view;
    }
}
