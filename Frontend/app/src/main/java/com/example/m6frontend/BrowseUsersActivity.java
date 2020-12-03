package com.example.m6frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;


public class BrowseUsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<JSONObject> dataSet;
    private GoogleSignInAccount currentAccount;
    private boolean isLoading = false;
    private String activity;
    private final String TAG = "BrowseUsersActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_users);
        recyclerView = findViewById(R.id.userRecyclerView);
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);
        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        dataSet = initUserData();
    }

    private void initAdapter() {
        UserRecyclerViewAdapter recyclerViewAdapter = new UserRecyclerViewAdapter(dataSet, this, activity);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();


                if (!isLoading && linearLayoutManager != null
                        && linearLayoutManager.findLastCompletelyVisibleItemPosition() == dataSet.size() - 1) {
                    isLoading = true;
                }

            }
        });
    }


    private ArrayList<JSONObject> initUserData() {
        final ArrayList<JSONObject> _dataSet = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(BrowseUsersActivity.this);


        if (activity.equals("users")) {
                String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/user/" + currentAccount.getEmail() + "/findfriends";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray friends = response.getJSONArray("users");

                                    for (int i = 0; i < friends.length(); i++) {
                                        _dataSet.add(new JSONObject());
                                        _dataSet.get(i).put("name", friends.getJSONObject(i).getString("name"));
                                        _dataSet.get(i).put("email", friends.getJSONObject(i).getString("id"));
                                        _dataSet.get(i).put("ownerPicture", friends.getJSONObject(i).getString("pfp"));
                                    }
                                    initAdapter();
                                    initScrollListener();
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
                String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/user/" + currentAccount.getEmail();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray friends;
                                    if (activity.equals("friends")) {
                                        friends = response.getJSONArray("friends");
                                    } else {
                                        friends = response.getJSONArray("requestin");
                                    }

                                    for (int i = 0; i < friends.length(); i++) {

                                        _dataSet.add(new JSONObject());
                                        _dataSet.get(i).put("name", friends.getJSONObject(i).getString("name"));
                                        _dataSet.get(i).put("email", friends.getJSONObject(i).getString("id"));
                                        _dataSet.get(i).put("ownerPicture", friends.getJSONObject(i).getString("pfp"));
                                    }
                                    initAdapter();
                                    initScrollListener();
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
            }

        return _dataSet;

    }
}

