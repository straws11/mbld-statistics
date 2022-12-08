package com.example.mbldapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class TimerBackGroundService extends Service {
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sendBroadcast(new Intent("com.example.mbldApp"));
            //System.out.println("hereee");
            handler.postDelayed(this, 1000);//calls itself so it does the same thing until later cancelled
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //when service is started
        handler.postDelayed(runnable,1000);//actually starts the runnable for the first time

        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
