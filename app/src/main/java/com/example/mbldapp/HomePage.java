package com.example.mbldapp;

import static androidx.core.app.NotificationChannelCompat.DEFAULT_CHANNEL_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.utils.ColorTemplate;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Home");
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        //linkable wca link made by
        TextView tvLink = findViewById(R.id.made_by_link);
        tvLink.setMovementMethod(LinkMovementMethod.getInstance());
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