package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindEventActivity extends AppCompatActivity {

    String TAG = "FindEventActivity";
    ExpandableListView eventView;
    List<String> eventGroup;
    HashMap<String, List<String>> eventInfo;
    MainAdapter adapter;
    // TODO: allow user to change this value
    int numStartEvents = 3;
    int totalEvents;
    int maxEvents = 9;
    String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/getEvent";
    RequestQueue queue;

    String eventName = "";
    String id = "";
    String description = "";
    String start = "";
    String end = "";
    List<Boolean> loadingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_event);

        eventView = findViewById(R.id.eventList);
        eventView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        eventGroup = new ArrayList<>();
        eventInfo = new HashMap<>();

        adapter = new MainAdapter(this, eventGroup, eventInfo);
        eventView.setAdapter(adapter);
        queue = Volley.newRequestQueue(this);

        // initialize loading list
        loadingList = new ArrayList<>();
        for (int i = 0; i < numStartEvents; i++) {
            loadingList.add(true);
        }

        // expand eventGroup
        expandGroup(numStartEvents);

        // get events

        totalEvents = 0;
        addEvents(totalEvents, numStartEvents);
        totalEvents = numStartEvents;

        eventView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;
            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE && !isLoading() && totalEvents < maxEvents && totalEvents >= numStartEvents) {
                    setLoading(true);
                    expandGroup(numStartEvents);
                    addEvents(totalEvents, numStartEvents);
                    totalEvents += numStartEvents;
                }
            }
        });

    }

    private boolean isLoading () {
        for (int i = 0; i < loadingList.size(); i++) {
            if (loadingList.get(i) == true) {
                return true;
            }
        }
        return false;
    }

    private void setLoading (boolean isLoading) {
        for (int i = 0; i < loadingList.size(); i++) {
            loadingList.set(i, isLoading);
        }
    }

    private void expandGroup (int toExpand) {
        for (int i = 0; i < toExpand; i++) {
            eventGroup.add("");
        }
    }

    private void addEvents(int eventNum, int toAdd) {
        for (int i = eventNum; i < eventNum + toAdd; i++) {
            addEventData(i);
        }
    }

    // TODO: remove id from events
    // TODO: incorporate backend
    // TODO: add options for event search
    private void addEventData(final int numEvent) {
        // debugging code
        /*
        eventName = "name" + numEvent;
        id = "id";
        description = "desc";
        start = "start";
        end = "end";
        eventGroup.set(numEvent, getString(R.string.event_name) + eventName);

        String[] array;
        List<String> list = new ArrayList<>();
        array = getResources().getStringArray(R.array.event_name);

        // TODO: clean up implementation
        list.add(array[0] + id);
        list.add(array[1] + description);
        list.add(array[2] + start);
        list.add(array[3] + end);

        eventInfo.put(eventGroup.get(numEvent), list);

        adapter.notifyDataSetChanged();
        loadingList.set(numEvent % 3, false);
        eventView.expandGroup(numEvent);
        Log.d(TAG, "event added");
        */
        // Server code
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
                            eventGroup.set(numEvent, getString(R.string.event_name) + eventName);

                            String[] array;
                            List<String> list = new ArrayList<>();
                            array = getResources().getStringArray(R.array.event_name);

                            // TODO: clean up implementation
                            list.add(array[0] + id);
                            list.add(array[1] + description);
                            list.add(array[2] + start);
                            list.add(array[3] + end);

                            eventInfo.put(eventGroup.get(numEvent), list);

                            adapter.notifyDataSetChanged();
                            loadingList.set(numEvent % 3, false);
                            eventView.expandGroup(numEvent);
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


    }
}