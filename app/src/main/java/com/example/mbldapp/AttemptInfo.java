package com.example.mbldapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AttemptInfo extends AppCompatActivity {

    //all components
    TextView tvInfoResult, tvInfoStats;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_info);
        //toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_info);
        myToolbar.setTitle("Attempt Information");//TODO:add id of attempt or something
        setSupportActionBar(myToolbar);

        //getting the attempt from the intent that caused this activity to start
        Intent intent = getIntent();
        MBLDAttempt mbldAttempt = (MBLDAttempt) intent.getSerializableExtra("attemptItem");

        //loading all the info from the object into views
        tvInfoResult = findViewById(R.id.tvInfoResult);
        tvInfoStats = findViewById(R.id.tvInfoStats);

        String multiLineStats = "Total time: " + mbldAttempt.getTotalTime() +
                "\nMemo Time: " + mbldAttempt.getPhase1() +
                "\nExec Time: " + mbldAttempt.getPhase2();

        tvInfoStats.setText(multiLineStats);

    }
}