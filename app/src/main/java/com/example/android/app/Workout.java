package com.example.android.app;

import java.util.Date;
import java.util.UUID;
public class Workout {
    private UUID mId;
    private String mTitle;
    private Date mDate;


    public Workout() {
        // Generate unique identifier
        this(UUID.randomUUID());
    }

    public Workout(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

}
