package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class ViewProfileActivity extends AppCompatActivity {

    private Intent intent;
    private String activity;
    private String friendid;
    // private static final String TAG = "ProfileSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        activity = intent.getStringExtra("activity");
        // TODO: get profile picture
        GoogleSignInAccount currentAccount =  GoogleSignIn.getLastSignedInAccount(this);
        friendid = intent.getStringExtra("friendid");

        if (activity.equals("friends")) {
            setContentView(R.layout.view_friend_profile);
            Button deleteFriend = findViewById(R.id.deleteFriendButton);
            deleteFriend.setOnClickListener(v -> deleteFriendConfirm());

        } else {
            setContentView(R.layout.view_user_profile);
            Button profileViewConfirm = findViewById(R.id.profileViewConfirm);
            profileViewConfirm.setOnClickListener(v -> addFriendConfirm());
        }
        ImageView profilePicture = findViewById(R.id.profileViewPicture);
        Glide.with(this)
                .load(currentAccount.getPhotoUrl())
                .circleCrop()
                .into(profilePicture);
    }

    private void addFriendConfirm() {
        AddFriendConfirmDialog dialog = new AddFriendConfirmDialog();
        Bundle args = new Bundle();
        args.putString("friendid", friendid);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "add friend confirm button");

    }

    private void deleteFriendConfirm() {
        DeleteFriendConfirmDialog dialog = new DeleteFriendConfirmDialog();
        dialog.show(getSupportFragmentManager(), "delete friend confirm button");
    }

}