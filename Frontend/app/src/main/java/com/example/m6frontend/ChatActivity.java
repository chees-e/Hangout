package com.example.m6frontend;


import android.app.Application;
import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import sdk.chat.app.firebase.ChatSDKFirebase;
import sdk.chat.core.session.ChatSDK;
// import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Button sendButton;
        //setContentView(R.layout.activity_chat);
        // EditText userMessageInput = (EditText) findViewById(R.id.input_message);
        //TODO: Friend List
        //sendButton = findViewById(R.id.send_message_button);

        try {
            ChatSDKFirebase.quickStart(this, "pre", "", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChatSDK.ui().startSplashScreenActivity(this);
    }


}