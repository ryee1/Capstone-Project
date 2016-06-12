package com.project.richard.insightjournal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by a11 on 6/4/16.
 */
@Database(version = LogsDatabase.VERSION, packageName = "com.project.richard.insightjournal")
public final class LogsDatabase {
    public static final int VERSION = 1;

    @Table(LogsColumns.class) public static final String LOGS = "logs";
    @Table(PresetsColumns.class) public static final String PRESETS = "presets";

//    @OnCreate public static void onCreate(Context context, SQLiteDatabase db) {
//    }
//
//    @OnUpgrade public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
//                                            int newVersion) {
//    }
//
//    @OnConfigure public static void onConfigure(SQLiteDatabase db) {
//    }
}
