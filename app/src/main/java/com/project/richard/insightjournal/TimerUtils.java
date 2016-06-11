package com.project.richard.insightjournal;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by a11 on 6/10/16.
 */
public class TimerUtils {
    public static ArrayList<String> createSecondsArrayList(Context context){
        ArrayList<String> secArray = new ArrayList<>();
        secArray.add(0, 0 + " " + context.getString(R.string.second_spinner_text));
        secArray.add(1, 1 + " " + context.getString(R.string.second_spinner_text));
        for(int i = 2; i <= 60; i++){
            secArray.add(i, i + " " + context.getResources().getString(R.string.seconds_spinner_text));
        }
        return secArray;
    }
    public static ArrayList<String> createMinutesArrayList(Context context){
        ArrayList<String> minArray = new ArrayList<>();
        minArray.add(0, 0 + " " + context.getString(R.string.minute_spinner_text));
        minArray.add(1, 1 + " " + context.getString(R.string.minute_spinner_text));
        for(int i = 2; i <= 60; i++){
            minArray.add(i, i + " " + context.getResources().getString(R.string.minutes_spinner_text));
        }
        return minArray;
    }
    public static ArrayList<String> createHoursArrayList(Context context){
        ArrayList<String> hourArray = new ArrayList<>();
        hourArray.add(0, 0 + " " + context.getString(R.string.hour_spinner_text));
        hourArray.add(1, 1 + " " + context.getString(R.string.hour_spinner_text));
        for(int i = 2; i <= 24; i++){
            hourArray.add(i, i + " " + context.getResources().getString(R.string.hours_spinner_text));
        }
        return hourArray;
    }
}
