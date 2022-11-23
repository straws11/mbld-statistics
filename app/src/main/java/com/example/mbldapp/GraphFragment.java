package com.example.mbldapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


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
                helper.showDialog(getActivity());
            }
        });


        //this is how I access methods I wrote in MyHelpers class. create an instance of it and boom! you can use it

        attempts = helper.readAttempts(getActivity());
        //BELOW FOLLOWS EXAMPLE CHART
        //init line chart
        lineChart = (LineChart) view.findViewById(R.id.line_chart);
        /*//test data
        int[][] data = {{10,5},{20,10},{30,-15},{40,0}};
        List<Entry> entries = new ArrayList<Entry>();
        //filling entries with all data points
        for (int i=0; i<4;i++) entries.add(new Entry(data[i][0], data[i][1]));
        System.out.println(entries);
        //adding to LineDataSet
        LineDataSet dataSet = new LineDataSet(entries,"MyLabel");
        //dataSet.setColor(...);
        //LineData object..
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();//refresh*/

        Collections.reverse(attempts);//readAttempts reverses it for nice display but I need it to be ascending for graph to work
        plotLineChart(attempts);
        return view;
    }


    private void plotLineChart(ArrayList<MBLDAttempt> dataSource) {
        //plot stuff, taking params either passed by onCreateView or from being called using another frag/activity
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < dataSource.size(); i++)
            entries.add(new Entry(i,dataSource.get(i).getPoints()));
            //entries.add(new Entry(dataSource.get(i).getDateDuration(), dataSource.get(i).getPoints()));//old for loop
        //System.out.println(entries);
        //creating a dataset with a label, can have multiple of these datasets to plot over same graph
        LineDataSet lineDataSet = new LineDataSet(entries,"Score");
        //System.out.println("yo");
        lineDataSet.setColor(-16777216);//this is black ? haha
        //this is the total data collection to pass to the chart
        LineData lineData = new LineData(lineDataSet);
        //passing and refreshing chart
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getXAxis().setGranularity(Float.parseFloat(String.valueOf(dataSource.size()*.1)));
        lineChart.getAxisRight().setGranularity(2f);
        //lineChart.getXAxis().enableAxisLineDashedLine(3f,1f,0f);
        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setPosition(800f,2050f);//fix!
        lineChart.getDescription().setText("Graph of points for each attempt");
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private ArrayList<MBLDAttempt> extractData(ArrayList<MBLDAttempt> attempts, String criteria) {
        //not sure what to do with this yet but this could be used to extract all the info I need to plot before passing it to plot()
        return null;
    }
    @Override
    public void onResume() {
        super.onResume();
        lineChart.invalidate();
    }
}