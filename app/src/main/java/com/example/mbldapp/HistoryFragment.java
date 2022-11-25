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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class HistoryFragment extends Fragment implements SelectItemListener {

    //helper
    MyHelpers helper = new MyHelpers();
    //private vars
    private RecyclerView rvAttempts;
    private Dialog dialog;
    private ArrayList<MBLDAttempt> attempts;
    private MBLDAttempt selMbldAttempt;
    private AttemptItemAdapter adapter;
    private Button btnFullInfo, btnDelete;

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
                    attempts.remove(selMbldAttempt);
                    break;
                case R.id.btnDialogFullPage:
                    //load full page
                    Intent intent = new Intent(getActivity(),AttemptInfo.class);
                    intent.putExtra("attemptItem",selMbldAttempt);
                    startActivity(intent);
                    break;
                case R.id.btnDialogEdit:
                    //load new dialog, close current
                    //can edit result, comment?, time?
                    //close this new one, open the original
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

        try {
            attempts = helper.readAttempts(getActivity());
            // Create adapter passing in the sample user data
            adapter = new AttemptItemAdapter(attempts,this);
            // Attach the adapter to the recyclerview to populate items
            rvAttempts.setAdapter(adapter);
            // Set layout manager to position the items
            rvAttempts.setLayoutManager(new LinearLayoutManager(getActivity()));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        return view;
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

        tvDialogResult.setText(mbldAttempt.getResult() +"\n"+ mbldAttempt.getComplexTime());
        tvDialogScore.setText("Score: " + Integer.toString(mbldAttempt.getPoints()));
        tvDialogComment.setText(mbldAttempt.getComment());

        //Toast.makeText(getActivity(), attItem.getResult(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {//overriding the onresume of a fragment (which is called when I move to this History tab) and updating the rvAttempts
        super.onResume();
        //updates the data source used by adapter
        attempts = helper.readAttempts(getActivity());
        adapter = new AttemptItemAdapter(attempts,this);
        rvAttempts.setAdapter(adapter);
        rvAttempts.setLayoutManager(new LinearLayoutManager(getActivity()));
        System.out.println("onResume");
        //notify update. should maybe use the more specific methods?
        //adapter.notifyDataSetChanged();
    }

}