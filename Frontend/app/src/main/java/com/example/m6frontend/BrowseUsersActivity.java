package com.example.m6frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BrowseUsersActivity extends AppCompatActivity {
    private final String TAG = "BrowseUsersActivity";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_users);
        recyclerView = findViewById(R.id.userRecyclerView);
        numUsers = 0;
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);

        int startUsers = 10;
        dataSet = initUserData(startUsers);


        initAdapter();
        initScrollListener();
    }

    private void initAdapter() {
        recyclerViewAdapter = new UserRecyclerViewAdapter(dataSet, this);
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

                while (currentSize - 1 < nextLimit && currentSize - 1 < maxUsers) {
                    dataSet.add(new JSONObject());
                    try {
                        dataSet.get(currentSize).put("name","name" + numUsers);
                        dataSet.get(currentSize).put("email", "email" + numUsers);
                        dataSet.get(currentSize).put("location","location"+ numUsers);
                        // TODO: get user picture
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


    private ArrayList<JSONObject> initUserData(int num) {

        // debugging code
        ArrayList<JSONObject> dataSet = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            try {
                dataSet.add(new JSONObject());
                dataSet.get(i).put("name","name" + numUsers);
                dataSet.get(i).put("email", "email" + numUsers);
                dataSet.get(i).put("location","location"+ numUsers);
                 // TODO: get owner picture
                numUsers++;
                Log.d(TAG, "event added");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        return dataSet;

    }
}

