package com.example.m6frontend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendsAdapter extends
        RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName;
        public Button messageButton;

        public ViewHolder(final View itemView) {
            super(itemView);

            friendName = (TextView) itemView.findViewById(R.id.friend_name);
            messageButton = (Button) itemView.findViewById(R.id.message_button);
            messageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent messageIntent = new Intent(itemView.getContext(), ChatActivity.class);
                    itemView.getContext().startActivity(messageIntent);
                }
            });
        }
    }

    private List<String> friendList;

    public FriendsAdapter(List<String> friendList) {
        this.friendList = friendList;
    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.my_friend, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String name = friendList.get(position);

        holder.friendName.setText(name);
        holder.messageButton.setText("Message");
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }
}
