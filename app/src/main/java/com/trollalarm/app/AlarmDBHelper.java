package com.trollalarm.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aaron on 06/06/2014.
 */
public class AlarmDBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final String SQL_ALARM_TABLE_NAME = "alarms";
    public static final String SQL_ALARM_ID = "id";
    public static final String SQL_ALARM_HOUR = "hour";
    public static final String SQL_ALARM_MINUTE = "minute";
    public static final String SQL_ALARM_SCHEDULEFLAGS = "scheduleflags";
    public static final String SQL_ALARM_METHOD = "method";
    public static final String SQL_ALARM_ISON = "isOn";

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Alarm.db";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + SQL_ALARM_TABLE_NAME + " (" +
            SQL_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            SQL_ALARM_HOUR + " INTEGER NOT NULL," +
            SQL_ALARM_MINUTE + " INTEGER NOT NULL," +
            SQL_ALARM_METHOD + " INTEGER NOT NULL," +
            SQL_ALARM_SCHEDULEFLAGS + " INTEGER NOT NULL," +
            SQL_ALARM_ISON + " INTEGER NOT NULL" +
            ")";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + SQL_ALARM_TABLE_NAME;



    public AlarmDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
