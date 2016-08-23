package com.project.richard.insightjournal.ui.timerscreen;


import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
import com.project.richard.insightjournal.ui.widget.ReminderWidget;
import com.project.richard.insightjournal.utils.ContentValuesUtils;
import com.project.richard.insightjournal.utils.SharedPrefUtils;

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
    public static final String LOCATION_STOP_TIMER_DIALOG = "location_stop_timer_dialog";


    private static AsyncQueryHandler mAsyncQueryHandler;
    private LinkedHashMap<String, Boolean> mGoalsHashMap;
    private Unbinder unbinder;

    @BindView(R.id.edittext_stop_timer_dialog) EditText journalEditText;
    @BindView(R.id.goals_container_stop_timer_dialog) LinearLayout goalsContainer;
    @BindColor(R.color.colorNegativeGoal) int colorNegativeGoal;
    @BindColor(R.color.colorPositiveGoal) int colorPositiveGoal;

    public static StopTimerDialogFragment newInstance(long duration, long date, String title, String location) {
        StopTimerDialogFragment f = new StopTimerDialogFragment();
        Bundle args = new Bundle();
        args.putLong(DURATION_STOP_TIMER_DIALOG, duration);
        args.putLong(DATE_STOP_TIMER_DIALOG, date);
        args.putString(TITLE_STOP_TIMER_DIALOG, title);
        args.putString(LOCATION_STOP_TIMER_DIALOG, location);
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
        final String location = getArguments().getString(LOCATION_STOP_TIMER_DIALOG);

        mAsyncQueryHandler= new AsyncQueryHandler(getActivity().getContentResolver()){};
        builder.setView(view)
                .setMessage(getContext().getString(R.string.stop_timer_dialog_title))
                .setPositiveButton(getContext().getString(R.string.stop_timer_dialog_confirm_text), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        mAsyncQueryHandler.startInsert(0, null, LogsProvider.Logs.LOGS,
                                ContentValuesUtils.stopTimerDialogContentValues(duration, date,
                                title, journalEditText.getText().toString(), hashMapToJson(mGoalsHashMap),
                                location));
                        Intent intent = new Intent(getActivity(), PagerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        SharedPrefUtils.setLastSession(getContext(), date);
                        updateReminderWidget();
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getContext().getString(R.string.stop_timer_dialog_cancel_text), new DialogInterface.OnClickListener() {
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

    private void updateReminderWidget(){
        Intent intent = new Intent(getContext(), ReminderWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(
                new ComponentName(getContext(), ReminderWidget.class)
        );
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        getContext().sendBroadcast(intent);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), LogsProvider.Goals.GOALS, null, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if(data.getCount() != 0){
            mGoalsHashMap = new LinkedHashMap<>();
            while(data.moveToNext()) {
                final String goal = data.getString(data.getColumnIndex(GoalsColumns.GOALS));
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.goal_layout_stop_timer_dialog,
                        goalsContainer, false);
                TextView goalTextView = (TextView) view.findViewById(R.id.goal_textview_goals_dialog);
                final ImageButton goalToggle = (ImageButton) view.findViewById(R.id.goal_toggle_stop_timer_dialog);
                goalTextView.setText(goal);
                view.setBackgroundColor(colorNegativeGoal);
                goalsContainer.addView(view);
                mGoalsHashMap.put(goal, false);
                goalToggle.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        if(mGoalsHashMap.get(goal) != null && mGoalsHashMap.get(goal)){
                            mGoalsHashMap.put(goal, false);
                            view.setBackgroundColor(colorNegativeGoal);
                            ((ImageButton) v).setImageResource(R.drawable.ic_thumb_down_black_18dp);
                            v.setContentDescription(getContext().getString(R.string.contentDescriptionNegativeToggle));
                        }else {
                            mGoalsHashMap.put(goal, true);
                            view.setBackgroundColor(colorPositiveGoal);
                            ((ImageButton) v).setImageResource(R.drawable.ic_thumb_up_black_18dp);
                            v.setContentDescription(getContext().getString(R.string.contentDescriptionPositiveToggle));
                        }
                    }
                });
            }
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {

    }
}
