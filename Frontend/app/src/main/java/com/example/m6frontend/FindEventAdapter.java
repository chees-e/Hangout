package com.example.m6frontend;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.m6frontend.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FindEventAdapter extends RecyclerView.Adapter<FindEventAdapter.FindEventViewHolder> {
    private ArrayList<JSONObject> mDataSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FindEventViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView eventName;
        public ImageView eventOwnerPicture;
        public TextView locationName;
        public TextView eventDescription;
        public TextView startDate;
        public TextView startTime;
        public TextView endDate;
        public TextView endTime;
        public TextView attendees;
        public FindEventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventOwnerPicture = itemView.findViewById(R.id.eventOwnerPicture);
            locationName = itemView.findViewById(R.id.findLocationName);
            eventDescription = itemView.findViewById(R.id.findDescription);
            startDate = itemView.findViewById(R.id.findStartDate);
            startTime = itemView.findViewById(R.id.findStartTime);
            endDate = itemView.findViewById(R.id.findEndDate);
            endTime = itemView.findViewById(R.id.findEndTime);
            attendees = itemView.findViewById(R.id.findAttendees);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FindEventAdapter(ArrayList<JSONObject> dataSet) {
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FindEventAdapter.FindEventViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.eventcard, parent, false);

        FindEventViewHolder vh = new FindEventViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FindEventViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            holder.eventName.setText(mDataSet.get(position).get("name").toString());
            holder.eventOwnerPicture.setImageResource(R.drawable.ic_launcher_foreground); // TODO: get owner picture
            holder.locationName.setText(mDataSet.get(position).get("location").toString());
            holder.eventDescription.setText(mDataSet.get(position).get("desc").toString());
            holder.startDate.setText(mDataSet.get(position).get("start").toString());
            holder.startTime.setText(mDataSet.get(position).get("start").toString());
            holder.endDate.setText(mDataSet.get(position).get("end").toString());
            holder.endTime.setText(mDataSet.get(position).get("end").toString());
            holder.attendees.setText(mDataSet.get(position).get("attendees").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}