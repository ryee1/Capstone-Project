package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.GoalsColumns;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.database.PresetsColumns;
import com.project.richard.insightjournal.events.OnGoalsDialogConfirm;
import com.project.richard.insightjournal.ui.DividerItemDecoration;
import com.project.richard.insightjournal.ui.timerscreen.TimerActivity;
import com.project.richard.insightjournal.utils.SharedPrefUtils;
import com.project.richard.insightjournal.utils.TimerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerSettingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = TimerSettingFragment.class.getSimpleName();

    private String mCurrentPresetType;
    private Unbinder unbinder;
    private GoalsAdapter mGoalsAdapter;

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int LOADER_PRESET_ID = 0;
    public static final int LOADER_GOAL_ID = 1;

    private int mPage;

    @BindView(R.id.goals_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.duration_preset_button) Button durationButton;
    @BindView(R.id.preparation_preset_button) Button prepButton;
    @BindView(R.id.title_preset_button) Button titleButton;
    @BindView(R.id.toggle_preset_type_switch) Switch togglePresetTypeSwitch;

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

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        mCurrentPresetType = SharedPrefUtils.getTypePref(getContext());
    }

    @Override
    public void onPause() {
        //TODO
        //SharedPrefUtils.addTitlePref(getContext(), mCurrentPresetType);
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

    @OnClick(R.id.fab_timer_setting)
    public void start() {
        startActivity(new Intent(getActivity(), TimerActivity.class));
    }


    @OnClick(R.id.duration_preset_button)
    public void onDurationButtonClick(View v) {
        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), TimePickerDialogFragment.DURATION_FRAGMENT_TAG);
    }

    @OnClick(R.id.preparation_preset_button)
    public void onPrepButtonClick(View v) {
        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), TimePickerDialogFragment.PREP_FRAGMENT_TAG);
    }

    @OnClick(R.id.goal_add_button)
    public void onAddGoalClick() {
        AddGoalDialogFragment dialogFragment = AddGoalDialogFragment.newInstance(null, -1, mCurrentPresetType);
        dialogFragment.show(getActivity().getSupportFragmentManager(), AddGoalDialogFragment.ADD_GOAL_FRAGMENT_TAG);
    }

    @OnCheckedChanged(R.id.toggle_preset_type_switch)
    public void onPresetTypeCheckChanged(CompoundButton compoundButton, boolean isChecked){
        ContentValues cv = new ContentValues();
        cv.put(PresetsColumns.RECORD_TOGGLE_ON, isChecked);
        getActivity().getContentResolver().update(LogsProvider.Presets.PRESETS, cv, PresetsColumns.TYPE
                + " = '" + mCurrentPresetType + "'", null);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        if (id == LOADER_PRESET_ID) {

            if (SharedPrefUtils.getTypePref(getContext()).equals(SharedPrefUtils.EMPTY_PRESET_PREF)) {
                cursorLoader = new CursorLoader(getContext(), LogsProvider.Presets.PRESETS, null, null, null, null);
            } else {
                cursorLoader = new CursorLoader(getContext(), LogsProvider.Presets.PRESETS, null,
                        PresetsColumns.TYPE + " = " + '"' + SharedPrefUtils.getTypePref(getContext()) + '"', null, null);
            }
        }
        else if(id == LOADER_GOAL_ID){
            cursorLoader = new CursorLoader(getContext(), LogsProvider.Goals.GOALS, null, null, null, null);
        }
        return cursorLoader;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_PRESET_ID && data.getCount() != 0) {
            data.moveToFirst();
            titleButton.setText(data.getString(data.getColumnIndex(PresetsColumns.TYPE)));
            if(data.getString(data.getColumnIndex(PresetsColumns.TYPE)).equals(PresetsColumns.SITTING_MEDITAION)){
                togglePresetTypeSwitch.setText("Track Breaths");
            }
            else{
                togglePresetTypeSwitch.setText("Track Steps");
            }
            prepButton.setText("Preparation Timer: " + TimerUtils.millisToDigital(
                    data.getInt(data.getColumnIndex(PresetsColumns.PREPARATION_TIME)))
            );
            durationButton.setText("Duration: " + TimerUtils.millisToDigital(
                    data.getLong(data.getColumnIndex(PresetsColumns.DURATION)))
            );
            if(data.getInt(data.getColumnIndex(PresetsColumns.RECORD_TOGGLE_ON)) == 1){
                togglePresetTypeSwitch.setChecked(true);
            }
            else{
                togglePresetTypeSwitch.setChecked(false);
            }
        }
        else if(loader.getId() == LOADER_GOAL_ID){
            mGoalsAdapter.swapCursor(data);
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_GOAL_ID){
            mGoalsAdapter.swapCursor(null);
        }
    }

}
