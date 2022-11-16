package com.example.mbldapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onDoAttemptBtnClicked(View view) {
        Intent intent = new Intent(this,Attempting.class);
        startActivity(intent);


    }

    public void onbtnStatisticsClicked(View view) {
        Intent intent = new Intent(this,Statistics.class);
        startActivity(intent);
    }

    public void onbuttonClick(View view) {
        Intent intent = new Intent(this,MainApp.class);
        startActivity(intent);
    }
}