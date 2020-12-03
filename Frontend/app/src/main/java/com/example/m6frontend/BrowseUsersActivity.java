package com.example.m6frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
    // private final String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/getEvent";
    // RequestQueue queue;
    private int numUsers;
    private RecyclerView recyclerView;
    private UserRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<JSONObject> dataSet;
    private GoogleSignInAccount currentAccount;
    private boolean isLoading = false;
    private final int numLoad = 10;
    private final int maxUsers = 30;
    private String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_users);
        recyclerView = findViewById(R.id.userRecyclerView);
        numUsers = 0;
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);
        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        dataSet = initUserData();
    }

    private void initAdapter() {
        recyclerViewAdapter = new UserRecyclerViewAdapter(dataSet, this, activity );
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
                    loadMoreEvents();
                    isLoading = true;
                }

            }
        });
    }

    private void loadMoreEvents() {
        dataSet.add(null);
        recyclerView.post(new Runnable() {
                              @Override
                              public void run() {
                                  recyclerViewAdapter.notifyItemInserted(dataSet.size() - 1);
                              }
                          });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dataSet.remove(dataSet.size() - 1);
                int scrollPosition = dataSet.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + numLoad;

                while (currentSize - 1 < nextLimit && currentSize - 1 < maxUsers) {
                    dataSet.add(new JSONObject());
                    try {

                        dataSet.get(currentSize).put("name","name" + numUsers);
                        if (activity.equals("friends")) {
                            dataSet.get(currentSize).put("email", "email" + numUsers);
                            dataSet.get(currentSize).put("location","location"+ numUsers);
                        }

                        // TODO: get user picture
                        dataSet.get(currentSize).put("ownerPicture", currentAccount.getPhotoUrl());
                        numUsers++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    currentSize++;
                }
                recyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;

            }
        }, 200);
    }


    private ArrayList<JSONObject> initUserData() {
        final ArrayList<JSONObject> _dataSet = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(BrowseUsersActivity.this);

        // TODO: get description
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
                                // TODO: Handle error

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
                                        // TODO: get location
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
                                // TODO: Handle error

                            }
                        });
                requestQueue.add(jsonObjectRequest);
                requestQueue.start();
            }

        return _dataSet;

    }
}

