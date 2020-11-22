package com.example.m6frontend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<JSONObject> mDataSet;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context context;

    public UserRecyclerViewAdapter(ArrayList<JSONObject> dataSet, Context context) {
        mDataSet = dataSet;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_card, parent, false);
            return new FindUserViewHolder(v);
        } else {
            View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.event_loading, parent, false);
            return new LoadingViewHolder(v);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof UserRecyclerViewAdapter.FindUserViewHolder) {
            populateEvents((UserRecyclerViewAdapter.FindUserViewHolder) holder, position);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ViewProfileIntent = new Intent(context, ViewProfileActivity.class);
                context.startActivity(ViewProfileIntent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size(); // TODO: fix
    }



    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private static class FindUserViewHolder extends RecyclerView.ViewHolder {

        public TextView profileName;
        public TextView profileEmail;
        public TextView profileLocation;
        public ImageView profileUri;

        public FindUserViewHolder(View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.profileCardName);
            profileEmail = itemView.findViewById(R.id.profileCardEmail);
            profileLocation = itemView.findViewById(R.id.profileCardLocation);
            profileUri = itemView.findViewById(R.id.profileCardPicture);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.findProgressBar);
        }
    }

    private void populateEvents(UserRecyclerViewAdapter.FindUserViewHolder holder, int position) {
        try {
            holder.profileName.setText(mDataSet.get(position).get("name").toString());
            holder.profileLocation.setText(mDataSet.get(position).get("location").toString());
            holder.profileEmail.setText(mDataSet.get(position).get("email").toString());
            holder.profileUri.setImageURI(null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
