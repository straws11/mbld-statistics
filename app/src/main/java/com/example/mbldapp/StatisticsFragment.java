package com.example.mbldapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class StatisticsFragment extends Fragment {

    private RecyclerView rvAttempts;
    ArrayList<AttemptListItem> formattedAttempts = new ArrayList<>();
    ArrayList<MBLDAttempt> attempts;
    AttemptItemAdapter adapter;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_statistics); - handled by onCreateView
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        rvAttempts = view.findViewById(R.id.rvAttempts);

        //initialize attempts
        attempts = readAttempts();
        if (attempts != null) {
            int attemptsSize = attempts.size();
            for (int i = 0; i < attemptsSize; i++) {//iterate through each mbld attempt and create a formatted ListItem
                //content of each item
                String result = attempts.get(i).toString();
                String date = attempts.get(i).getDate();
                formattedAttempts.add(new AttemptListItem(result, date));
            }
            // Create adapter passing in the sample user data
            adapter = new AttemptItemAdapter(formattedAttempts);
            // Attach the adapter to the recyclerview to populate items
            rvAttempts.setAdapter(adapter);
            // Set layout manager to position the items
            rvAttempts.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        return view;
    }

    public ArrayList<MBLDAttempt> readAttempts() {
        ArrayList<MBLDAttempt> attempts = new ArrayList<>();
        Reader reader = null;
        //reads all attempts from the json file
        try {
            reader = new FileReader(getActivity().getFilesDir()+"/attempts.json");
            Type attemptsListType = new TypeToken<ArrayList<MBLDAttempt>>(){}.getType();
            attempts = new Gson().fromJson(reader,attemptsListType);
        } catch (FileNotFoundException e) {//if not found
            e.printStackTrace();
            return null;//didn't find it
        }
        return attempts;
    }
}