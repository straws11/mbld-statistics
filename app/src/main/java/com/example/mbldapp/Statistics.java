package com.example.mbldapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Statistics extends AppCompatActivity {

    ArrayList<AttemptListItem> formattedAttempts = new ArrayList<>();
    ArrayList<MBLDAttempt> attempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        RecyclerView rvAttempts = findViewById(R.id.rvAttempts);

        //initialize attempts
        attempts = readAttempts();
        int attemptsSize = attempts.size();
        if (attempts != null) {
            for (int i = 0; i < attemptsSize; i++) {//iterate through each mbld attempt and create a formatted ListItem
                //content of each itme
                String result = attempts.get(i).toString();
                String date = attempts.get(i).getDate();
                formattedAttempts.add(new AttemptListItem(result, date));
            }
            // Create adapter passing in the sample user data
            AttemptItemAdapter adapter = new AttemptItemAdapter(formattedAttempts);
            // Attach the adapter to the recyclerview to populate items
            rvAttempts.setAdapter(adapter);
            // Set layout manager to position the items
            rvAttempts.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public ArrayList<MBLDAttempt> readAttempts() {
        ArrayList<MBLDAttempt> attempts = new ArrayList<>();
        Reader reader = null;
        //reads all attempts from the json file
        try {
            reader = new FileReader(getApplicationContext().getFilesDir()+"/attempts.json");
            Type attemptsListType = new TypeToken<ArrayList<MBLDAttempt>>(){}.getType();
            attempts = new Gson().fromJson(reader,attemptsListType);
        } catch (FileNotFoundException e) {//if not found
            e.printStackTrace();
            return null;//didn't find it
        }
        return attempts;
    }


}