package com.example.android.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class WorkoutListFragment extends Fragment {

    private RecyclerView mWorkoutRecyclerView;
    private WorkoutAdapter mAdapter;
    private RelativeLayout mEmptyView;
    private Button mEmptyViewAddButton;

    // Subtitle on toolbar
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private boolean mSubtitleVisible;

    private Callbacks mCallbacks;

    public interface Callbacks {
        void onWorkoutSelected(Workout workout);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_list, container, false);

        mEmptyView = (RelativeLayout) view.findViewById(R.id.empty_view);
        mEmptyViewAddButton = (Button) mEmptyView.findViewById(R.id.empty_view_add_button);

        mEmptyViewAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAWorkout();
            }
        });

        mWorkoutRecyclerView = (RecyclerView) view.findViewById(R.id.workout_recycler_view);
        mWorkoutRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    mAdapter.swipeToDelete(viewHolder.getAdapterPosition());
                    updateUI();
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(mWorkoutRecyclerView);

        return view;
    }

    public void updateUI() {
        WorkoutLab workoutLab = WorkoutLab.get(getActivity());
        List<Workout> workouts = workoutLab.getWorkouts();

        if (mAdapter == null) {
            mAdapter = new WorkoutAdapter(workouts);
            mWorkoutRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setWorkouts(workouts);
            mAdapter.notifyDataSetChanged();
        }
        if (mAdapter.getItemCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        updateSubtitle();
    }

    // Private class for viewholder
    private class WorkoutHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private Workout mWorkout;

        private TextView mTitleTextView;
        private TextView mDateTextView;

        public WorkoutHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_workout_title_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_workout_date_text_view);
        }

        public void bindWorkout(Workout workout) {
            mWorkout = workout;
            mTitleTextView.setText(mWorkout.getTitle());
            Date date = mWorkout.getDate();
            String formattedDate = DateFormatter.formatDateAsString(DateFormat.LONG, date);
            String formattedTime = DateFormatter.formatDateAsTimeString(DateFormat.SHORT, date);
            mDateTextView.setText(formattedDate + " @ " + formattedTime);
        }

        @Override
        public void onClick(View view) {
            mCallbacks.onWorkoutSelected(mWorkout);
        }
    }

    // Private class for adapter to handle all data handling and viewholder creation
    private class WorkoutAdapter extends RecyclerView.Adapter<WorkoutHolder> {

        private List<Workout> mWorkouts;

        public WorkoutAdapter(List<Workout> workouts) {
            mWorkouts = workouts;
        }

        @Override
        public WorkoutHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            View view = layoutInflater.inflate(R.layout.list_item_workout, parent, false);
            return new WorkoutHolder(view);
        }

        @Override
        public void onBindViewHolder(WorkoutHolder holder, int position) {
            Workout workout = mWorkouts.get(position);
            holder.bindWorkout(workout);
        }

        @Override
        public int getItemCount() {
            return mWorkouts.size();
        }

        public void setWorkouts(List<Workout> workouts) {
            mWorkouts = workouts;
        }

        public void swipeToDelete(int position) {
            WorkoutLab workoutLab = WorkoutLab.get(getActivity());
            Workout workout = mWorkouts.get(position);
            workoutLab.deleteWorkout(workout);
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(position, workoutLab.getWorkouts().size());
            Toast.makeText(getContext(), R.string.toast_delete_workout, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_workout_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_workout:
                addAWorkout();
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        WorkoutLab workoutLab = WorkoutLab.get(getActivity());
        int workoutCount = workoutLab.getWorkouts().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, workoutCount, workoutCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private void addAWorkout() {
        Workout workout = new Workout();
        WorkoutLab.get(getActivity()).addWorkout(workout);
        updateUI();
        mCallbacks.onWorkoutSelected(workout);
    }
}
