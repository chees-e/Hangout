package com.example.m6frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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


public class FindEventActivity extends AppCompatActivity {

    private final String TAG = "FindEventActivity";
    // private final String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/getEvent";
    // RequestQueue queue;
    private int numEvents;
    private RecyclerView recyclerView;
    private EventRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<JSONObject> dataSet;
    private GoogleSignInAccount currentAccount;
    private boolean isLoading = false;
    private final int numLoad = 10;
    private final int maxEvents = 30;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_event);
        recyclerView =  findViewById(R.id.recyclerView);
        numEvents = 0;
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);

        int startEvents = 10;
        dataSet = initEventData(startEvents);


        initAdapter();
        initScrollListener();
    }

    private void initAdapter() {
        recyclerViewAdapter = new EventRecyclerViewAdapter(dataSet, this);
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
        recyclerViewAdapter.notifyItemInserted(dataSet.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dataSet.remove(dataSet.size() - 1);
                int scrollPosition = dataSet.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + numLoad;

                while (currentSize - 1 < nextLimit && currentSize - 1 < maxEvents) {
                    dataSet.add(new JSONObject());
                    try {
                        dataSet.get(currentSize).put("name","name" + numEvents);
                        dataSet.get(currentSize).put("desc","desc"+ numEvents);
                        dataSet.get(currentSize).put("location","location"+ numEvents);
                        dataSet.get(currentSize).put("start","start"+ numEvents);
                        dataSet.get(currentSize).put("end","end"+ numEvents);
                        dataSet.get(currentSize).put("attendees","attendees"+ numEvents);
                        dataSet.get(currentSize).put("ownerPicture", currentAccount.getPhotoUrl()); // TODO: get owner picture
                        numEvents++;
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


    private ArrayList<JSONObject> initEventData(int num) {
        final ArrayList<JSONObject> dataSet = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(FindEventActivity.this);
        String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/event/";

        //Getting all events
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        try {
                            JSONArray events = response.getJSONArray("events");
                            int length = response.getInt("length");

                            //stuff show up after a longgg time
                            Uri tempurl = currentAccount.getPhotoUrl();
                            for (int i = 0; i < length; i++) {
                                dataSet.add(new JSONObject());
                                dataSet.get(i).put("name", events.getJSONObject(i).getString("name"));
                                dataSet.get(i).put("desc", events.getJSONObject(i).getString("desc"));
                                dataSet.get(i).put("location", events.getJSONObject(i).getString("location"));
                                dataSet.get(i).put("start", events.getJSONObject(i).getString("start"));
                                dataSet.get(i).put("end", events.getJSONObject(i).getString("end"));
                                dataSet.get(i).put("attendees", "TODO");
                                dataSet.get(i).put("ownerPicture", tempurl); // TODO: get owner picture
                                numEvents++;
                                Log.d(TAG, "event added");

                            }
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

        return dataSet;

        /*
        // Server code
        final JSONObject data = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            data.put("name",response.getString("name"));
                            data.put("id",response.getString("id"));
                            data.put("desc",response.getString("desc"));
                            data.put("start",response.getString("start"));
                            data.put("end",response.getString("end"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, response.toString());
                        Log.d(TAG, "Event info received");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e(TAG, "Event info can not be received");
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

        return data;
        */

    }
}