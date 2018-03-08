package com.devdelhi.pointgram.pointgram.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.devdelhi.pointgram.pointgram.Activity.Activity_Create_Alarm;
import com.devdelhi.pointgram.pointgram.Model.alarms_database;
import com.devdelhi.pointgram.pointgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Fragment_Alarms extends Fragment {

    private RecyclerView mUsersList;
    private DatabaseReference mUserDatabase;
    private FirebaseRecyclerAdapter adapter;
    private String name,description;
    private Double lat, lng;
    private int range;
    private String place;
    private String TAG = "DJ";
    private String userName;

    public Fragment_Alarms() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_alarms, container, false);

        userName = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUsersList = view.findViewById(R.id.alarmsRecyclerView);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("alarms_database").child(userName);

        FloatingActionButton fab =  view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent createAlarmIntent = new Intent(getActivity(),Activity_Create_Alarm.class);
                getActivity().startActivity(createAlarmIntent);

            }
        });

        mUsersList.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = FirebaseDatabase.getInstance().getReference().child("alarms_database").child(userName)
                .limitToLast(10);

        FirebaseRecyclerOptions<alarms_database> options =
                new FirebaseRecyclerOptions.Builder<alarms_database>()
                        .setQuery(query, alarms_database.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<alarms_database, Fragment_Alarms.UserViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull alarms_database model) {

                name = model.getName();
                description = model.getDescription();
                lat = model.getLat();
                lng = model.getLng();
                place = model.getNameOfPlace();
                range = model.getRange();

                Log.d(TAG, name+"");
                Log.d(TAG, description+"");
                Log.d(TAG, place+"");
                Log.d(TAG, range+"");

                holder.setName(name);
                holder.setDescription(description);
                holder.setPlaceName(place);
                holder.setRange(range);

                final String userID = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
/*
                        Intent intent = new Intent(getActivity(), Activity_PreviewAlarm.class);
                        intent.putExtra("Mode", "Change Alarm");
                        startActivity(intent);
*/
                    }
                });
            }

            @Override
            public Fragment_Alarms.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_list_item_alarm, parent, false);

                return new Fragment_Alarms.UserViewHolder(view);
            }
        };

        mUsersList.setAdapter(adapter);
        adapter.startListening();

        return view;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView alarmNameTV = mView.findViewById(R.id.alarm_title_item);
            alarmNameTV.setText(name);
        }

        public void setDescription(String desc) {
            TextView alarmDesc = mView.findViewById(R.id.alarm_Description_item);
            alarmDesc.setText(desc);
        }

        public void setPlaceName(String s) {
            TextView alarmDesc = mView.findViewById(R.id.alarm_location_name_item);
            alarmDesc.setText(s);
        }

        public void setRange(int range) {
            TextView alarmRadius = mView.findViewById(R.id.alarm_radius_item);
            alarmRadius.setText("Radius Set : " + range);
        }
    }
}
