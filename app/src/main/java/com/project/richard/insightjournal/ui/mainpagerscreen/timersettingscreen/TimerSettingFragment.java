package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.database.PresetsColumns;
import com.project.richard.insightjournal.ui.timerscreen.TimerActivity;
import com.project.richard.insightjournal.utils.ContentValuesUtil;
import com.project.richard.insightjournal.utils.SharedPrefUtils;
import com.project.richard.insightjournal.utils.TimerUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerSettingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = TimerSettingFragment.class.getName();

    private String mCurrentPresetTitle;
    private Unbinder unbinder;

    @BindView(R.id.expandableLayout) ExpandableLayout expandableLayout;
    @BindView(R.id.meditation_title_textview) TextView titleTextView;
    @BindView(R.id.meditation_duration_textview) TextView durationTextView;
    @BindView(R.id.preparation_duration_textview) TextView prepTextView;
    @BindView(R.id.duration_button) Button durationButton;
    @BindView(R.id.prep_button) Button prepButton;
    @BindView(R.id.title_button) Button titleButton;
    @BindView(R.id.long_term_goals_textview) TextView longTermGoals;
    @BindView(R.id.short_term_goals_textview) TextView shortTermGoals;

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int LOADER_PRESET_ID = 0;

    private int mPage;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_settings, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override public void onResume() {
        super.onResume();
        mCurrentPresetTitle = SharedPrefUtils.getTitlePref(getContext());
    }

    @Override public void onPause() {
        //TODO
        //SharedPrefUtils.addTitlePref(getContext(), mCurrentPresetTitle);
        super.onPause();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }
    @OnClick(R.id.timer_setting_start_button)
    public void start() {
        startActivity(new Intent(getActivity(), TimerActivity.class));
    }

    @OnClick(R.id.cardview_title_and_settings)
    public void toggleSettings() {
        expandableLayout.toggle();
    }

    @OnClick(R.id.duration_button)
    public void onDurationButtonClick(View v) {
        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), TimePickerDialogFragment.DURATION_FRAGMENT_TAG);
    }

    @OnClick(R.id.prep_button)
    public void onPrepButtonClick(View v){
        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), TimePickerDialogFragment.PREP_FRAGMENT_TAG);
    }

    @OnClick(R.id.cardview_short_term)
    public void onShortTermClick(){
        GoalsDialogFragment dialogFragment = new GoalsDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), GoalsDialogFragment.SHORT_TERM_FRAGMENT_TAG);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        if (id == LOADER_PRESET_ID) {

            if(SharedPrefUtils.getTitlePref(getContext()).equals(SharedPrefUtils.EMPTY_PRESET_PREF)){
                cursorLoader = new CursorLoader(getContext(), LogsProvider.Presets.PRESETS, null, null, null, null);
            }
            else{
                cursorLoader = new CursorLoader(getContext(), LogsProvider.Presets.PRESETS, null,
                        PresetsColumns.TITLE + " = " + '"' + SharedPrefUtils.getTitlePref(getContext()) + '"', null, null);
            }
        }
        return cursorLoader;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            SharedPrefUtils.addTitlePref(getContext(), "Default Preset");
            getContext().getContentResolver().insert(LogsProvider.Presets.PRESETS,
                    ContentValuesUtil.presetContentValues("Default Preset", 5, 30));
            titleTextView.setText("Default Preset");
            return;
        }
        titleTextView.setText(data.getString(data.getColumnIndex(PresetsColumns.TITLE)));
        prepTextView.setText("Preparation Timer: " + data.getInt(data.getColumnIndex(PresetsColumns.PREPARATION_TIME)));
        durationTextView.setText("Duration: " + TimerUtils.millisToDigital(data.getLong(data.getColumnIndex(PresetsColumns.DURATION))));
        durationButton.setText("Duration: " + TimerUtils.millisToDigital(data.getLong(data.getColumnIndex(PresetsColumns.DURATION))));
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        
    }
}
