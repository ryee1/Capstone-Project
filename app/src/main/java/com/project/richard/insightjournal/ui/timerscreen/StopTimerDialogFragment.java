package com.project.richard.insightjournal.ui.timerscreen;


import android.app.Activity;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.GoalsColumns;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.ui.mainpagerscreen.PagerActivity;
import com.project.richard.insightjournal.utils.ContentValuesUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class StopTimerDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = StopTimerDialogFragment.class.getSimpleName();
    public static final String DURATION_STOP_TIMER_DIALOG = "duration_stop_timer_dialog";
    public static final String DATE_STOP_TIMER_DIALOG = "date_stop_timer_dialog";
    public static final String TITLE_STOP_TIMER_DIALOG = "title_stop_timer_dialog";
    public static final String JOURNAL_STOP_TIMER_DIALOG = "journal_stop_timer_dialog";


    private static AsyncQueryHandler mAsyncQueryHandler;
    private LinkedHashMap<String, Boolean> mGoalsHashMap;
    private Unbinder unbinder;

    @BindView(R.id.edittext_stop_timer_dialog) EditText journalEditText;
    @BindView(R.id.goals_container_stop_timer_dialog) LinearLayout goalsContainer;
    @BindColor(R.color.colorNegativeGoal) int colorNegativeGoal;
    @BindColor(R.color.colorPositiveGoal) int colorPositiveeGoal;

    public static StopTimerDialogFragment newInstance(long duration, long date, String title) {
        StopTimerDialogFragment f = new StopTimerDialogFragment();
        Bundle args = new Bundle();
        args.putLong(DURATION_STOP_TIMER_DIALOG, duration);
        args.putLong(DATE_STOP_TIMER_DIALOG, date);
        args.putString(TITLE_STOP_TIMER_DIALOG, title);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_stop_timer_dialog,
                null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        unbinder = ButterKnife.bind(this, view);
        final long duration = getArguments().getLong(DURATION_STOP_TIMER_DIALOG);
        final long date = getArguments().getLong(DATE_STOP_TIMER_DIALOG);
        final String title = getArguments().getString(TITLE_STOP_TIMER_DIALOG);
        final String journal = getArguments().getString(JOURNAL_STOP_TIMER_DIALOG);

        mAsyncQueryHandler= new AsyncQueryHandler(getActivity().getContentResolver()){};
        builder.setView(view)
                .setMessage("????")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        mAsyncQueryHandler.startInsert(0, null, LogsProvider.Logs.LOGS,
                                ContentValuesUtils.stopTimerDialogContentValues(duration, date,
                                title, journalEditText.getText().toString(), hashMapToJson(mGoalsHashMap)));
                        Intent intent = new Intent(getActivity(), PagerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                });
        return builder.create();
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    private String hashMapToJson(HashMap<String, Boolean> hashMap){
        Gson gson = new Gson();
        return gson.toJson(hashMap);
    }

    private void printHash(HashMap<String, Boolean> hashMap){
        for(String s : hashMap.keySet()){
            Log.e(TAG, s + " " +hashMap.get(s).toString());
        }
    }
    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), LogsProvider.Goals.GOALS, null, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        Log.e(TAG, data.getCount() + " count");
        if(data.getCount() != 0){
            mGoalsHashMap = new LinkedHashMap<>();
            while(data.moveToNext()) {
                final String goal = data.getString(data.getColumnIndex(GoalsColumns.GOALS));
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.goal_layout_goals_dialog, null);
                TextView goalTextView = (TextView) view.findViewById(R.id.goal_textview_goals_dialog);
                ImageButton goalPositive = (ImageButton) view.findViewById(R.id.goal_positive_stop_timer_dialog);
                ImageButton goalNegative = (ImageButton) view.findViewById(R.id.goal_negative_stop_timer_dialog);
                goalTextView.setText(goal);
                view.setBackgroundColor(colorNegativeGoal);
                goalsContainer.addView(view);
                mGoalsHashMap.put(goal, false);
                goalPositive.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        mGoalsHashMap.put(goal, true);
                        view.setBackgroundColor(colorPositiveeGoal);
                    }
                });
                goalNegative.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        mGoalsHashMap.put(goal, false);
                        view.setBackgroundColor(colorNegativeGoal);
                    }
                });
            }
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {

    }
}
