package com.example.mbldapp;

import android.content.Context;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class MyHelpers {
    public String encodeTime(int seconds) {
        int hours = seconds / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time;
        if (seconds>=3600) {
            time = String.format(Locale.getDefault(),"%d:%02d:%02d",hours,mins,secs);
        } else if (seconds>=60) {
            time = String.format(Locale.getDefault(),"%02d:%02d",mins,secs);
        } else {
            time = String.format(Locale.getDefault(),"%d",seconds);
        }
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

    public void saveAttempt(Context context, MBLDAttempt mbldAttempt) {
            // Convert JSON File to Java Object
        MyHelpers helper = new MyHelpers();
        ArrayList<MBLDAttempt> attempts;

        if ((helper.readAttempts(context))== null) {
            attempts = new ArrayList<>();
        } else {
            attempts = helper.readAttempts(context);
        }

        //add new attempt and save
        attempts.add(mbldAttempt);
        saveAttemptsToFile(context, attempts);
        Toast.makeText(context,"Attempt Logged",Toast.LENGTH_LONG).show();
    }

    public void saveAttemptsToFile(Context context, ArrayList<MBLDAttempt> attempts) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter writer = new FileWriter(new File(context.getFilesDir(),"attempts.json"),false);
            gson.toJson(attempts,writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
