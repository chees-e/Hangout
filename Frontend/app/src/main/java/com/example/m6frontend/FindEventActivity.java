package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindEventActivity extends AppCompatActivity {

    String TAG = "FindEventActivity";
    ExpandableListView eventView;
    List<String> eventGroup;
    HashMap<String, List<String>> eventInfo;
    MainAdapter adapter;
    int numEvents;
    // TODO: allow user to change this value
    int numStartEvents = 1;
    String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/getEvent";
    RequestQueue queue;

    String eventName = "";
    String id = "";
    String description = "";
    String start = "";
    String end = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_event);

        eventView = findViewById(R.id.eventList);
        eventGroup = new ArrayList<>();
        eventInfo = new HashMap<>();

        adapter = new MainAdapter(this, eventGroup, eventInfo);
        eventView.setAdapter(adapter);
        // get events
        numEvents = 0;
        queue = Volley.newRequestQueue(this);
        for (int i = 0; i < numStartEvents; i++) {
            addEventData();
        }

    }

    // TODO: remove id from events
    // TODO: incorporate backend
    // TODO: add options for event search
    private void addEventData() {


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            eventName = response.getString("name");
                            id = response.getString("id");
                            description = response.getString("desc");
                            start = response.getString("start");
                            end = response.getString("end");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

        eventGroup.add(getString(R.string.event_name) + eventName);

        String[] array;
        List<String> list = new ArrayList<>();
        array = getResources().getStringArray(R.array.event_name);

        // TODO: clean up implementation
        list.add(array[0] + id);
        list.add(array[1] + description);
        list.add(array[2] + start);
        list.add(array[3] + end);

        eventInfo.put(eventGroup.get(numEvents), list);
        adapter.notifyDataSetChanged();
    }
}