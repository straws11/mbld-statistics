package com.example.mbldapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MBLDAttempt { //inner class. MultiBlind contains MBlDAttempt
    private int solved;
    private int attempted;
    private int phase1Time;
    private int phase2Time;
    private String date;

    //constructor
    MBLDAttempt(int solved, int attempted, int phase1Time, int phase2Time) {//for attempts with multiphase
        this.solved = solved;
        this.attempted = attempted;
        this.phase1Time = phase1Time;
        this.phase2Time = phase2Time;
        this.date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    public int getScore() {
        return this.solved - (this.attempted - this.solved);
    }

    @Override
    public String toString() {
        int hours = phase1Time / 3600;
        int mins = (phase1Time % 3600) / 60;
        int secs = phase1Time % 60;
        String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours,
                mins, secs);
        return solved + "/" + attempted + " in " + time;

    }
}

