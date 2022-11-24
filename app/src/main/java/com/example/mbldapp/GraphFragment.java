package com.example.mbldapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class GraphFragment extends Fragment {

    private ArrayList<MBLDAttempt> attempts;
    private LineChart lineChart;
    private Button btnSetRange;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyHelpers helper = new MyHelpers();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        //button init
        btnSetRange = view.findViewById(R.id.btnSetRange);
        //set button onclick listener
        btnSetRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showRangeDialog();
            }
        });
        //get initial data source
        attempts = helper.readAttempts(getActivity());

        //init line and set properties
        lineChart = (LineChart) view.findViewById(R.id.line_chart);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setGranularity(2f);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setPosition(800f,2050f);//fix!
        lineChart.getDescription().setText("Graph of points for each attempt");

        Collections.reverse(attempts);//readAttempts reverses it for nice display but I need it to be ascending for graph to work
        plotLineChart(attempts);
        return view;
    }


    private void plotLineChart(ArrayList<MBLDAttempt> dataSource) {
        //plot stuff, taking params either passed by onCreateView or from being called using another frag/activity
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < dataSource.size(); i++)
            entries.add(new Entry(i,dataSource.get(i).getPoints()));
        //System.out.println(entries);
        //creating a dataset with a label, can have multiple of these datasets to plot over same graph
        LineDataSet lineDataSet = new LineDataSet(entries,"Points");
        //System.out.println("yo");
        lineDataSet.setColor(-16777216);//this is black ? haha
        //this is the total data collection to pass to the chart
        LineData lineData = new LineData(lineDataSet);
        //passing and refreshing chart
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private ArrayList<MBLDAttempt> extractData(ArrayList<MBLDAttempt> attempts, int startVal, int endVal) {
        ArrayList<MBLDAttempt> filteredAttempts = new ArrayList<>();
        for (int i = 0; i < attempts.size(); i++) {
            int attSize = attempts.get(i).getAttempted();
            if (attSize >= startVal && attSize <= endVal) {
                filteredAttempts.add(attempts.get(i));
            }
        }

        return filteredAttempts;
    }

    public void showRangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //inflater to create or build the layout from the xml file
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cube_range,null);//this is crucial for me to get components on this view later!
        builder.setView(dialogView);
        //the buttons are somehow automatically appending to the layout on the bottom, along with their onclick listeners
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //TODO add data validation for user input to not crash.
                //do yes stuff
                EditText edtFrom = dialogView.findViewById(R.id.edtDialogFromRange);
                EditText edtTo = dialogView.findViewById(R.id.edtDialogToRange);
                //filter data source
                ArrayList<MBLDAttempt> filteredAttempts = extractData(attempts,Integer.parseInt(edtFrom.getText().toString()),Integer.parseInt(edtTo.getText().toString()));//this updates the attempts arraylist, removing all attempts not matching criteria
                plotLineChart(filteredAttempts);//plots with the new info
                //refresh graph

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                //do no stuff
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        lineChart.invalidate();
    }
}