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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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
        final ArrayList<JSONObject> _dataSet = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(FindEventActivity.this);
        //This url needs to be changed so that it gets the events that users are not in
        //Or make the response include whether the user has already participated or not so that
        //it can be displayed differently
        //I will work on it so DW about this part
        String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/event/";

        //Getting all events
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray events = response.getJSONArray("events");
                            int length = response.getInt("length");

                            Uri tempurl = currentAccount.getPhotoUrl();
                            for (int i = 0; i < length; i++) {
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                Date startdate = format.parse( events.getJSONObject(i).getString("start").substring(0,10) + " " +  events.getJSONObject(i).getString("start").substring(11,17));
                                Date enddate = format.parse( events.getJSONObject(i).getString("end").substring(0,10) + " " +  events.getJSONObject(i).getString("end").substring(11,17));
                                //TODO: Formating the date
                                //So change these two
                                //Rn for some reason it is displaying start time twice instead of start and end,
                                //Also there is a big black space in each entry => idk why but it is a problem
                                String startstr = startdate.toString() + " - ";
                                String endstr = enddate.toString();

                                _dataSet.add(new JSONObject());
                                _dataSet.get(i).put("name", events.getJSONObject(i).getString("name"));
                                _dataSet.get(i).put("desc", events.getJSONObject(i).getString("desc"));
                                _dataSet.get(i).put("location", "Location: " + events.getJSONObject(i).getString("location"));
                                _dataSet.get(i).put("start", startstr);
                                _dataSet.get(i).put("end", endstr);
                                _dataSet.get(i).put("attendees", "TODO: attendees");
                                _dataSet.get(i).put("ownerPicture", tempurl); // TODO: get owner picture
                                numEvents++;
                                Log.d(TAG, "event added");

                            }
                            initAdapter();
                            initScrollListener();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (ParseException e) {
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

        return _dataSet;

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