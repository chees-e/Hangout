package com.example.m6frontend;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class EventRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<JSONObject> mDataSet;
    private final int VIEW_TYPE_ITEM = 0;
    private final Context context;
    private final String activity;


    // Provide a suitable constructor (depends on the kind of dataset)
    public EventRecyclerViewAdapter(ArrayList<JSONObject> dataSet, Context context, String activity) {
        mDataSet = dataSet;
        this.context = context;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        // create a new view
        if (viewType == VIEW_TYPE_ITEM) {
            View v;
            if (activity.equals("findEvent")) {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.find_event_card, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_event_card, parent, false);
            }
            return new FindEventViewHolder(v);

        } else {
            View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.event_loading, parent, false);
            return new LoadingViewHolder(v);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof FindEventViewHolder) {
            populateEvents((FindEventViewHolder) holder, position);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }



    @Override
    public int getItemViewType(int position) {
        int VIEW_TYPE_LOADING = 1;
        return mDataSet.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private static class FindEventViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView eventName;
        public ImageView eventOwnerPicture;
        public TextView locationName;
        public TextView eventDescription;
        public TextView startDate;
        public TextView endDate;
        public TextView attendees;
        public ImageButton eventconfirmButton;
        public FindEventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventOwnerPicture = itemView.findViewById(R.id.eventOwnerPicture);
            locationName = itemView.findViewById(R.id.findLocationName);
            eventDescription = itemView.findViewById(R.id.findDescription);
            startDate = itemView.findViewById(R.id.findStartDate);
            endDate = itemView.findViewById(R.id.findEndDate);
            attendees = itemView.findViewById(R.id.findAttendees);
            eventconfirmButton = itemView.findViewById(R.id.event_confirm_button);

        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void populateEvents(FindEventViewHolder holder, int position) {
        try {

            Glide.with(context)
                    .load(mDataSet.get(position).get("ownerPicture"))
                    .thumbnail(0.5f)
                    .circleCrop()
                    .into(holder.eventOwnerPicture);

            holder.eventName.setText(mDataSet.get(position).get("name").toString());
            holder.locationName.setText(mDataSet.get(position).get("location").toString());
            holder.eventDescription.setText(mDataSet.get(position).get("desc").toString());
            holder.startDate.setText(mDataSet.get(position).get("start").toString());
            holder.endDate.setText(mDataSet.get(position).get("end").toString());
            holder.attendees.setText(mDataSet.get(position).get("attendees").toString());
            if (activity.equals("findEvent")) {
                holder.eventconfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            confirmInterest(mDataSet.get(position).getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                holder.eventconfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            deleteEvent(mDataSet.get(position).getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteEvent(String eventid) {
        DeleteEventDialog dialog = new DeleteEventDialog();
        Bundle args = new Bundle();
        args.putString("eventid", eventid);
        dialog.setArguments(args);
        dialog.show(((AppCompatActivity)this.context).getSupportFragmentManager(), " delete event button");

    }

    private void confirmInterest(String eventid) {
        ConfirmInterestDialog dialog = new ConfirmInterestDialog();
        Bundle args = new Bundle();
        args.putString("eventid", eventid);
        dialog.setArguments(args);
        dialog.show(((AppCompatActivity)this.context).getSupportFragmentManager(), " confirm interest button");
    }


}