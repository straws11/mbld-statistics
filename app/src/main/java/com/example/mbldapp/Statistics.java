package com.example.mbldapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity {

    ArrayList<AttemptListItem> attempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        RecyclerView rvAttempts = findViewById(R.id.rvAttempts);

        //initialize attempts
        attempts = AttemptListItem.createAttemptList(40);
        // Create adapter passing in the sample user data
        AttemptItemAdapter adapter = new AttemptItemAdapter(attempts);
        // Attach the adapter to the recyclerview to populate items
        rvAttempts.setAdapter(adapter);
        // Set layout manager to position the items
        rvAttempts.setLayoutManager(new LinearLayoutManager(this));

    }
}