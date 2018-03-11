package com.devdelhi.pointgram.pointgram.Fragments;

import android.app.AlertDialog;
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
import com.devdelhi.pointgram.pointgram.Activity.Activity_Chat;
import com.devdelhi.pointgram.pointgram.Activity.Activity_Friends;
import com.devdelhi.pointgram.pointgram.Activity.Activity_Profile;
import com.devdelhi.pointgram.pointgram.Model.friends;
import com.devdelhi.pointgram.pointgram.Model.users_database;
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

public class Fragment_Chats extends Fragment {

    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseRecyclerAdapter adapter;
    private String name,status,image;
    private String date;
    private String TAG = "DEEJAY_FRIENDS";

    public Fragment_Chats() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        mFriendsList = view.findViewById(R.id.all_friends_recyclerView);
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users_database");
        mFriendsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        Query query = mFriendsDatabase.limitToLast(10);

        FirebaseRecyclerOptions<friends> options =
                new FirebaseRecyclerOptions.Builder<friends>()
                        .setQuery(query, friends.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<friends, Activity_Friends.UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Activity_Friends.UserViewHolder holder, int position, @NonNull friends model) {

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
                        holder.setThumbnailImage(image, getActivity().getApplicationContext());

                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), Activity_Chat.class);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userName", name);
                                intent.putExtra("userImage", image);
                                startActivity(intent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public Activity_Friends.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_list_item_user, parent, false);

                return new Activity_Friends.UserViewHolder(view);
            }
        };

        mFriendsList.setAdapter(adapter);
        adapter.startListening();

        return view;
    }
}
