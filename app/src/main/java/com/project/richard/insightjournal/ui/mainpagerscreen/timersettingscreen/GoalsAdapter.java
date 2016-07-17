package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.GoalsColumns;
import com.project.richard.insightjournal.database.PresetsColumns;
import com.project.richard.insightjournal.events.OnGoalDeleteEvent;
import com.project.richard.insightjournal.utils.TimerUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by richard on 7/14/16.
 */
public class GoalsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GoalsAdapter.class.getSimpleName();

    private final int PRESET = 0, SESSION_TITLE = 1, GOALS = 2;

    private final Context mContext;
    private Cursor mGoalCursor;
    private Cursor mPresetCursor;

    public static class GoalsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goal_rv_textview) TextView goalTextView;
        @BindView(R.id.goals_rv_delete_button) ImageButton goalDeleteButton;

        public GoalsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class PresetViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_preset_rv_button) Button titleButton;
        @BindView(R.id.preparation_preset_rv_button) Button prepButton;
        @BindView(R.id.duration_preset_rv_button) Button durationButton;


        public PresetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class SessionTitleViewHolder extends RecyclerView.ViewHolder {

        public SessionTitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public GoalsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder vh = null;
        switch (viewType) {
            case PRESET:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.goal_presets_recyclerview, parent, false);
                vh = new PresetViewHolder(view);
                break;
            case SESSION_TITLE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.goal_session_title_recyclerview, parent, false);
                vh = new SessionTitleViewHolder(view);
                break;
            case GOALS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.goal_main_recyclerview, parent, false);
                vh = new GoalsViewHolder(view);
                break;
        }
        return vh;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case PRESET:
                PresetViewHolder presetHolder = (PresetViewHolder) holder;
                presetHolder.durationButton.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
                        dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(),
                                TimePickerDialogFragment.DURATION_FRAGMENT_TAG);
                    }
                });
                presetHolder.prepButton.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        TimePickerDialogFragment dialogFragment = new TimePickerDialogFragment();
                        dialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(),
                                TimePickerDialogFragment.PREP_FRAGMENT_TAG);
                    }
                });
                presetHolder.titleButton.setText(mPresetCursor.getString(mPresetCursor.getColumnIndex(PresetsColumns.TITLE)));
                presetHolder.prepButton.setText("Preparation Timer: " + TimerUtils.millisToDigital(
                        mPresetCursor.getInt(mPresetCursor.getColumnIndex(PresetsColumns.PREPARATION_TIME)))
                );
                presetHolder.durationButton.setText("Duration: " + TimerUtils.millisToDigital(
                        mPresetCursor.getLong(mPresetCursor.getColumnIndex(PresetsColumns.DURATION)))
                );
                break;
            case SESSION_TITLE:
                break;
            case GOALS:
                mGoalCursor.moveToPosition(position - 2);
                GoalsViewHolder goalHolder = (GoalsViewHolder) holder;
                Log.e(TAG, mGoalCursor.getString(mGoalCursor.getColumnIndex(GoalsColumns.GOALS)));
                goalHolder.goalTextView.setText(mGoalCursor.getString(mGoalCursor.getColumnIndex(GoalsColumns.GOALS)));
                goalHolder.goalDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        EventBus.getDefault().post(new OnGoalDeleteEvent());
                    }
                });
        }

    }

    @Override
    public int getItemCount() {
        if (null == mPresetCursor) return 0;
        if (null == mGoalCursor) return 2;
        Log.e(TAG, "count: " + mGoalCursor.getCount());
        return mGoalCursor.getCount() + 2;
    }

    @Override public int getItemViewType(int position) {
        if (position == 0) {
            return PRESET;
        } else if (position == 1) {
            return SESSION_TITLE;
        } else {
            return GOALS;
        }
    }

    public Cursor getCursor() {
        return mGoalCursor;
    }

    public void swapGoalCursor(Cursor newCursor) {
        mGoalCursor = newCursor;
        notifyDataSetChanged();
    }

    public void swapPresetCursor(Cursor newCursor) {
        mPresetCursor = newCursor;
        notifyItemChanged(0);
    }
}
