package com.example.mbldapp;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import kotlinx.coroutines.flow.SharedFlow;


public class AttemptingFragment extends Fragment implements View.OnClickListener, LifecycleObserver {
    //vars
    public Handler handler = new Handler();
    MyHelpers helper = new MyHelpers();//gives me access to my helper methods like encoding time and saving and reading attempts
    EditText edtAmountSolved;
    EditText edtCubeAmount;
    EditText edtComment;
    Button btnGen;
    Button btnEditResult;
    Button btnStart;
    Button btnNewAttempt;
    TextView tvTimer;
    TextView tvResultDisplay;
    TextView tvAmountSolved;
    boolean fragmentBackgrounded = false;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attempting, container, false);
        edtCubeAmount = view.findViewById(R.id.edtCubeAmount);
        edtAmountSolved = view.findViewById(R.id.edtSolved);
        btnGen = view.findViewById(R.id.btnGetScrambles);
        btnEditResult = view.findViewById(R.id.btnEditResult);
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
        btnEditResult.setOnClickListener(this);

        //update the timer if there was an attempt ongoing before the app got killed (either by user or Android)
        //temp
        //SharedPreferences sharedPreferences = getActivity().getSharedPreferences("timerState",Context.MODE_PRIVATE);
        //sharedPreferences.edit().clear().commit();
        getSharedPrefInfo();

        return view;
    }

    /*@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
        //show the notify if attempt is active (phase 2 or 3)
        fragmentBackgrounded = true;
        if (runPhase != 1) notificationManager.notify(notificationId, timerService.builder.build());
        //setAlertOnlyOnce();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
        //hide the notify?
        fragmentBackgrounded = false;
        try {
            if (runPhase != 1) notificationManager.cancel(notificationId);//TODO please fix these disgusting hardcoded ids
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("foregrounded!"); runs when app is opened from minimized state
    }*/

    public void onbtnGenScramblesClicked() {
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

    public void onbtnStartClicked() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("timerState",Context.MODE_PRIVATE);
        if (runPhase==1) { //attempt inactive, now go into memo
            //start running
            runPhase++;//now in memo
            //Use shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("time", new Date().getTime());
            editor.putInt("attempted",attempted);
            editor.putInt("runPhase",runPhase);
            editor.apply();
            tvResultDisplay.setVisibility(View.VISIBLE);
            tvResultDisplay.setText(attempted + " cube attempt");

            handler.postDelayed(runnable,1000);//starts the worker? thread, it will loop within itself now. NOW IN TimerBackgroundService.java
            btnStart.setText("Split");

        } else if (runPhase==2) {//in memo, now go into exec
            runPhase++;
            btnStart.setText("Stop");
            phase1 = totalSeconds;//get current time as memo time
            sharedPreferences.edit().putInt("runPhase",runPhase).apply();
            sharedPreferences.edit().putInt("phase1",phase1).apply();

        } else {//in exec aka runPhase = 3, now stop
            handler.removeCallbacks(runnable);//stops the infinite runnable loop
            sharedPreferences.edit().clear().apply();
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
            btnEditResult.setVisibility(View.VISIBLE);
            tvAmountSolved.setVisibility(View.VISIBLE);
            tvResultDisplay.setText(helper.encodeTime(totalSeconds)+"["+helper.encodeTime(phase1)+"]");
        }
    }

    public void onbtnNextAttemptClicked() {
        //hide
        btnNewAttempt.setVisibility(View.INVISIBLE);
        edtAmountSolved.setVisibility(View.INVISIBLE);
        tvAmountSolved.setVisibility(View.INVISIBLE);
        edtComment.setVisibility(View.INVISIBLE);
        tvResultDisplay.setVisibility(View.INVISIBLE);
        btnEditResult.setVisibility(View.INVISIBLE);
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

    public void onbtnEditResultClicked() {
        showEditResultDialog(getContext());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGetScrambles:
                onbtnGenScramblesClicked();
                break;
            case R.id.btnNextAttempt:
                onbtnNextAttemptClicked();
                break;
            case R.id.btnStartAttempt:
                onbtnStartClicked();
                break;
            case R.id.btnEditResult:
                onbtnEditResultClicked();
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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            totalSeconds++;//TODO this is for some more accurate timings, it should be ++
            String time = helper.encodeTime(totalSeconds);
            tvTimer.setText(time);
            handler.postDelayed(this, 1000);//calls itself, ie the loop that keeps updating timer is called within itself
        }
    };

    private void getSharedPrefInfo() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("timerState",Context.MODE_PRIVATE);
        attempted = sharedPreferences.getInt("attempted",0);
        if (attempted != 0) {//if actually doing an attempt
            //System.out.println("should be busy with an attempt");
            runPhase = sharedPreferences.getInt("runPhase",2);
            totalSeconds = (int) ((new Date().getTime() - sharedPreferences.getLong("time",0))/1000);//returns total time that has elapsed since attempt started
            tvTimer.setText(helper.encodeTime(totalSeconds));
            handler.postDelayed(runnable,1000);

            //setup components as if in an attempt
            edtCubeAmount.setVisibility(View.INVISIBLE);
            btnGen.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            tvTimer.setVisibility(View.VISIBLE);
            //updating labels and getting phase time where applicable
            if (runPhase == 2) {
                //if in memo
                btnStart.setText("Split");
            }
            else if (runPhase == 3) {
                //if in exec
                phase1 = sharedPreferences.getInt("phase1",0);
                btnStart.setText("Stop");
            }
        }

    }

    public void showEditResultDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //inflater to create or build the layout from the xml file
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_result,null);//this is crucial for me to get components on this view later!
        builder.setView(dialogView);
        //getting component references
        EditText edtDialogSolved = dialogView.findViewById(R.id.edtCubesSolved);
        EditText edtDialogAttempted = dialogView.findViewById(R.id.edtCubesAttempted);
        EditText edtDialogMemo = dialogView.findViewById(R.id.edtMemoTime);
        EditText edtDialogExec = dialogView.findViewById(R.id.edtExecTime);

        edtDialogAttempted.setText(Integer.toString(attempted));
        edtDialogMemo.setText(helper.encodeTime(phase1));
        edtDialogExec.setText(helper.encodeTime(phase2));

        //the buttons are somehow automatically appending to the layout on the bottom, along with their onclick listeners
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //do yes stuff

                String dialogAttVal = edtDialogAttempted.getText().toString();
                String dialogSolvedVal = edtDialogSolved.getText().toString();
                String dialogMemoVal = edtDialogMemo.getText().toString();
                String dialogExecVal = edtDialogExec.getText().toString();
                if (!dialogAttVal.equals("")) attempted = Integer.parseInt(dialogAttVal);
                if (!dialogSolvedVal.equals("")) solved = Integer.parseInt(dialogAttVal);

                if (!dialogMemoVal.equals("")) {
                    int memoSeconds;
                    String[] split = dialogMemoVal.split(":");
                    if (split.length == 3) {
                        memoSeconds = Integer.parseInt(split[0])*3600 + Integer.parseInt(split[1])*60 + Integer.parseInt(split[2]);
                    } else if (split.length == 2) {
                        memoSeconds = Integer.parseInt(split[0])*60 + Integer.parseInt(split[1]);
                    } else memoSeconds = 0;

                    phase2 = memoSeconds;
                }

                if (!dialogExecVal.equals("")) {
                    int execSeconds;
                    String[] split = dialogExecVal.split(":");
                    if (split.length == 3) {
                        execSeconds = Integer.parseInt(split[0])*3600 + Integer.parseInt(split[1])*60 + Integer.parseInt(split[2]);
                    } else if (split.length == 2) {
                        execSeconds = Integer.parseInt(split[0])*60 + Integer.parseInt(split[1]);
                    } else execSeconds = 0;

                    phase2 = execSeconds;
                }

                tvResultDisplay.setText(helper.encodeTime(totalSeconds)+"["+helper.encodeTime(phase2)+"]");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                //do no stuff
            }
        });
        builder.create();
        builder.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        //this is called whenever you swipe from adjacent tabs or maximize the app
    }
}