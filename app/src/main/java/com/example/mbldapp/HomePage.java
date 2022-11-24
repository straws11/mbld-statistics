package com.example.mbldapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Home");
        setSupportActionBar(myToolbar);
    }

    public void onbtnTODOClicked(View view) {
        //Intent intent = new Intent(this,Statistics.class);
        //startActivity(intent);
    }

    public void onbtnEnterApp(View view) {
        Intent intent = new Intent(this,MainApp.class);
        startActivity(intent);
    }
}