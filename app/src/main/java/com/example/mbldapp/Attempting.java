package com.example.mbldapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Locale;

public class Attempting extends AppCompatActivity {
    //vars
    private Handler handler = new Handler();
    EditText edtAmountSolved;
    EditText edtCubeAmount;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempting);
        //declared globally, assigned AFTER setting content view above
        edtCubeAmount = findViewById(R.id.edtCubeAmount);
        edtAmountSolved = findViewById(R.id.edtSolved);
        btnGen = findViewById(R.id.btnGetScrambles);
        btnStart = findViewById(R.id.btnStartAttempt);
        btnNewAttempt = findViewById(R.id.btnNextAttempt);
        tvTimer = findViewById(R.id.tvTimer);
        tvResultDisplay = findViewById(R.id.tvResult);
        tvAmountSolved = findViewById(R.id.tvSolved);

    }

    public void onbtnGenScramblesClicked(View view) {
        //hides components that get my scramble count
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 100);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101);

        edtCubeAmount.setVisibility(View.GONE);
        btnGen.setVisibility(View.GONE);
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
            handler.postDelayed(runnable,1000);//starts the worker? thread, it will loop within itself now
            btnStart.setText("Split");

        } else if (runPhase==2) {//in memo, now go into exec
            runPhase++;
            btnStart.setText("Stop");//TODO change text when multiphase is enabled so it says End Memo or smth
            phase1 = totalSeconds;//get current time as memo time

        } else {//in exec aka runPhase = 3, now stop
            handler.removeCallbacks(runnable);//stops the infinite runnable loop
            phase2 = totalSeconds-phase1;//get exec time
            runPhase=1;//resets var
            btnStart.setVisibility(View.GONE);
            btnStart.setText("Start");
            tvTimer.setVisibility(View.GONE);
            btnNewAttempt.setVisibility(View.VISIBLE);
            edtAmountSolved.setVisibility(View.VISIBLE);
            tvAmountSolved.setVisibility(View.VISIBLE);
        }
    }

    public void onbtnNextAttemptClicked(View view) {
        //hide
        btnNewAttempt.setVisibility(View.GONE);
        edtAmountSolved.setVisibility(View.GONE);
        tvAmountSolved.setVisibility(View.GONE);
        //show
        btnGen.setVisibility(View.VISIBLE);
        edtCubeAmount.setVisibility(View.VISIBLE);
        //save attempt
        //create mbld object
        solved = Integer.parseInt(edtAmountSolved.getText().toString());
        MBLDAttempt mbldAttempt = new MBLDAttempt(solved,attempted,phase1,phase2);
        //Resetting vars
        phase1 = 0;
        phase2 = 0;
        totalSeconds = 0;
        //debug display points Toast.makeText(this,Integer.toString(mbldAttempt.getScore()),Toast.LENGTH_SHORT).show();
        //SAVING
        saveAttempt(mbldAttempt);
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
        else {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveAttempt(MBLDAttempt mbldAttempt) {
        // Convert JSON File to Java Object
        Reader reader = null;
        List<MBLDAttempt> attempts = readAttempts();

        if (attempts==null) {//no attempts exist create empty array
            attempts = new ArrayList<>();
        }

        //add new attempt
        attempts.add(mbldAttempt);

        //save using GSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter writer = new FileWriter(new File(getApplicationContext().getFilesDir(),"attempts.json"),false);
            gson.toJson(attempts, writer);
            writer.close();
            Toast.makeText(getApplicationContext(),"Attempt Logged",Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }//end of save method

    public ArrayList<MBLDAttempt> readAttempts() {
        ArrayList<MBLDAttempt> attempts = new ArrayList<>();
        Reader reader = null;
        //reads all attempts from the json file
        try {
            reader = new FileReader(getApplicationContext().getFilesDir()+"/attempts.json");
            Type attemptsListType = new TypeToken<ArrayList<MBLDAttempt>>(){}.getType();
            attempts = new Gson().fromJson(reader,attemptsListType);
        } catch (FileNotFoundException e) {//if not found
            e.printStackTrace();
            return null;//didn't find it
        }
        return attempts;
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
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
         else if (requestCode == 101) {
             if (grantResults.length > 0
                     && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 Toast.makeText(this, "Storage Read Permission Granted", Toast.LENGTH_SHORT).show();
             } else {
                 Toast.makeText(this, "Storage Read Permission Denied", Toast.LENGTH_SHORT).show();
             }
         }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            totalSeconds++;
            String time = encodeTime(totalSeconds);
            tvTimer.setText(time);
            handler.postDelayed(this, 1000);//calls itself, ie the loop that keeps updating timer is called within itself
        }//end of public void
    }; //end of private runnable variable?

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setTitle("Enter Amount Solved");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                solved = Integer.parseInt(input.getText().toString());//parseint is essentially strtoint
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                //Toast.makeText(getApplicationContext(),"loser",Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public String encodeTime(int seconds) {
        int hours = seconds / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;
        //TODO make format only switch over to mm:ss and hh:mm:ss when it is necessary, not always 00:00:xy
        String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours,
                mins, secs);
        return time;
    }
}
