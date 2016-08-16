package com.project.richard.insightjournal.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by a11 on 6/14/16.
 */
public class SharedPrefUtils {
    public static final String TIMER_SETTING_SCREEN_PREF = "timer_setting_screen_pref";
    public static final String EMPTY_PRESET_PREF = "empty_preset_pref";
    public static final String PRESET_TITLE_PREF = "preset_title_pref";
    public static final String SHORT_TERM_GOALS_PREF = "short_term_goals_pref";
    public static final String LONG_TERM_GOALS_PREF = "long_term_goals_pref";
    public static final String IS_FIRST_START = "is_first_start";

    public static boolean isFirstStart(Context context){
        SharedPreferences getPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        Log.e("BLAH", getPrefs.getBoolean(IS_FIRST_START, true) + " ??");
        return getPrefs.getBoolean(IS_FIRST_START, true);
    }

    public static void setFirstStart(Context context){
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean(IS_FIRST_START, false).apply();
    }

    public static void setTitlePref(Context context, String title){
        SharedPreferences.Editor editor = context.getSharedPreferences(TIMER_SETTING_SCREEN_PREF, 0).edit();
        editor.putString(PRESET_TITLE_PREF, title).commit();
    }

    public static String getTypePref(Context context){
        SharedPreferences pref = context.getSharedPreferences(TIMER_SETTING_SCREEN_PREF, 0);
        return pref.getString(PRESET_TITLE_PREF, EMPTY_PRESET_PREF);
    }

}
