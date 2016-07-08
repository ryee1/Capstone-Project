package com.project.richard.insightjournal.ui.timerscreen;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.utils.ContentValuesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class StopTimerDialogFragment extends DialogFragment {

    private static final String TAG = StopTimerDialogFragment.class.getSimpleName();
    public static final String DURATION_STOP_TIMER_DIALOG = "duration_stop_timer_dialog";
    public static final String DATE_STOP_TIMER_DIALOG = "date_stop_timer_dialog";
    public static final String TITLE_STOP_TIMER_DIALOG = "title_stop_timer_dialog";
    public static final String JOURNAL_STOP_TIMER_DIALOG = "journal_stop_timer_dialog";
    private Unbinder unbinder;

    @BindView(R.id.edittext_stop_timer_dialog) EditText journalEditText;

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

        Log.e(TAG, "dur: " + duration);
        builder.setView(view)
                .setMessage("????")
                .setPositiveButton("Log Session", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        getActivity().getContentResolver().insert(LogsProvider.Logs.LOGS,
                                ContentValuesUtil.stopTimerDialogContentValues(duration, date,
                                        title, journalEditText.getText().toString()));
                    }
                })
                .setNegativeButton("Don't Log", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton("Reset Timer", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }
}
