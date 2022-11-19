package com.example.mbldapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MBLDAttempt implements Serializable {//implements because it allows me to attach a mbldAttempt obj onto an intent in historyfragment
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

    public String getResult() {
        return this.solved+ "/" + this.attempted;
    }

    public int getScore() {
        return this.solved - (this.attempted - this.solved);
    }

    public String getDate() {
        return this.date;
    }

    public String getPhase1() {
        return encodeTime(this.phase1Time);
    }

    public String getPhase2() {
        return encodeTime(this.phase2Time);
    }

    public String getTotalTime() {
        return encodeTime(this.phase1Time+this.phase2Time);
    }

    @Override
    public String toString() {
        int totalTime = phase1Time + phase2Time;
        int hours = totalTime / 3600;
        int mins = (totalTime % 3600) / 60;
        int secs = totalTime % 60;
        String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours,
                mins, secs);
        return solved + "/" + attempted + " in " + time;

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

