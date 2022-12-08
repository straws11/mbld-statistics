package com.example.mbldapp;

import static androidx.core.app.NotificationChannelCompat.DEFAULT_CHANNEL_ID;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;


public class AttemptingFragment extends Fragment implements View.OnClickListener, LifecycleObserver {
    //vars
    public Handler handler = new Handler();
    MyHelpers helper = new MyHelpers();//gives me access to my helper methods like encoding time and saving and reading attempts
    EditText edtAmountSolved;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    private Intent notificationIntent;
    private PendingIntent pendingIntent;
    public static int notificationId = 11;//i need one but I just made up the value, idk what it means.
    EditText edtCubeAmount;
    EditText edtComment;
    Button btnGen;
    Button btnStart;
    Button btnNewAttempt;
    TextView tvTimer;
    TextView tvResultDisplay;
    TextView tvAmountSolved;
    int runPhase = 1; //1 not started/finished/stopped, 2 in memo, 3 in exec
    int phase1, phase2, totalSeconds = 0;
    int solved;
    int attempted;

    public AttemptingFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attempting, container, false);
        edtCubeAmount = view.findViewById(R.id.edtCubeAmount);
        edtAmountSolved = view.findViewById(R.id.edtSolved);
        btnGen = view.findViewById(R.id.btnGetScrambles);
        btnStart = view.findViewById(R.id.btnStartAttempt);
        btnNewAttempt = view.findViewById(R.id.btnNextAttempt);
        tvTimer = view.findViewById(R.id.tvTimer);
        tvResultDisplay = view.findViewById(R.id.tvResult);
        tvAmountSolved = view.findViewById(R.id.tvSolved);
        edtComment = view.findViewById(R.id.edtComment);

        //set onclicklisteners to all buttons so they call the onClick? function that is overriden below
        btnGen.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnNewAttempt.setOnClickListener(this);

        //no clue if this belongs here
        notificationManager = NotificationManagerCompat.from(getActivity());
        notificationIntent = new Intent(getActivity(), AttemptingFragment.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(getActivity(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        //creating the notification object
        builder = new NotificationCompat.Builder(getActivity(), "11")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Title")
                .setContentText("contenttext")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return view;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        //show the notify!
        notificationManager.notify(notificationId, builder.build());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        //hide the notify?
        try {
            notificationManager.cancel(11);//TODO please fix these disgusting hardcoded ids
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("foregrounded!"); runs when app is opened from minimized state
    }

    public void onbtnGenScramblesClicked(View view) {
        //hides components that get my scramble count
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 100);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101);

        edtCubeAmount.setVisibility(View.INVISIBLE);
        btnGen.setVisibility(View.INVISIBLE);
        //show
        btnStart.setVisibility(View.VISIBLE);
        tvTimer.setVisibility(View.VISIBLE);

        attempted = Integer.parseInt(edtCubeAmount.getText().toString());
        //TODO:generate and display scrambles
    }

    public void onbtnStartClicked(View view) {
        if (runPhase==1) { //attempt inactive, now go into memo
            //start running
            runPhase++;//now in memo
            //handler.postDelayed(runnable,1000);//starts the worker? thread, it will loop within itself now. NOW IN TimerBackgroundService.java
            Intent serviceIntent = new Intent(getActivity(), TimerBackGroundService.class);
            getActivity().startService(serviceIntent);
            btnStart.setText("Split");

        } else if (runPhase==2) {//in memo, now go into exec
            runPhase++;
            btnStart.setText("Stop");
            phase1 = totalSeconds;//get current time as memo time

        } else {//in exec aka runPhase = 3, now stop
            //handler.removeCallbacks(runnable);//stops the infinite runnable loop
            Intent serviceIntent = new Intent(getActivity(), TimerBackGroundService.class);
            getActivity().stopService(serviceIntent);
            phase2 = totalSeconds-phase1;//get exec time
            runPhase=1;//resets var
            btnStart.setVisibility(View.INVISIBLE);
            btnStart.setText("Start");
            tvTimer.setVisibility(View.INVISIBLE);
            edtComment.setVisibility(View.VISIBLE);
            edtComment.setText(null);
            btnNewAttempt.setVisibility(View.VISIBLE);
            edtAmountSolved.setVisibility(View.VISIBLE);
            edtAmountSolved.setText(null);
            tvAmountSolved.setVisibility(View.VISIBLE);
        }
    }

    public void onbtnNextAttemptClicked(View view) {
        //hide
        btnNewAttempt.setVisibility(View.INVISIBLE);
        edtAmountSolved.setVisibility(View.INVISIBLE);
        tvAmountSolved.setVisibility(View.INVISIBLE);
        edtComment.setVisibility(View.INVISIBLE);
        //show
        btnGen.setVisibility(View.VISIBLE);
        edtCubeAmount.setVisibility(View.VISIBLE);
        edtCubeAmount.setText(null);
        //save attempt
        //create mbld object
        solved = Integer.parseInt(edtAmountSolved.getText().toString());
        String comment = edtComment.getText().toString();
        MBLDAttempt mbldAttempt = new MBLDAttempt(solved,attempted,phase1,phase2,comment);
        //Resetting vars
        phase1 = 0;
        phase2 = 0;
        totalSeconds = 0;
        //SAVING
        helper.saveAttempt(getActivity(),mbldAttempt);
    }

    //MY FUNCTIONS USED
    //--------------------------//

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGetScrambles:
                onbtnGenScramblesClicked(view);
                break;
            case R.id.btnNextAttempt:
                onbtnNextAttemptClicked(view);
                break;
            case R.id.btnStartAttempt:
                onbtnStartClicked(view);
                break;
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[] { permission }, requestCode);
        }
        else {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 101) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Storage Read Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Storage Read Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //update components
            totalSeconds+=10;//TODO this is for some more accurate timings, it should be ++
            String time = helper.encodeTime(totalSeconds);
            System.out.println("updated");
            tvTimer.setText(time);
            //update notification text


        }
    };


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter("com.example.mbldApp"));
    }
}