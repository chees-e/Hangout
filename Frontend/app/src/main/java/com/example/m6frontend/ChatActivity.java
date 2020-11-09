package com.example.m6frontend;


import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
// import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {
    final static String TAG = "ChatActivity";
    private Button sendButton;

    //private RecyclerView userMessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        EditText userMessageInput = (EditText) findViewById(R.id.input_message);
        //TODO: Friend List
        sendButton = findViewById(R.id.send_message_button);

    }


}