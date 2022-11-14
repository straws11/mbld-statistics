package com.example.mbldapp;

import java.util.ArrayList;

public class AttemptListItem {

    private String result;
    private int score;


    public AttemptListItem(String pResult, int pScore) {
        //constructor
        result = pResult;
        score = pScore;
    }

    public int getScore() {
        return score;
    }

    public String getResult() {
        return result;
    }


    public static ArrayList<AttemptListItem> createAttemptList(int numAttempts) {
        ArrayList<AttemptListItem> attempts = new ArrayList<>();



        return attempts;
    }
}
