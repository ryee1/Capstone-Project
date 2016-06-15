package com.project.richard.insightjournal.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by a11 on 6/14/16.
 */
public class SharedPrefUtils {
    public static final String MAINSCREEN_PREF = "main_screen_pref";

    public static final String PRESET_ID_PREF = "preset_id_pref";

    public static void addIdPref(Context context, int id){
        SharedPreferences.Editor editor = context.getSharedPreferences(MAINSCREEN_PREF, 0).edit();
        editor.putInt(PRESET_ID_PREF, id).commit();
    }

    public static int getIdPref(Context context){
        SharedPreferences pref = context.getSharedPreferences(MAINSCREEN_PREF, 0);
        return pref.getInt(PRESET_ID_PREF, 0);
    }
}
