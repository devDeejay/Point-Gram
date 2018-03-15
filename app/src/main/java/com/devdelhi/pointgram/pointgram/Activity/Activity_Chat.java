package com.devdelhi.pointgram.pointgram.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devdelhi.pointgram.pointgram.Model.messages;
import com.devdelhi.pointgram.pointgram.R;
import com.devdelhi.pointgram.pointgram.Support.TimeAgo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    private String TAG = "ChatActivity";
    private String mCurrentUserID;
    private String messageText;

    private ImageButton mChatSendButton;
    private EditText mChatMessageView;
    private DatabaseReference mMessageDatabaseForUser;

    private RecyclerView mMessagesList;

    private FirebaseRecyclerAdapter messageAdapter;

    int posn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int TOTAL_ITEMS_TO_LOAD = 100;
    private int mCurrentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__chat);

        mChatUser = getIntent().getStringExtra("userID");
        mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (mCurrentUserID == null) {
            finish();
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Please Wait While We Load Your Chat");
        progressDialog.show();

        Log.d(TAG, "Current User " + mCurrentUserID);
        Log.d(TAG, "Chat User " + mChatUser);

        mChatoolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatoolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipeMessageRefreshLayout);

        mMessagesList = findViewById(R.id.messagesList);

        mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mMessageDatabaseForUser = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUserID).child(mChatUser);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_layout, null);

        actionBar.setCustomView(action_bar_view);

        mTitleView = findViewById(R.id.chatUsernameChatbar);
        mLastSeenView = findViewById(R.id.chatUserOnlineStatus);
        mProfileImage = findViewById(R.id.chatCustomBarImage);

        mChatSendButton = findViewById(R.id.sendTextIV);
        mChatMessageView = findViewById(R.id.chatMessageET);


        Query query = mMessageDatabaseForUser.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        Log.d(TAG, "Query " + query);

        FirebaseRecyclerOptions<messages> options =
                new FirebaseRecyclerOptions.Builder<messages>()
                        .setQuery(query, messages.class)
                        .build();

        Log.d(TAG, "Options " + options);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        messageAdapter = new FirebaseRecyclerAdapter<messages, Activity_Chat.MessageViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder holder, int position, messages model) {

                messageText = model.getMessage();
                Boolean seen = model.getSeen();
                String type = model.getType();
                long time = model.getTime();
                String from = model.getFrom();

                if (from != null) {
                    if (from.equals(mCurrentUserID)) {
                        holder.setMessageForSender(messageText, seen, type, time, getApplicationContext());
                    }else if (from.equals(mChatUser)) {
                        holder.setMessageForChatUser(messageText, seen, type, time, getApplicationContext());
                    }
                }

                Log.d(TAG, "Message : " + messageText);
                Log.d(TAG, "Seen : " + seen);
                Log.d(TAG, "Type : " + type);
                Log.d(TAG, "Time : " + time);

                posn = position;
            }

            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_message_item_layout, parent, false);

                return new Activity_Chat.MessageViewHolder(view);
            }

        };

        mMessagesList.scrollToPosition(posn);

        //loadMessages();

        //Setting Up Chat Activity

        mRootDatabaseReference.child("users_database").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChatUserName = dataSnapshot.child("user_name").getValue().toString();
                userImage = dataSnapshot.child("user_thumbnail").getValue().toString();
                String online = dataSnapshot.child("online").getValue().toString();

                if (online.equals("true")) {
                    mLastSeenView.setText("Online");
                }
                else {
                    TimeAgo timeAgo = new TimeAgo();
                    long lastTime = Long.parseLong(online);

                    String lastSeen = timeAgo.getTimeAgo(lastTime, getApplicationContext());

                    mLastSeenView.setText(lastSeen);
                }

                mTitleView.setText(mChatUserName);
                Picasso.with(Activity_Chat.this).load(userImage).placeholder(R.drawable.avatar).into(mProfileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Starting To Setup Chat

        mRootDatabaseReference.child("Chat").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatUser)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timeStamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserID + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserID, chatAddMap);

                    mRootDatabaseReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d(TAG, "Something Went Wrong");
                                finish();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        progressDialog.dismiss();

        mChatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Chat.this, Activity_Profile.class);
                intent.putExtra("UserID", mChatUser);
                startActivity(intent);
            }
        });


        mMessagesList.setAdapter(messageAdapter);
        mMessagesList.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter.startListening();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        TextView senderMessageTV;
        TextView chatUserMessageTV;

        CircleImageView chatUserProfileImage;

        TextView senderMessageTimeTV;
        TextView chatUserMessageTimeTV;

        LinearLayout senderLinearLayout;
        LinearLayout chatUserLinearLayout;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            senderMessageTV = mView.findViewById(R.id.senderProfileText);
            chatUserMessageTV = mView.findViewById(R.id.chatUserTextMessage);

            senderMessageTimeTV = mView.findViewById(R.id.senderProfileTextTime);
            chatUserMessageTimeTV = mView.findViewById(R.id.chatUserTextTime);

            chatUserProfileImage = mView.findViewById(R.id.chatUserProfileImage);

            senderLinearLayout = mView.findViewById(R.id.senderLinearLayout);
            chatUserLinearLayout = mView.findViewById(R.id.chatUserLinearLayout);

        }

        public void setMessageForSender(String messageText, Boolean seen, String type, long time, Context applicationContext) {
            senderLinearLayout.setVisibility(View.VISIBLE);
            chatUserLinearLayout.setVisibility(View.GONE);
            senderMessageTV.setText(messageText);/*
            Calendar c = Calendar.getInstance();
            senderMessageTimeTV.setText(c.getTime()+"");*/
        }

        public void setMessageForChatUser(String messageText, Boolean seen, String type, long time, Context applicationContext) {
            chatUserLinearLayout.setVisibility(View.VISIBLE);
            senderLinearLayout.setVisibility(View.GONE);
            chatUserMessageTV.setText(messageText);/*
            Calendar c = Calendar.getInstance();
            chatUserMessageTimeTV.setText(c.getTime()+"");*/
            Picasso.with(applicationContext).load(userImage).placeholder(R.drawable.avatar).into(chatUserProfileImage);
        }
    }

    private void sendMessage() {
        String message = mChatMessageView.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {

            String currentUserRef = "messages/" + mCurrentUserID + "/" + mChatUser;
            String chatUserRef = "messages/" + mChatUser + "/" + mCurrentUserID;

            DatabaseReference userMessagePush = mRootDatabaseReference.child("messages").child(mCurrentUserID).child(mChatUser).push();
            String pushID = userMessagePush.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserID);

            Map messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + pushID, messageMap);
            messageUserMap.put(chatUserRef + "/"+pushID, messageMap);

            mRootDatabaseReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d(TAG, "Something Went Wrong" + databaseError.getMessage().toString());
                    }
                }
            });
        }
        mChatMessageView.setText("");
    }

}
