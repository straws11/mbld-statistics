package com.example.mbldapp;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerForeGroundService extends Service {
    public Handler handler = new Handler();
    NotificationCompat.Builder builder;
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //Intent sendBCIntent = new Intent("com.example.mbldApp");
            //sendBCIntent.putExtra("builder",builder);
            sendBroadcast(new Intent("com.example.mbldApp"));
            System.out.println("running.");
            handler.postDelayed(this, 1000);//calls itself so it does the same thing until later cancelled
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//TODO potentially closes after some minutes of minimized, no evidence
        //when service is started
        handler.postDelayed(runnable,1000);//actually starts the runnable for the first time
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        Intent notificationIntent = new Intent(this, AttemptingFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder = new NotificationCompat.Builder(this, "11")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("00:00")
                .setContentText("In PHASE of CUBENO cube multiblind attempt")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSilent(true)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        startForeground(11,builder.build());
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent restartService = new Intent("RestartService");
        sendBroadcast(restartService);
        //handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
