package com.project.richard.insightjournal.ui.mainpagerscreen;


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
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.database.PresetsColumns;
import com.project.richard.insightjournal.ui.timerscreen.TimerActivity;
import com.project.richard.insightjournal.utils.ContentValuesUtil;
import com.project.richard.insightjournal.utils.SharedPrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerSettingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = TimerSettingFragment.class.getName();

    private String mCurrentPresetTitle;

    @BindView(R.id.expandableLayout) ExpandableLayout expandableLayout;
    @BindView(R.id.meditation_title_textview) TextView titleTextView;
    @BindView(R.id.meditation_duration_textview) TextView durationTextView;
    @BindView(R.id.preparation_duration_textview) TextView prepTextView;

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
        ButterKnife.bind(this, view);

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

    @OnClick(R.id.timer_setting_start_button)
    public void start() {
        startActivity(new Intent(getActivity(), TimerActivity.class));
    }

    @OnClick(R.id.cardview_title_and_settings)
    public void toggleSettings() {
        expandableLayout.toggle();
    }

    @OnClick(R.id.duration_button)
    public void showTimePickerDialog(View v) {
        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
        dialogFragment.show(getActivity().getSupportFragmentManager(), "timepicker");
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
        durationTextView.setText("Duration: " + data.getInt(data.getColumnIndex(PresetsColumns.DURATION)));
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        
    }
}
