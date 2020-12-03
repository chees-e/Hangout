package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;


public class ViewProfileActivity extends AppCompatActivity {

    private String friendid;
    private final String TAG = "ViewProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String activity = intent.getStringExtra("activity");


        friendid = intent.getStringExtra("friendid");
        String friendname = intent.getStringExtra("friendname");
        String friendpfp = intent.getStringExtra("friendpfp");




        if (activity.equals("friends")) {
            setContentView(R.layout.view_friend_profile);

            RequestQueue requestQueue = Volley.newRequestQueue(ViewProfileActivity.this);

            String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/user/" + friendid;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                System.out.println(response.toString());
                                int friendsl = response.getJSONArray("friends").length();
                                int eventsl = response.getJSONArray("events").length();

                                Button deleteFriend = findViewById(R.id.deleteFriendButton);
                                deleteFriend.setOnClickListener(v -> deleteFriendConfirm());
                                TextView profileEmail = (TextView) findViewById(R.id.profileEmail);
                                TextView profileFriendsCount = (TextView) findViewById(R.id.friend_friends_count);
                                TextView profileEventsCount = (TextView) findViewById(R.id.friend_events_count);
                                profileFriendsCount.setText("" + friendsl);
                                profileEventsCount.setText("" + eventsl);


                                profileEmail.setText(friendid);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "error");

                        }
                    });
            requestQueue.add(jsonObjectRequest);
            requestQueue.start();
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
        Bundle args = new Bundle();
        args.putString("friendid", friendid);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "delete friend confirm button");
    }

}