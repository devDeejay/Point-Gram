package com.devdelhi.pointgram.pointgram.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devdelhi.pointgram.pointgram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Activity_Account_Settings extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;

    private CircleImageView display_pic_IV;
    private TextView displayNameTV, statusTV;
    private String TAG = "DEEJAY";
    private String current_uid;
    private static final int GALLERY_PICK = 1;
    private String statusString;
    private Button changeStatusButton, changePictureButton;
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__account__settings);

        changeStatusButton = findViewById(R.id.changeStatusButton);
        changePictureButton = findViewById(R.id.changeImageButton);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        current_uid = mCurrentUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users_database").child(current_uid);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please Wait");
        mProgressDialog.setMessage("We are getting your Name, Image And Status.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        display_pic_IV = findViewById(R.id.user_display_image);
        displayNameTV = findViewById(R.id.user_display_name);
        statusTV = findViewById(R.id.user_display_status);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();
                final String thumbnail = dataSnapshot.child("user_thumbnail").getValue().toString();

                Log.d(TAG, "Retrieved Name is " + name);
                Log.d(TAG, "Retrieved status is " + status);

                statusString = status;
                displayNameTV.setText(name);
                statusTV.setText(status);
                Picasso.with(Activity_Account_Settings.this).load(R.drawable.avatar).placeholder(R.drawable.avatar).into(display_pic_IV);

                if (!image.equals("default")) {
                    Picasso.with(Activity_Account_Settings.this).load(thumbnail).placeholder(R.drawable.avatar).into(display_pic_IV);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressDialog.hide();
            }
        });

        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Account_Settings.this, Activity_Status.class);
                intent.putExtra("Status", statusString);
                startActivity(intent);
            }
        });

        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(galleryIntent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        mProgressDialog.dismiss();
    }

    private boolean isUserSignedIn() {
        boolean isSignedin = false;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            isSignedin = true;
        }
        return isSignedin;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageURI = data.getData();
            CropImage.activity()
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(Activity_Account_Settings.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog.setTitle("Please Wait");
                mProgressDialog.setMessage("We are Changing Your Picture.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                final File thumbFilePath = new File(resultUri.getPath());

                Bitmap thumbBitmap = null;
                try {
                    thumbBitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumbFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("users_profile_image").child(current_uid+".jpg");
                final StorageReference thumbfilepathRef = mImageStorage.child("users_profile_image").child("thumbs").child(current_uid+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            final String downloadURL = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumbfilepathRef.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumbDownloadURL = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {

                                        Map updateHashMap = new HashMap<>();
                                        updateHashMap.put("user_image", downloadURL);
                                        updateHashMap.put("user_thumbnail", thumbDownloadURL);

                                        Log.d(TAG, downloadURL);
                                        Log.d(TAG, thumbDownloadURL);

                                        mDatabaseReference.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Picture Saved.", Snackbar.LENGTH_LONG);
                                                    snackbar.show();
                                                }
                                                else {
                                                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Failed To Save Picture.", Snackbar.LENGTH_LONG);
                                                    snackbar.show();
                                                }
                                            }
                                        });
                                    }
                                    else {

                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Failed To Save Picture.", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                }
                            });


                        }
                        else {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Failed To Save Picture.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        mProgressDialog.dismiss();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        */
    }
}
