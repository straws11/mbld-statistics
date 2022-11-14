package com.example.mbldapp;

import java.util.ArrayList;

public class AttemptListItem {//this class is purely for each item that'll be listed

    private String result;
    private String date;


    public AttemptListItem(String pResult, String pDate) {
        //constructor
        result = pResult;
        date = pDate;
    }


    public String getResult() {
        return result;
    }

    public String getDate() {
        return date;
    }

}
