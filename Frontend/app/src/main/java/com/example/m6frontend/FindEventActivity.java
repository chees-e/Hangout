package com.example.m6frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindEventActivity extends AppCompatActivity {

    String TAG = "FindEventActivity";
    String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/getEvent";
    RequestQueue queue;
    private int numEvents;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<JSONObject> dataSet;
    GoogleSignInAccount currentAccount;
    boolean isLoading = false;
    private final int numLoad = 10;
    private final int maxEvents = 30;
    private final int startEvents = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_event);
        recyclerView =  findViewById(R.id.recyclerView);
        numEvents = 0;
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);

        dataSet = initEventData(startEvents);


        initAdapter();
        initScrollListener();
    }

    private void initAdapter() {
        recyclerViewAdapter = new RecyclerViewAdapter(dataSet, this);;
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

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == dataSet.size() - 1) {
                        loadMoreEvents();
                        isLoading = true;
                    }
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