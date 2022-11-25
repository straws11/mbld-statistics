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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class GraphFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ArrayList<MBLDAttempt> attempts;
    private LineChart lineChart;
    private Button btnSetRange;
    private int startVal = 1;
    private int endVal = 1000;
    private String spinnerOption = "All";

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
        //spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.spinOptions,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        //button init
        btnSetRange = view.findViewById(R.id.btnSetRange);
        //set button onclick listener
        btnSetRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showRangeDialog(getActivity());
            }
        });
        //get initial data source
        try {
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
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return view;
    }


    private void plotLineChart(ArrayList<MBLDAttempt> filteredAttempts) {//lineData includes all LineDataSets (could be 1 or 3 depending on genDataSet options)
        //ILineDataSet list to contain all LineDataSets to plot
        List<ILineDataSet> dataSets = new ArrayList<>();

        if (spinnerOption.compareTo("All")==0) {//need to plot all three, so genning the other two we need
            LineDataSet lineDataSetMemo = genDataSet(filteredAttempts, "Exec");
            LineDataSet lineDataSetExec = genDataSet(filteredAttempts, "Memo");
            dataSets.add(lineDataSetExec);
            dataSets.add(lineDataSetMemo);
        }
        LineDataSet lineDataSet = genDataSet(filteredAttempts, spinnerOption);//get main set
        dataSets.add(lineDataSet);
        //final thing to plot
        LineData finalData = new LineData(dataSets);
        lineChart.setData(finalData);
        lineChart.invalidate();
    }

    private LineDataSet genDataSet(ArrayList<MBLDAttempt> dataSource, String option) {//generates the appropriate LineDataSet
        List<Entry> entries = new ArrayList<>();
        switch (option) {
            case "All":
                for (int i = 0; i < dataSource.size(); i++)
                    entries.add(new Entry(i, dataSource.get(i).getExecPerCube()+ dataSource.get(i).getMemoPerCube()));
                break;
            case "Memo":
                for (int i =0; i< dataSource.size(); i++)
                    entries.add(new Entry(i,dataSource.get(i).getMemoPerCube()));
                break;
            case "Exec":
                for (int i =0; i< dataSource.size(); i++)
                    entries.add(new Entry(i,dataSource.get(i).getExecPerCube()));
        }
        LineDataSet lineDataSet = new LineDataSet(entries, option);

        return lineDataSet;
    }

    private ArrayList<MBLDAttempt> extractData(ArrayList<MBLDAttempt> attempts) {//extract data in appropriate cube attempted range
        ArrayList<MBLDAttempt> filteredAttempts = new ArrayList<>();
        for (int i = 0; i < attempts.size(); i++) {
            int attSize = attempts.get(i).getAttempted();
            if (attSize >= startVal && attSize <= endVal) {
                filteredAttempts.add(attempts.get(i));
            }
        }

        return filteredAttempts;
    }

    public void showRangeDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                //assign the global vars to whatever user entered into the dialog edits
                startVal = Integer.parseInt(edtFrom.getText().toString());
                endVal = Integer.parseInt(edtTo.getText().toString());
                //get filtered data by creating new arraylist (the original is kept with all attempts to reuse for a new filter later)
                ArrayList<MBLDAttempt> filteredAttempts = extractData(attempts);

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//select an item in the spinner
        spinnerOption = adapterView.getItemAtPosition(i).toString();
        ArrayList<MBLDAttempt> filteredAttempts = extractData(attempts);//TODO make filtered attempts global so I don't have to gen a new one
        plotLineChart(filteredAttempts);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}