package com.example.android.app;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;
/*
 * Disclaimer: I copied this code directly from the big nerd ranch github and did not change a thing.
 */
public class DatePickerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {

        Date date = (Date) getIntent().getSerializableExtra(WorkoutFragment.EXTRA_DATE);
        return DatePickerFragment.newInstance(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
