package com.example.m6frontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventsAdapter extends
        RecyclerView.Adapter<EventsAdapter.ViewHolder>{

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView myEventName;

        public ViewHolder(View itemView) {
            super(itemView);
            myEventName = (TextView) itemView.findViewById(R.id.tvEventName);
        }
    }

    private List<String> eventList;
    private LayoutInflater inflater;

    public EventsAdapter(Context context, List<String> events) {
        this.eventList = events;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String event = eventList.get(position);
        holder.myEventName.setText(event);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
