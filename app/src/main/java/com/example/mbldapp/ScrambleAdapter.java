package com.example.mbldapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScrambleAdapter extends
        RecyclerView.Adapter<ScrambleAdapter.ViewHolder> {

    private String[] scrambles;

    //constructor
    public ScrambleAdapter(String[] scrambles) {
        this.scrambles = scrambles;
    }

    @NonNull
    @Override
    public ScrambleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate layout
        View scrambleView = inflater.inflate(R.layout.item_scramble, parent,false);

        ViewHolder viewHolder = new ViewHolder(scrambleView);
        return viewHolder;
    }

    @Override//this is how im placing stuff in the RecyclerView
    public void onBindViewHolder(@NonNull ScrambleAdapter.ViewHolder holder, int position) {
        String scramble = scrambles[position];

        TextView tvScramble = holder.scrambleTextView;
        tvScramble.setText(Integer.toString(position+1) +". "+ scramble);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView scrambleTextView;
        public LinearLayout itemScramble;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            scrambleTextView = (TextView) itemView.findViewById(R.id.tvInfoScrambleItem);
            itemScramble = (LinearLayout) itemView.findViewById(R.id.ItemScramble);
        }
    }

    @Override
    public int getItemCount() {
        try {return scrambles.length;}
        catch (NullPointerException e) {return 0;}
    }
}
