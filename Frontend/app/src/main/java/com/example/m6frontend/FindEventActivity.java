package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<JSONObject> dataSet;
    GoogleSignInAccount currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_event);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.find_event_recyclerview);
        numEvents = 0;
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        dataSet = getEventData(5);
        mAdapter = new FindEventAdapter(dataSet);;
        recyclerView.setAdapter(mAdapter);
    }

    private ArrayList<JSONObject> getEventData(int num) {

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