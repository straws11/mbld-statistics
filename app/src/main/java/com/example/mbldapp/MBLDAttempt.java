package com.example.mbldapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MBLDAttempt implements Serializable {//implements because it allows me to attach a mbldAttempt obj onto an intent in historyfragment
    private int solved;
    private int attempted;
    private int phase1Time;
    private int phase2Time;
    private String dateTime;
    private String comment;
    private ArrayList<String> scrambles = new ArrayList<>();

    //helper
    MyHelpers helper = new MyHelpers();

    //constructor
    MBLDAttempt(int solved, int attempted, int phase1Time, int phase2Time, String comment) {//for attempts with multiphase
        this.solved = solved;
        this.attempted = attempted;
        this.phase1Time = phase1Time;
        this.phase2Time = phase2Time;
        this.dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());//FIX DATE
        this.comment = comment;
    }

    public String getResult() {
        return this.solved+ "/" + this.attempted;
    }

    public int getPoints() {
        return this.solved - (this.attempted - this.solved);
    }

    public String getExecPerCube() {
        return helper.encodeTime(Math.round((float) this.phase2Time / this.attempted));
    }

    public String getMemoPerCube() {
        return helper.encodeTime(Math.round((float) this.phase1Time / this.attempted));
    }

    public String getDate() {
        return this.dateTime;
    }

    public String getPhase1() {
        return helper.encodeTime(this.phase1Time);
    }

    public String getPhase2() {
        return helper.encodeTime(this.phase2Time);
    }

    public String getTotalTime() {
        return helper.encodeTime(this.phase1Time+this.phase2Time);
    }

    public String getComment() {
        return this.comment;
    }

    public String getComplexTime() {
        return getTotalTime()+"["+getPhase1()+"]";
    }

    @Override
    public String toString() {
        return solved + "/" + attempted + " in " + getTotalTime();
    }

}

