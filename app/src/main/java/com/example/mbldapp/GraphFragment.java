package com.example.mbldapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class GraphFragment extends Fragment {

    private ArrayList<MBLDAttempt> attempts;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        //this is how I access methods I wrote in MyHelpers class. create an instance of it and boom! you can use it
        MyHelpers helper = new MyHelpers();
        attempts = helper.readAttempts(getActivity());
        //BELOW FOLLOWS EXAMPLE CHART
        //init line chart
        /*LineChart lineChart = (LineChart) view.findViewById(R.id.line_chart);
        //test data
        int[][] data = {{10,5},{20,10},{30,5},{40,0}};
        List<Entry> entries = new ArrayList<Entry>();
        //filling entries with all data points
        for (int i=0; i<4;i++) entries.add(new Entry(data[i][0], data[i][1]));
        //adding to LineDataSet
        LineDataSet dataSet = new LineDataSet(entries,"MyLabel");
        //dataSet.setColor(...);
        //LineData object..
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();//refresh*/



        plotLineChart(attempts);
        return view;
    }

    private void plotLineChart(ArrayList<MBLDAttempt> dataSource) {
        //plot stuff, taking params either passed by onCreateView or from being called using another frag/activity
        dataSource.get(1).getPoints();

    }
}