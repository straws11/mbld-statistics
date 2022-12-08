package com.example.mbldapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AttemptInfo extends AppCompatActivity {

    //all components
    TextView tvInfoResult, tvInfoStats, tvInfoComment, tvInfoPerformance;
    MyHelpers helper = new MyHelpers();

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
        int rankPos = intent.getIntExtra("rankPos",0);
        int percentage = intent.getIntExtra("percentile", 0);

        //loading all the info from the object into views
        tvInfoResult = findViewById(R.id.tvInfoResult);
        tvInfoStats = findViewById(R.id.tvInfoStats);
        tvInfoComment = findViewById(R.id.tvInfoComment);
        tvInfoPerformance = findViewById(R.id.tvInfoPerformance);

        tvInfoResult.setText(mbldAttempt.getResult());
        String multiLineStats = "Total time: " + helper.encodeTime(mbldAttempt.getTotalTime()) +
                "\nMemo Time: " + helper.encodeTime(mbldAttempt.getPhase1()) +
                "\nMemo/cube: " + helper.encodeTime(mbldAttempt.getMemoPerCube()) +
                "\nExec Time: " + helper.encodeTime(mbldAttempt.getPhase2()) +
                "\nExec/cube: " + helper.encodeTime(mbldAttempt.getExecPerCube()) +
                "\nAttempt Rank: " + rankPos;

        tvInfoStats.setText(multiLineStats);
        tvInfoComment.setText("COMMENT:\n" + mbldAttempt.getComment());
        if (percentage == -1) tvInfoPerformance.setText("This is your only attempt of this size.");
        else tvInfoPerformance.setText(String.format("This attempt performed better than %2d%% of attempts of this size.",percentage));

    }
}