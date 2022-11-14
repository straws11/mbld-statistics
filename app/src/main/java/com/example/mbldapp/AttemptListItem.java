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

    private static int randomtestId = 0;

    public static ArrayList<AttemptListItem> createAttemptList(int numAttempts) {
        ArrayList<AttemptListItem> attempts = new ArrayList<>();

        for (int i = 1; i <= numAttempts; i++) {
            attempts.add(new AttemptListItem("Person " + ++randomtestId, i*10));
        }
        return attempts;
    }
}
