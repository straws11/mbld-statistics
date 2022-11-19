package com.example.mbldapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    Dialog dialog;
    ArrayList<MBLDAttempt> attempts;
    MBLDAttempt selMbldAttempt;
    AttemptItemAdapter adapter;
    Button btnFullInfo, btnDelete;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //btnDelete.setOnClickListener(mDialogListener);
        //btnFullInfo.setOnClickListener(mDialogListener);
    }

    private View.OnClickListener mDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnDialogDelete:
                    //delete attempt
                    break;
                case R.id.btnDialogFullPage:
                    //load full page
                    Intent intent = new Intent(getActivity(),AttemptInfo.class);
                    intent.putExtra("attemptItem",selMbldAttempt);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        rvAttempts = view.findViewById(R.id.rvAttempts);
        dialog = new Dialog(getActivity());
        //find buttons
        dialog.setContentView(R.layout.dialog_item_attempt);
        btnDelete = dialog.findViewById(R.id.btnDialogDelete);
        btnFullInfo = dialog.findViewById(R.id.btnDialogFullPage);
        //set button onclick listeners
        btnDelete.setOnClickListener(mDialogListener);
        btnFullInfo.setOnClickListener(mDialogListener);

        //initialize attempts
        attempts = readAttempts();
            // Create adapter passing in the sample user data
            adapter = new AttemptItemAdapter(attempts,this);
            // Attach the adapter to the recyclerview to populate items
            rvAttempts.setAdapter(adapter);
            // Set layout manager to position the items
            rvAttempts.setLayoutManager(new LinearLayoutManager(getActivity()));

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
    public void onItemClicked(MBLDAttempt mbldAttempt) {
        //what happens when I click an attempt
        //assign global var
        selMbldAttempt = mbldAttempt;//so i can pass it in the intent
        //show dialog
        dialog.show();
        //load info into textviews and such
        TextView tvDialogResult = dialog.findViewById(R.id.tvDialogResult);
        TextView tvDialogScore = dialog.findViewById(R.id.tvDialogScore);
        TextView tvDialogComment = dialog.findViewById(R.id.tvDialogComment);

        tvDialogResult.setText(mbldAttempt.getResult());
        tvDialogScore.setText("Score: " + Integer.toString(mbldAttempt.getScore()));
        //tvDialogComment.setText(mbldAttempt.TODO);

        //Toast.makeText(getActivity(), attItem.getResult(), Toast.LENGTH_SHORT).show();
    }


}