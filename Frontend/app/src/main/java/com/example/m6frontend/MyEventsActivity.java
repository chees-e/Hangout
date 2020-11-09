package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {
    private String TAG = "MyEventsActivity";
    private List<String> myList;
    private final String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/event";
    private RequestQueue queue;
    //EventsAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        RecyclerView rvEvents;

        myList = new ArrayList<String>();
        queue = Volley.newRequestQueue(this);
        FloatingActionButton addEventButton;
        addEvents();
        Log.d(TAG, Integer.toString(myList.size()));

        rvEvents = (RecyclerView) findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
       // adapter = new EventsAdapter(this, myList);
        //rvEvents.setAdapter(adapter);

        addEventButton = findViewById(R.id.fab);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addEventIntent = new Intent(MyEventsActivity.this, AddEventActivity.class);
                startActivity(addEventIntent);
            }
        });
    }

    /*private void addEvents() {
        // Server code
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "success");
                int len = response.length();
                for (int i = 0; i < len; i++) {
                    try {
                        eventName = response.getJSONObject(i).getString("name");
                        id = response.getJSONObject(i).getString("id");
                        description = response.getJSONObject(i).getString("desc");
                        start = response.getJSONObject(i).getString("start");
                        end = response.getJSONObject(i).getString("end");

                        myList.set(i, eventName);
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Event info can not be received");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }*/

    private void addEvents() {
        // Server code

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String eventName = response.getString("name");
                            // String id = response.getString("id");
                            // String description = response.getString("desc");
                            // String start = response.getString("start");
                            //String end = response.getString("end");

                            myList.add(getString(R.string.event_name) + eventName);
                            //myList.add(id);
                           // adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Event info can not be received");
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}