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
import android.view.View;

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
        //android docs said to init the notify channel when app starts \o/
        createNotificationChannel();
    }

    public void onbtnTODOClicked(View view) {
        //Intent intent = new Intent(this,Statistics.class);
        //startActivity(intent);
    }

    public void onbtnEnterApp(View view) {
        Intent intent = new Intent(this,MainApp.class);
        startActivity(intent);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Name in Android App Info"; //replace with getString(R.string.channel_name) for better code...
            String description = "Desc in Android App Info";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("11", name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}