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
import java.util.Comparator;
import java.util.List;

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
                    helper.saveAttemptsToFile(getActivity(),attempts);
                    dialog.dismiss();
                    onResume();

                    break;
                case R.id.btnDialogFullPage:
                    //load full page
                    Intent intent = new Intent(getActivity(),AttemptInfo.class);
                    intent.putExtra("attemptItem", selMbldAttempt);
                    intent.putExtra("rankPos",getAttemptRank(attempts, selMbldAttempt));
                    intent.putExtra("percentile",getPerformancePercentage(attempts, selMbldAttempt));
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

    public int getAttemptRank(ArrayList<MBLDAttempt> attempts, MBLDAttempt mbldAttempt) {
        //sort array according to points
        Collections.sort(attempts,(att1,att2)->att2.getPoints()-att1.getPoints());
        for (int i=0; i<attempts.size();i++) {
            if (attempts.get(i).equals(mbldAttempt)) {
                return i+1;
            }
        }
        return 0;
    }

    public int getPerformancePercentage(ArrayList<MBLDAttempt> attempts, MBLDAttempt mbldAttempt) {
        //produce a percentage for "this performed better than x% attempts of this size"
        ArrayList<MBLDAttempt> sameAttempts = new ArrayList<>();
        //creates array containing the scores of all attempts of equal size to this attempt
        for (int i = 0; i < attempts.size(); i++) {
            if (attempts.get(i).getAttempted() == mbldAttempt.getAttempted()) {
                sameAttempts.add(attempts.get(i));
            }
        }
        int sameAttemptsSize = sameAttempts.size();
        System.out.println(sameAttemptsSize + "size");
        //sorting
        for (int i = 0; i < sameAttemptsSize-1; i++) {
            for (int j = i+1; j < sameAttemptsSize; j++) {
                if (sameAttempts.get(j).getPoints()>sameAttempts.get(i).getPoints()) {
                     Collections.swap(sameAttempts, i, j);
                } else if (sameAttempts.get(j).getPoints()==sameAttempts.get(i).getPoints() && sameAttempts.get(j).getTotalTime()<sameAttempts.get(i).getTotalTime()) {
                    //if points are equal but j was faster
                    Collections.swap(sameAttempts, i, j);
                }
            }
        }
        System.out.println(sameAttempts);
        //get position of current attempt in the sameAttempts array
        int pos = 0;
        for (int i = 0; i < sameAttemptsSize; i++) {
            System.out.println(i + "loop");
            if (sameAttempts.get(i).equals(mbldAttempt)) {
                pos = i+1;
                break;
            }
        }
        System.out.println(pos);
        System.out.println((int) ((float) (sameAttemptsSize-pos)/(sameAttemptsSize-1)*100));
        return (int) ((float) (sameAttemptsSize-pos)/(sameAttemptsSize-1)*100);//todo still wrong LOL
    }

}