package com.example.gallery.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventBaseHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "eventBase.db";

    public EventBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + EventDbShema.EventTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                        EventDbShema.EventTable.Cols.UUID + ", " +
                        EventDbShema.EventTable.Cols.TITLE + ", " +
                        EventDbShema.EventTable.Cols.DATE + ", " +
                        EventDbShema.EventTable.Cols.DESCRIPTION + ", " +
                        EventDbShema.EventTable.Cols.LATITUDE + ", " +
                        EventDbShema.EventTable.Cols.LONGITUDE +
                ")"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
