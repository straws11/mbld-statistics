package com.example.mbldapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MyHelpers {
    public String encodeTime(int seconds) {//TODO format based on the actual time, dont include HH if not necessary.
        int hours = seconds / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours,
                mins, secs);
        return time;
    }

    public ArrayList<MBLDAttempt> readAttempts(Context context) {
        ArrayList<MBLDAttempt> attempts;
        Reader reader;
        //reads all attempts from the json file
        try {
            reader = new FileReader(context.getFilesDir()+"/attempts.json");
            Type attemptsListType = new TypeToken<ArrayList<MBLDAttempt>>(){}.getType();
            attempts = new Gson().fromJson(reader,attemptsListType);
            //Collections.reverse(attempts);
            //this sorts the array ascending
            Collections.sort(attempts, new Comparator<MBLDAttempt>(){
                public int compare(MBLDAttempt obj1, MBLDAttempt obj2) {
                    // ## Ascending order
                    return Long.compare(obj2.getDateDuration(),obj1.getDateDuration());
                }
            });
        } catch (FileNotFoundException e) {//if not found
            e.printStackTrace();
            return null;//didn't find it
        }
        return attempts;
    }

    public void saveAttempts(Context context, MBLDAttempt mbldAttempt) {
            // Convert JSON File to Java Object
            Reader reader = null;
            MyHelpers helper = new MyHelpers();
            List<MBLDAttempt> attempts;

            if ((helper.readAttempts(context))== null) {
                attempts = new ArrayList<>();
            } else {
                attempts = helper.readAttempts(context);
            }

            //add new attempt
            attempts.add(mbldAttempt);

            //save using GSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try {
                FileWriter writer = new FileWriter(new File(context.getFilesDir(),"attempts.json"),false);
                gson.toJson(attempts, writer);
                writer.close();
                Toast.makeText(context,"Attempt Logged",Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void showDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //setup of my two input fields
        //final EditText input = new EditText(context);
        //final EditText input2 = new EditText(context);
        LayoutInflater inflater = (activity).getLayoutInflater();

        //input.setInputType(InputType.TYPE_CLASS_NUMBER);
        //input2.setInputType(InputType.TYPE_CLASS_NUMBER);

        //builder.setView(input);
        //builder.setView(input2);
        builder.setView(inflater.inflate(R.layout.dialog_cube_range,null));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //do yes stuff
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
}
