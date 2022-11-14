package com.example.mbldapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttemptItemAdapter extends
        RecyclerView.Adapter<AttemptItemAdapter.ViewHolder> {

    private List<AttemptListItem> attempts;

    public AttemptItemAdapter(List<AttemptListItem> pAttempts) {
        attempts = pAttempts;//constructor
    }

    @Override
    public AttemptItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View attemptView = inflater.inflate(R.layout.item_attempt, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(attemptView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(AttemptItemAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        AttemptListItem attemptListItem = attempts.get(position);

        // Set item views based on your views and data model
        TextView textResultView = holder.resultTextView;
        textResultView.setText(String.valueOf(attemptListItem.getResult()));
        TextView textScoreView = holder.scoreTextView;
        textScoreView.setText(String.valueOf(attemptListItem.getScore()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return attempts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {//inner class pretty sure
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView resultTextView;
        public TextView scoreTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            resultTextView = (TextView) itemView.findViewById(R.id.tvResultItem);
            scoreTextView = (TextView) itemView.findViewById(R.id.tvScoreItem);
        }
    }


}
