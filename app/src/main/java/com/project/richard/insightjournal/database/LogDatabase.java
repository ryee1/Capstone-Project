//package com.project.richard.insightjournal.database;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//
//import net.simonvt.schematic.annotation.OnConfigure;
//import net.simonvt.schematic.annotation.OnCreate;
//import net.simonvt.schematic.annotation.OnUpgrade;
//import net.simonvt.schematic.annotation.Table;
//
///**
// * Created by a11 on 6/4/16.
// */
//public final class LogDatabase {
//    public static final int VERSION = 1;
//
//    @Table(LogColumns.class) public static final String LOGS = "logs";
//
//    @OnCreate public static void onCreate(Context context, SQLiteDatabase db) {
//    }
//
//    @OnUpgrade public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
//                                            int newVersion) {
//    }
//
//    @OnConfigure public static void onConfigure(SQLiteDatabase db) {
//    }
//}
