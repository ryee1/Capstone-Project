package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.GoalsColumns;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.database.PresetsColumns;
import com.project.richard.insightjournal.events.OnGoalsDialogConfirm;
import com.project.richard.insightjournal.utils.SharedPrefUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerSettingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = TimerSettingFragment.class.getSimpleName();

    private String mCurrentPresetTitle;
    private Unbinder unbinder;
    private GoalsAdapter mGoalsAdapter;

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int LOADER_PRESET_ID = 0;
    public static final int LOADER_GOAL_ID = 1;

    private int mPage;

    @BindView(R.id.goals_recyclerview) RecyclerView recyclerView;
//    @BindView(R.id.meditation_title_textview) TextView titleTextView;
//    @BindView(R.id.meditation_duration_textview) TextView durationTextView;
//    @BindView(R.id.preparation_duration_textview) TextView prepTextView;
//    @BindView(R.id.duration_button) Button durationButton;
//    @BindView(R.id.prep_button) Button prepButton;
//    @BindView(R.id.title_button) Button titleButton;

    public static TimerSettingFragment newInstance(int page) {
        Bundle args = new Bundle();
        TimerSettingFragment fragment = new TimerSettingFragment();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

        getLoaderManager().initLoader(LOADER_PRESET_ID, null, this);
        getLoaderManager().initLoader(LOADER_GOAL_ID, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_settings, container, false);
        unbinder = ButterKnife.bind(this, view);

        mGoalsAdapter = new GoalsAdapter(getContext());

        recyclerView.setAdapter(mGoalsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        mCurrentPresetTitle = SharedPrefUtils.getTitlePref(getContext());
    }

    @Override
    public void onPause() {
        //TODO
        //SharedPrefUtils.addTitlePref(getContext(), mCurrentPresetTitle);
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    @Subscribe
    public void onGoalsDialogConfirm(OnGoalsDialogConfirm event) {
        ContentValues cv = new ContentValues();
        cv.put(GoalsColumns.GOALS, event.goal);
        getContext().getContentResolver().insert(LogsProvider.Goals.GOALS, cv);

    }

//    @OnClick(R.id.timer_setting_start_button)
//    public void start() {
//        startActivity(new Intent(getActivity(), TimerActivity.class));
//    }
//
//    @OnClick(R.id.cardview_title_and_settings)
//    public void toggleSettings() {
//        expandableLayout.toggle();
//    }
//
//    @OnClick(R.id.duration_button)
//    public void onDurationButtonClick(View v) {
//        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
//        dialogFragment.show(getActivity().getSupportFragmentManager(), TimePickerDialogFragment.DURATION_FRAGMENT_TAG);
//    }
//
//    @OnClick(R.id.prep_button)
//    public void onPrepButtonClick(View v) {
//        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
//        dialogFragment.show(getActivity().getSupportFragmentManager(), TimePickerDialogFragment.PREP_FRAGMENT_TAG);
//    }
//
//    @OnClick(R.id.timer_settings_add_goal_button)
//    public void onAddGoalClick() {
//        GoalsDialogFragment dialogFragment = new GoalsDialogFragment();
//        dialogFragment.show(getActivity().getSupportFragmentManager(), GoalsDialogFragment.SHORT_TERM_FRAGMENT_TAG);
//    }


    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        Log.e(TAG, "pppp");
        if (id == LOADER_PRESET_ID) {

            if (SharedPrefUtils.getTitlePref(getContext()).equals(SharedPrefUtils.EMPTY_PRESET_PREF)) {
                cursorLoader = new CursorLoader(getContext(), LogsProvider.Presets.PRESETS, null, null, null, null);
            } else {
                cursorLoader = new CursorLoader(getContext(), LogsProvider.Presets.PRESETS, null,
                        PresetsColumns.TITLE + " = " + '"' + SharedPrefUtils.getTitlePref(getContext()) + '"', null, null);
            }
        }
        else if(id == LOADER_GOAL_ID){
            cursorLoader = new CursorLoader(getContext(), LogsProvider.Goals.GOALS, null, null, null, null);
        }
        return cursorLoader;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(TAG, "cc");
        if(loader.getId() == LOADER_PRESET_ID) {
            mGoalsAdapter.swapPresetCursor(data);
            Log.e(TAG, "pp");
//            if (!data.moveToFirst()) {
//                SharedPrefUtils.addTitlePref(getContext(), "Default Preset");
//                getContext().getContentResolver().insert(LogsProvider.Presets.PRESETS,
//                        ContentValuesUtil.presetContentValues("Default Preset", 5, 30));
//                titleTextView.setText("Default Preset");
//                return;
//            }
//            titleTextView.setText(data.getString(data.getColumnIndex(PresetsColumns.TITLE)));
//            prepButton.setText("Preparation Timer: " + TimerUtils.millisToDigital(
//                    data.getInt(data.getColumnIndex(PresetsColumns.PREPARATION_TIME)))
//            );
//            prepTextView.setText("Preparation Timer: " + TimerUtils.millisToDigital(
//                    data.getInt(data.getColumnIndex(PresetsColumns.PREPARATION_TIME)))
//            );
//            durationTextView.setText("Duration: " + TimerUtils.millisToDigital(
//                    data.getLong(data.getColumnIndex(PresetsColumns.DURATION)))
//            );
//            durationButton.setText("Duration: " + TimerUtils.millisToDigital(
//                    data.getLong(data.getColumnIndex(PresetsColumns.DURATION)))
//            );
        }
        else if(loader.getId() == LOADER_GOAL_ID){
            mGoalsAdapter.swapGoalCursor(data);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_PRESET_ID){
            mGoalsAdapter.swapPresetCursor(null);
        }
        else if(loader.getId() == LOADER_GOAL_ID){
            mGoalsAdapter.swapGoalCursor(null);
        }
    }

}
