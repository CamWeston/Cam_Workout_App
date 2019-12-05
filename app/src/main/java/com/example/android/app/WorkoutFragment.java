package com.example.android.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;


public class WorkoutFragment extends Fragment {
    private static final String WORKOUT_FRAGMENT = "WorkoutFragment";
    private static final String ARG_WORKOUT_ID = "workout_id";

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_IMAGE = "DialogImage";

    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    public static final int ACTIVITY_REQUEST_DATE = 3;
    public static final int ACTIVITY_REQUEST_TIME = 4;
    private static final int DATE_FORMAT = DateFormat.FULL;
    private static final int TIME_FORMAT = DateFormat.SHORT;

    private Workout mWorkout;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSubmitButton;



    private Callbacks mCallbacks;

    public interface Callbacks {
        void onWorkoutUpdated(Workout workout);
    }


    public static WorkoutFragment newInstance(UUID workoutId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_WORKOUT_ID, workoutId);

        WorkoutFragment fragment = new WorkoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID workoutId = (UUID) getArguments().getSerializable(ARG_WORKOUT_ID);
        mWorkout = WorkoutLab.get(getActivity()).getWorkout(workoutId);
    }

    @Override
    public void onPause() {
        super.onPause();
        WorkoutLab.get(getActivity()).updateWorkout(mWorkout);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workout, container, false);

        setHasOptionsMenu(true);
        mTitleField = (EditText) v.findViewById(R.id.workout_title);
        mTitleField.setText(mWorkout.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mWorkout.setTitle(charSequence.toString());
                updateWorkout();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Also intentionally left blank
            }
        });
        mSubmitButton = (Button) v.findViewById(R.id.workout_report);

        mSubmitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Workout has been submitted", Toast.LENGTH_SHORT)
                        .show();
            }
        });



        mDateButton = (Button) v.findViewById(R.id.workout_date);
        final Date currentDate = mWorkout.getDate();

        String formattedDate = DateFormatter.formatDateAsString(DATE_FORMAT, currentDate);
        mDateButton.setText(formattedDate);

        mTimeButton = (Button) v.findViewById(R.id.workout_time_button);
        String formattedTime = DateFormatter.formatDateAsTimeString(TIME_FORMAT, currentDate);
        mTimeButton.setText(formattedTime);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getContext(), DatePickerActivity.class);
                    intent.putExtra(EXTRA_DATE, mWorkout.getDate());
                    startActivityForResult(intent, ACTIVITY_REQUEST_DATE);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TimePickerActivity.class);
                    intent.putExtra(EXTRA_TIME, mWorkout.getDate());
                    startActivityForResult(intent, ACTIVITY_REQUEST_TIME);
            }
        });


        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE || requestCode == REQUEST_TIME ||
                requestCode == ACTIVITY_REQUEST_DATE || requestCode == ACTIVITY_REQUEST_TIME) {
            final Date date;
            if (requestCode == REQUEST_DATE || requestCode == ACTIVITY_REQUEST_DATE) {
                date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            } else {
                date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            }

            mWorkout.setDate(date);
            mDateButton.setText(DateFormatter.formatDateAsString(DATE_FORMAT, date));
            mTimeButton.setText(DateFormatter.formatDateAsTimeString(TIME_FORMAT, date));
            updateWorkout();
        }
    }
    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable(ARG_WORKOUT_ID, mWorkout.getId());

        super.onSaveInstanceState(outState);
    }

    private void deleteWorkout() {
        WorkoutLab workoutLab = WorkoutLab.get(getActivity());
        workoutLab.deleteWorkout(mWorkout);
        mCallbacks.onWorkoutUpdated(mWorkout);

        Toast.makeText(getActivity(), R.string.toast_delete_workout, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_workout, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_workout:
                if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
                    deleteWorkout();
                    getActivity().finish();
                } else {
                    deleteWorkout();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .remove(this)
                            .commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateWorkout() {
        WorkoutLab.get(getActivity()).updateWorkout(mWorkout);
        mCallbacks.onWorkoutUpdated(mWorkout);
    }


}
