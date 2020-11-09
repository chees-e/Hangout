package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

// import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

//TODO: CONNECT WITH BACKEND GETUSER
public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        List<String> friendList;
        RecyclerView rvFriends = (RecyclerView) findViewById(R.id.rvFriends);

        friendList = new ArrayList<>();
        friendList.add("Alex");
        friendList.add("Bob");
        friendList.add("Casey");
        friendList.add("David");

        FriendsAdapter adapter = new FriendsAdapter(friendList);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));

        // FloatingActionButton addFriendButton = findViewById(R.id.fab2);
        //TODO: ADD FRIEND
    }
}