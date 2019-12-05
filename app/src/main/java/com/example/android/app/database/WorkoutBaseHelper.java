package com.example.android.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Sqlite open helper to set up database and handle version upgrades
 */

public class WorkoutBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "workoutBase.db";

    public WorkoutBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + WorkoutDbSchema.WorkoutTable.NAME + "(" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WorkoutDbSchema.WorkoutTable.Cols.UUID + " INTEGER, " +
                WorkoutDbSchema.WorkoutTable.Cols.TITLE + " TEXT, " +
                WorkoutDbSchema.WorkoutTable.Cols.DATE + " INTEGER " +
                ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
