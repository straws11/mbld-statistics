package com.example.mbldapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AttemptingFragment extends Fragment implements View.OnClickListener {
    //vars
    private Handler handler = new Handler();
    MyHelpers helper = new MyHelpers();//gives me access to my helper methods like encoding time and saving and reading attempts
    EditText edtAmountSolved;
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
        //setContentView(R.layout.activity_attempting); - handled by onCreateView ?
        //declared globally, assigned AFTER setting content view above
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

        return view;
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
        //debug display points Toast.makeText(this,Integer.toString(mbldAttempt.getScore()),Toast.LENGTH_SHORT).show();
        //SAVING
        helper.saveAttempts(getActivity(),mbldAttempt);
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
    //old method, replaced in MyHelpers.java
   /* public void saveAttempt(MBLDAttempt mbldAttempt) {
        // Convert JSON File to Java Object
        Reader reader = null;
        MyHelpers helper = new MyHelpers();
        List<MBLDAttempt> attempts = helper.readAttempts(getActivity());

        if (attempts==null) {//no attempts exist create empty array
            attempts = new ArrayList<>();
        }

        //add new attempt
        attempts.add(mbldAttempt);

        //save using GSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter writer = new FileWriter(new File(getActivity().getFilesDir(),"attempts.json"),false);
            gson.toJson(attempts, writer);
            writer.close();
            Toast.makeText(getActivity(),"Attempt Logged",Toast.LENGTH_LONG).show();

            //update RecyclerView adapter with new data point


        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


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
            totalSeconds+=50;//TODO this is for some more accurate timings, it should be ++
            String time = helper.encodeTime(totalSeconds);
            tvTimer.setText(time);
            handler.postDelayed(this, 1000);//calls itself, ie the loop that keeps updating timer is called within itself
        }//end of public void
    }; //end of private runnable variable?

  /*  public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
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
    }*/
}