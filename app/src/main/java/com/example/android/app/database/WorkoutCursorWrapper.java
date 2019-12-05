package com.example.android.app.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.android.app.Workout;

import java.util.Date;
import java.util.UUID;


public class WorkoutCursorWrapper extends CursorWrapper {
    public WorkoutCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Workout getWorkout() {
        String uuidString = getString(getColumnIndex(WorkoutDbSchema.WorkoutTable.Cols.UUID));
        String title = getString(getColumnIndex(WorkoutDbSchema.WorkoutTable.Cols.TITLE));
        long date = getLong(getColumnIndex(WorkoutDbSchema.WorkoutTable.Cols.DATE));

        Workout workout = new Workout(UUID.fromString(uuidString));
        workout.setTitle(title);
        workout.setDate(new Date(date));

        return workout;
    }
}
