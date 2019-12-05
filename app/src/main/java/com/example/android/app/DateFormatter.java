package com.example.android.app;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class DateFormatter {
    /*
     * Disclaimer: I copied this code directly from the big nerd ranch github and did not change a thing.
     */
    public static String formatDateAsString(int dateStyle, Date date) {
        DateFormat formatter = DateFormat.getDateInstance(dateStyle, Locale.getDefault());
        return formatter.format(date);
    }

    public static String formatDateAsTimeString(int dateStyle, Date date) {
        DateFormat formatter = DateFormat.getTimeInstance(dateStyle, Locale.getDefault());
        return formatter.format(date);
    }
}
