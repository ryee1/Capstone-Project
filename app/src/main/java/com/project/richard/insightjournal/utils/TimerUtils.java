package com.project.richard.insightjournal.utils;

import android.content.Context;

import com.project.richard.insightjournal.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by a11 on 6/10/16.
 */
public class TimerUtils {

    private static final String TAG = TimerUtils.class.getSimpleName();
    public static ArrayList<String> createSecondsArrayList(Context context) {
        ArrayList<String> secArray = new ArrayList<>();
        secArray.add(0, 0 + " " + context.getString(R.string.second_spinner_text));
        secArray.add(1, 1 + " " + context.getString(R.string.second_spinner_text));
        for (int i = 2; i <= 60; i++) {
            secArray.add(i, i + " " + context.getResources().getString(R.string.seconds_spinner_text));
        }
        return secArray;
    }

    public static ArrayList<String> createMinutesArrayList(Context context) {
        ArrayList<String> minArray = new ArrayList<>();
        minArray.add(0, 0 + " " + context.getString(R.string.minute_spinner_text));
        minArray.add(1, 1 + " " + context.getString(R.string.minute_spinner_text));
        for (int i = 2; i <= 60; i++) {
            minArray.add(i, i + " " + context.getResources().getString(R.string.minutes_spinner_text));
        }
        return minArray;
    }

    public static ArrayList<String> createHoursArrayList(Context context) {
        ArrayList<String> hourArray = new ArrayList<>();
        hourArray.add(0, 0 + " " + context.getString(R.string.hour_spinner_text));
        hourArray.add(1, 1 + " " + context.getString(R.string.hour_spinner_text));
        for (int i = 2; i <= 24; i++) {
            hourArray.add(i, i + " " + context.getResources().getString(R.string.hours_spinner_text));
        }
        return hourArray;
    }

    public static long millisToMillisRemaining(long maxDuration, long duration){
        return maxDuration - duration;
    }

    public static String millisToDigital(long duration){
        return String.format("%02d:%02d:%02d", millisToHours(duration), millisToMinutes(duration),
                millisToSeconds(duration));
    }

    public static String unixTimeToTime(long unixTime){
        Date date = new Date(unixTime);
        DateFormat format = SimpleDateFormat.getTimeInstance();
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }
    public static String unixTimeToDate(long unixTime){
        Date date = new Date(unixTime);
        DateFormat format = SimpleDateFormat.getDateInstance();
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public static long millisToHours(long duration){
        return TimeUnit.MILLISECONDS.toHours(duration);
    }
    public static long millisToMinutes(long duration){
        return TimeUnit.MILLISECONDS.toMinutes(duration) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
    }
    public static long millisToSeconds(long duration){
        return TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
    }
}
