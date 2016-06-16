package com.project.richard.insightjournal.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by a11 on 6/14/16.
 */
public class SharedPrefUtils {
    public static final String MAINSCREEN_PREF = "main_screen_pref";
    public static final String EMPTY_PRESET_PREF = "empty_preset_pref";
    public static final String PRESET_TITLE_PREF = "preset_title_pref";

    public static void addTitlePref(Context context, String title){
        SharedPreferences.Editor editor = context.getSharedPreferences(MAINSCREEN_PREF, 0).edit();
        editor.putString(PRESET_TITLE_PREF, title).commit();
    }

    public static String getTitlePref(Context context){
        SharedPreferences pref = context.getSharedPreferences(MAINSCREEN_PREF, 0);
        return pref.getString(PRESET_TITLE_PREF, EMPTY_PRESET_PREF);
    }
}
