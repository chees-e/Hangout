package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class ViewProfileActivity extends AppCompatActivity {

    Intent intent;
    String activity;

    private static final String TAG = "ProfileSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        activity = intent.getStringExtra("activity");
        // TODO: get profile picture
        GoogleSignInAccount currentAccount =  GoogleSignIn.getLastSignedInAccount(this);


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
        dialog.show(getSupportFragmentManager(), "add friend confirm button");
    }

    private void deleteFriendConfirm() {
        DeleteFriendConfirmDialog dialog = new DeleteFriendConfirmDialog();
        dialog.show(getSupportFragmentManager(), "delete friend confirm button");
    }

}