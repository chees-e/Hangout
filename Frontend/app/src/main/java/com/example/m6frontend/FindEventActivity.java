package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindEventActivity extends AppCompatActivity {

    ExpandableListView eventView;
    List<String> eventGroup;
    HashMap<String, List<String>> eventInfo;
    MainAdapter adapter;

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
        initEventData();

    }

    // TODO: remove id from events
    // TODO: incorporate backend
    // TODO: add options for event search
    private void initEventData() {
        eventGroup.add(getString(R.string.event_name));

        String[] array;
        List<String> list = new ArrayList<>();
        array = getResources().getStringArray(R.array.event_name);
        for (String item: array) {
            list.add(item);
        }

        eventInfo.put(eventGroup.get(0), list);
        adapter.notifyDataSetChanged();
    }
}