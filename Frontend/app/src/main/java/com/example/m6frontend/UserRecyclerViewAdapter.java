package com.example.m6frontend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<JSONObject> mDataSet;
    private final int VIEW_TYPE_ITEM = 0;
    private final Context context;
    private final String activity;

    public UserRecyclerViewAdapter(ArrayList<JSONObject> dataSet, Context context, String activity) {
        mDataSet = dataSet;
        this.context = context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_ITEM) {
            View v;
            if (activity.equals("friends")) {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_card, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_card, parent, false);
            }
            return new FindUserViewHolder(v, activity);


        } else {
            View v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.event_loading, parent, false);
            return new LoadingViewHolder(v);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof UserRecyclerViewAdapter.FindUserViewHolder) {
            populateEvents((UserRecyclerViewAdapter.FindUserViewHolder) holder, position);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ViewProfileIntent = new Intent(context, ViewProfileActivity.class);
                ViewProfileIntent.putExtra("activity", activity);
                try {
                    ViewProfileIntent.putExtra("friendid", mDataSet.get(position).getString("email"));
                    ViewProfileIntent.putExtra("friendname", mDataSet.get(position).getString("name"));
                    ViewProfileIntent.putExtra("friendpfp", mDataSet.get(position).getString("ownerPicture"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("BBBBBBB" + mDataSet);
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
        int VIEW_TYPE_LOADING = 1;
        return mDataSet.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private static class FindUserViewHolder extends RecyclerView.ViewHolder {

        public TextView profileName;
        public TextView profileEmail;
        public TextView profileLocation;
        public ImageView profilePicture;

        public FindUserViewHolder(View itemView, String activity) {
            super(itemView);
            profileName = itemView.findViewById(R.id.profileCardName);
            profilePicture = itemView.findViewById(R.id.profileCardPicture);

            if (activity.equals("friends")) {
                profileEmail = itemView.findViewById(R.id.profileCardEmail);
                profileLocation = itemView.findViewById(R.id.profileCardLocation);
            }

        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void populateEvents(FindUserViewHolder holder, int position) {
        try {
            holder.profileName.setText(mDataSet.get(position).get("name").toString());
            Glide.with(context)
                    .load(mDataSet.get(position).get("ownerPicture"))
                    .thumbnail(0.5f)
                    .circleCrop()
                    .into(holder.profilePicture);

            if (activity.equals("friends")) {
                holder.profileLocation.setText("user location"); //mDataSet.get(position).get("location").toString());
                holder.profileEmail.setText(mDataSet.get(position).get("email").toString());
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
