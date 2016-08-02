package com.project.richard.insightjournal.utils;

import android.content.ContentValues;

import com.project.richard.insightjournal.database.LogsColumns;
import com.project.richard.insightjournal.database.PresetsColumns;

/**
 * Created by a11 on 6/14/16.
 */
public class ContentValuesUtils {

    public static ContentValues presetContentValues(String type, int prepTime, int duration, int recordToggleOn){
        ContentValues cv = new ContentValues();
        cv.put(PresetsColumns.TYPE, type);
        cv.put(PresetsColumns.PREPARATION_TIME, prepTime);
        cv.put(PresetsColumns.DURATION, duration);
        cv.put(PresetsColumns.RECORD_TOGGLE_ON, recordToggleOn);
        return cv;
    }

    public static ContentValues stopTimerDialogContentValues(long duration, long date, String title,
                                                             String journal, String goalsJson){
        ContentValues cv = new ContentValues();
        cv.put(LogsColumns.SESSION_DURATION, duration);
        cv.put(LogsColumns.SESSION_DATETIME, date);
        cv.put(LogsColumns.TITLE, title);
        cv.put(LogsColumns.JOURNAL_ENTRY, journal);
        cv.put(LogsColumns.GOALS, goalsJson);
        return cv;
    }
}
