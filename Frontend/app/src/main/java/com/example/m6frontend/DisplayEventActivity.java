package com.example.m6frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DisplayEventActivity extends AppCompatActivity {

    private final String TAG = "DisplayEventActivity";
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
    private String activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_event);
        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        recyclerView =  findViewById(R.id.recyclerView);
        numEvents = 0;
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);


        dataSet = initEventData();

    }

    private void initAdapter() {

        recyclerViewAdapter = new EventRecyclerViewAdapter(dataSet, this, activity);
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


    private ArrayList<JSONObject> initEventData() {
        final ArrayList<JSONObject> _dataSet = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(DisplayEventActivity.this);
        //This url needs to be changed so that it gets the events that users are not in
        //Or make the response include whether the user has already participated or not so that
        //it can be displayed differently
        //I will work on it so DW about this part

        //TODO debug

        if (activity.equals("myEvent")) {
            String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/user/" + currentAccount.getEmail() + "/event/";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //Separated into the events you are hosting and events you are attending
                                //Put in the same data set for now
                                JSONArray hevents = response.getJSONArray("host");
                                JSONArray aevents = response.getJSONArray("attendee");

                                System.out.println(response.toString());

                                for (int i = 0; i < hevents.length(); i++) {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Date startdate = format.parse(hevents.getJSONObject(i).getString("start").substring(0, 10) + " " + hevents.getJSONObject(i).getString("start").substring(11, 17));
                                    Date enddate = format.parse(hevents.getJSONObject(i).getString("end").substring(0, 10) + " " + hevents.getJSONObject(i).getString("end").substring(11, 17));

                                    String startstr = startdate.toString() + " - ";
                                    String endstr = enddate.toString();

                                    _dataSet.add(new JSONObject());
                                    _dataSet.get(i).put("name", hevents.getJSONObject(i).getString("name"));
                                    _dataSet.get(i).put("host", hevents.getJSONObject(i).getString("host"));
                                    _dataSet.get(i).put("desc", hevents.getJSONObject(i).getString("desc"));
                                    _dataSet.get(i).put("location", "Location: " + hevents.getJSONObject(i).getString("location"));
                                    _dataSet.get(i).put("start", startstr);
                                    _dataSet.get(i).put("end", endstr);
                                    _dataSet.get(i).put("attendees", hevents.getJSONObject(i).getString("attendees"));
                                    _dataSet.get(i).put("ownerPicture", currentAccount.getPhotoUrl());
                                    numEvents++;
                                    Log.d(TAG, "event added");

                                }

                                for (int i = 0; i < aevents.length(); i++) {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Date startdate = format.parse(aevents.getJSONObject(i).getString("start").substring(0, 10) + " " + aevents.getJSONObject(i).getString("start").substring(11, 17));
                                    Date enddate = format.parse(aevents.getJSONObject(i).getString("end").substring(0, 10) + " " + aevents.getJSONObject(i).getString("end").substring(11, 17));

                                    String startstr = startdate.toString() + " - ";
                                    String endstr = enddate.toString();

                                    _dataSet.add(new JSONObject());
                                    _dataSet.get(i).put("name", aevents.getJSONObject(i).getString("name"));
                                    _dataSet.get(i).put("host", aevents.getJSONObject(i).getString("host"));
                                    _dataSet.get(i).put("desc", aevents.getJSONObject(i).getString("desc"));
                                    _dataSet.get(i).put("location", "Location: " + aevents.getJSONObject(i).getString("location"));
                                    _dataSet.get(i).put("start", startstr);
                                    _dataSet.get(i).put("end", endstr);
                                    _dataSet.get(i).put("attendees", aevents.getJSONObject(i).getString("attendees"));
                                    _dataSet.get(i).put("ownerPicture", currentAccount.getPhotoUrl());
                                    numEvents++;
                                    Log.d(TAG, "event added");

                                }
                                initAdapter();
                                initScrollListener();
                            } catch (JSONException | ParseException e) {
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
            String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/user/" + currentAccount.getEmail() + "/findevent/";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //TODO this
                                JSONArray events = response.getJSONArray("events");

                                for (int i = 0; i < events.length(); i++) {
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                    Date startdate = format.parse(events.getJSONObject(i).getString("start").substring(0, 10) + " " + events.getJSONObject(i).getString("start").substring(11, 17));
                                    Date enddate = format.parse(events.getJSONObject(i).getString("end").substring(0, 10) + " " + events.getJSONObject(i).getString("end").substring(11, 17));

                                    String startstr = startdate.toString() + " - ";
                                    String endstr = enddate.toString();

                                    _dataSet.add(new JSONObject());
                                    _dataSet.get(i).put("name", events.getJSONObject(i).getString("name"));
                                    _dataSet.get(i).put("host", events.getJSONObject(i).getString("host"));
                                    _dataSet.get(i).put("desc", events.getJSONObject(i).getString("desc"));
                                    _dataSet.get(i).put("location", "Location: " + events.getJSONObject(i).getString("location"));
                                    _dataSet.get(i).put("start", startstr);
                                    _dataSet.get(i).put("end", endstr);
                                    _dataSet.get(i).put("attendees", events.getJSONObject(i).getString("attendees"));
                                    _dataSet.get(i).put("ownerPicture", currentAccount.getPhotoUrl()); //TODO needs to be replaced
                                    numEvents++;
                                    Log.d(TAG, "event added");

                                }

                                initAdapter();
                                initScrollListener();
                            } catch (JSONException | ParseException e) {
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

        /*
        // debugging code
        ArrayList<JSONObject> dataSet = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            try {
                dataSet.add(new JSONObject());
                dataSet.get(i).put("name","name" + numEvents);
                dataSet.get(i).put("desc","desc"+ numEvents);
                dataSet.get(i).put("location","location"+ numEvents);
                dataSet.get(i).put("start","start"+ numEvents);
                dataSet.get(i).put("end","end"+ numEvents);
                dataSet.get(i).put("attendees","attendees"+ numEvents);
                dataSet.get(i).put("ownerPicture", currentAccount.getPhotoUrl()); // TODO: get owner picture
                numEvents++;
                Log.d(TAG, "event added");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return dataSet;
        */
    }
}