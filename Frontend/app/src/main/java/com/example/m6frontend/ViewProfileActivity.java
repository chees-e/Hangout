package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewProfileActivity extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    Intent intent;
    String activity;

    private static final String TAG = "ProfileSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        activity = intent.getStringExtra("activity");

        if (activity.equals("friends")) {
            setContentView(R.layout.view_friend_profile);
            Button deleteFriend = findViewById(R.id.deleteFriendButton);
            deleteFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriendConfirm();
                }
            });
        } else {
            setContentView(R.layout.view_user_profile);
            Button profileViewConfirm = findViewById(R.id.profileViewConfirm);
            profileViewConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFriendConfirm();
                }
            });
        }



    
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