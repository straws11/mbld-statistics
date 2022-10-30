package com.example.mbldapp;

import java.util.List;
import java.util.Locale;

public class MBLDAttempt { //inner class. MultiBlind contains MBlDAttempt
    private int solved;
    private int attempted;
    private int score;
    private int phase1Time;
    private int phase2Time;

    MBLDAttempt(int solved, int attempted, int phase1Time) {//constructor for attempts without multiphase
        this.solved = solved;
        this.attempted = attempted;
        this.phase1Time = phase1Time;
        this.phase2Time = 0;
        this.score = this.solved - (this.attempted - this.solved);
    }

    MBLDAttempt(int solved, int attempted, int phase1Time, int phase2Time) {//for attempts with multiphase
        //do nothing for now
    }

    public int getScore() {
        return score;
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

