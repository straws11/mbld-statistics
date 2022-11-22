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
            Collections.reverse(attempts);
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

                //update RecyclerView adapter with new data point


            } catch (IOException e) {
                e.printStackTrace();
            }
        }//end of save method
}
