package com.project.richard.insightjournal.utils;

import android.content.ContentValues;

import com.project.richard.insightjournal.database.PresetsColumns;

/**
 * Created by a11 on 6/14/16.
 */
public class ContentValuesUtil {

    public static ContentValues presetContentValues(String title, int prepTime, int duration){
        ContentValues cv = new ContentValues();
        cv.put(PresetsColumns.TITLE, title);
        cv.put(PresetsColumns.PREPARATION_TIME, prepTime);
        cv.put(PresetsColumns.DURATION, duration);
        return cv;
    }
}
