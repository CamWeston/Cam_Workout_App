package com.example.android.app.database;

public class WorkoutDbSchema {
    public static final class WorkoutTable {
        public static final String NAME = "workout";
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
        }
    }
}
