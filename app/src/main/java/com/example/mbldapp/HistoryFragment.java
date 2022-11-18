package com.example.mbldapp;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryFragment extends Fragment implements SelectItemListener {

    private RecyclerView rvAttempts;
    ArrayList<AttemptListItem> formattedAttempts = new ArrayList<>();
    ArrayList<MBLDAttempt> attempts;
    AttemptItemAdapter adapter;

    public HistoryFragment() {
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);
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
            adapter = new AttemptItemAdapter(formattedAttempts,this);
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

    @Override //this is the implementation of the interface thing created
    public void onItemClicked(AttemptListItem attItem) {
        //what happens when I click an attempt
        //show dialog
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_item_attempt);
        dialog.show();
        //load info into textviews and such
        TextView tvDialogResult = dialog.findViewById(R.id.tvDialogResult);
        TextView tvDialogScore = dialog.findViewById(R.id.tvDialogScore);
        TextView tvDialogComment = dialog.findViewById(R.id.tvDialogComment);

        tvDialogResult.setText(attItem.getResult());
        //tvDialogScore.setText();

        //Toast.makeText(getActivity(), attItem.getResult(), Toast.LENGTH_SHORT).show();
    }
}