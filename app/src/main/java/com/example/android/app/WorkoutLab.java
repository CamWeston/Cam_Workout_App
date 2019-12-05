package com.example.android.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.app.database.WorkoutBaseHelper;
import com.example.android.app.database.WorkoutCursorWrapper;
import com.example.android.app.database.WorkoutDbSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class WorkoutLab {


    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static WorkoutLab sWorkoutLab;



    public static WorkoutLab get(Context context) {
        if (sWorkoutLab == null) {
            sWorkoutLab = new WorkoutLab(context);
        }

        return sWorkoutLab;
    }

    public void addWorkout(Workout w) {
        ContentValues values = getContentValues(w);

        mDatabase.insert(WorkoutDbSchema.WorkoutTable.NAME, null, values);
    }

    public void updateWorkout(Workout workout) {
        String uuidString = workout.getId().toString();
        ContentValues values = getContentValues(workout);

        mDatabase.update(WorkoutDbSchema.WorkoutTable.NAME, values,
                WorkoutDbSchema.WorkoutTable.Cols.UUID + "=?",
                new String[]{uuidString});
    }

    public void deleteWorkout(Workout workout) {
        mDatabase.delete(
                WorkoutDbSchema.WorkoutTable.NAME,
                WorkoutDbSchema.WorkoutTable.Cols.UUID + "=?",
                new String[] {workout.getId().toString()}
        );
    }

    public List<Workout> getWorkouts() {
        List<Workout> workouts = new ArrayList<>();

        WorkoutCursorWrapper cursor = queryWorkouts(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                workouts.add(cursor.getWorkout());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return workouts;
    }

    public Workout getWorkout(UUID id) {
        WorkoutCursorWrapper cursor = queryWorkouts(
                WorkoutDbSchema.WorkoutTable.Cols.UUID + "=?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getWorkout();
        } finally {
            cursor.close();
        }
    }


    private static ContentValues getContentValues(Workout workout) {
        ContentValues values = new ContentValues();
        values.put(WorkoutDbSchema.WorkoutTable.Cols.UUID, workout.getId().toString());
        values.put(WorkoutDbSchema.WorkoutTable.Cols.TITLE, workout.getTitle());
        values.put(WorkoutDbSchema.WorkoutTable.Cols.DATE, workout.getDate().getTime());
        return values;
    }

    private WorkoutCursorWrapper queryWorkouts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                WorkoutDbSchema.WorkoutTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null //
        );

        return new WorkoutCursorWrapper(cursor);
    }

    private WorkoutLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new WorkoutBaseHelper(mContext)
                .getWritableDatabase();
    }
}
