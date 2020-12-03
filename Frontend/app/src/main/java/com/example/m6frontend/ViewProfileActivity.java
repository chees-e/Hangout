package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;



public class ViewProfileActivity extends AppCompatActivity {

    private String friendid;
    // private static final String TAG = "ProfileSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String activity = intent.getStringExtra("activity");
        // TODO: get profile picture

        //GoogleSignInAccount currentAccount =  GoogleSignIn.getLastSignedInAccount(this);
        friendid = intent.getStringExtra("friendid");
        String friendname = intent.getStringExtra("friendname");
        String friendpfp = intent.getStringExtra("friendpfp");
        System.out.println("AAAAAAAA" + friendid);
        System.out.println("AAAAAAAA" + friendname);
        System.out.println("AAAAAAAA" + friendpfp);

        //TextView profileDescription = findViewById(R.id.profileDescription);
        // TODO: get profile description

        if (activity.equals("friends")) {
            setContentView(R.layout.view_friend_profile);
            Button deleteFriend = findViewById(R.id.deleteFriendButton);
            deleteFriend.setOnClickListener(v -> deleteFriendConfirm());
            TextView profileEmail = (TextView) findViewById(R.id.profileEmail);
            //TextView profileLocation = (TextView) findViewById(R.id.profileLocation);
            profileEmail.setText(friendid);
        } else {
            setContentView(R.layout.view_user_profile);
            Button profileViewConfirm = findViewById(R.id.profileViewConfirm);
            profileViewConfirm.setOnClickListener(v -> addFriendConfirm());
        }
        TextView profileName = (TextView) findViewById(R.id.profileName);
        profileName.setText(friendname);
        ImageView profilePicture = (ImageView) findViewById(R.id.profileViewPicture);
        Glide.with(this)
                .load(friendpfp)
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