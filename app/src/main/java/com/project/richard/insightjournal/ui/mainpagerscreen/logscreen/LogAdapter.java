package com.project.richard.insightjournal.ui.mainpagerscreen.logscreen;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsColumns;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.utils.AnimUtils;
import com.project.richard.insightjournal.utils.TimerUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by richard on 6/29/16.
 */
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private static final String TAG = LogAdapter.class.getSimpleName();
    private static final int TIMESTAMP_TAG = 0;
    private Cursor mCursor;
    final private Context mContext;
    private final Gson gson = new Gson();
    private final HashMap<Long, Boolean> mStatesMap;
    private boolean showAllViews = false;
    public static class LogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.duration_log_recyclerview) TextView duration;
        @BindView(R.id.date_log_recyclerview) TextView date;
        @BindView(R.id.time_log_recyclerview) TextView time;
        @BindView(R.id.title_log_recyclerview) TextView title;
        @BindView(R.id.journal_log_recyclerview) TextView journal;
        @BindView(R.id.goals_container_log_recyclerview) LinearLayout goalsContainer;
        @BindView(R.id.toggle_layout_log_recyclerview) LinearLayout expandableView;
        @BindView(R.id.log_linearlayout_rv) LinearLayout logLinearLayout;
        @BindView(R.id.log_rv_edit_button) ImageButton editButton;
        @BindView(R.id.log_rv_delete_button) ImageButton deleteButton;

        public LogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public LogAdapter(Context mContext) {
        this.mContext = mContext;
        mStatesMap = new HashMap<>();
    }


    @Override public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_recyclerview, parent, false);
        return new LogViewHolder(view);
    }


    @Override public void onBindViewHolder(final LogViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.duration.setText(TimerUtils.millisToDigital(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DURATION))));
        holder.date.setText(TimerUtils.unixTimeToDate(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME))));
        holder.time.setText(TimerUtils.unixTimeToTime(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME))));
        holder.title.setText(String.format("%s", mCursor.getString(mCursor.getColumnIndex(LogsColumns.TITLE))));
        if(mCursor.getString(mCursor.getColumnIndex(LogsColumns.JOURNAL_ENTRY)).trim().length() > 0){
            holder.journal.setText(String.format("%s", mCursor.getString(mCursor.getColumnIndex(LogsColumns.JOURNAL_ENTRY))));
        }
        else{
            holder.journal.setText(R.string.no_journal_entry_log_rv);
        }
        holder.itemView.setTag(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME)));

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(mContext);
                confirmDialog
                        .setPositiveButton(mContext.getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        mCursor.moveToPosition(holder.getAdapterPosition());
                        new DeleteLogTask().execute(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME)));
                    }
                })
                        .setNegativeButton(mContext.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        holder.goalsContainer.removeAllViews();
        LinkedHashMap<String, Boolean> hashMap =
                gson.fromJson(mCursor.getString(mCursor.getColumnIndex(LogsColumns.GOALS)), LinkedHashMap.class);
        for (String goal : hashMap.keySet()) {
            holder.goalsContainer.addView(generateGoalView(goal, hashMap.get(goal)));
        }

        holder.logLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (holder.expandableView.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideUp(v.getContext(), v);
                    holder.expandableView.setVisibility(View.GONE);
                    mCursor.moveToPosition(holder.getAdapterPosition());
                    mStatesMap.put(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME)), false);
                } else {
                    AnimUtils.slideDown(v.getContext(), v);
                    holder.expandableView.setVisibility(View.VISIBLE);
                    mCursor.moveToPosition(holder.getAdapterPosition());
                    mStatesMap.put(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME)), true);
                }
            }
        });
        if(mStatesMap.get(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME))) != null
                && mStatesMap.get(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME)))){
            holder.expandableView.setVisibility(View.VISIBLE);
        }
        else{
            holder.expandableView.setVisibility(View.GONE);
        }
    }

    private View generateGoalView(String goal, Boolean goalReached) {
        TextView tv = new TextView(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 1, 0, 1);
        tv.setText(goal);
        tv.setLayoutParams(params);
        int backgroundColor = goalReached ? ContextCompat.getColor(mContext, R.color.colorPositiveGoal) :
                ContextCompat.getColor(mContext, R.color.colorNegativeGoal);
        tv.setBackgroundColor(backgroundColor);
        return tv;
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public void toggleStatesMap(){
        mCursor.moveToFirst();
        if(showAllViews){
            showAllViews = false;
        }
        else{
            showAllViews = true;
        }
        if(mCursor.getCount() > 0 && mCursor.moveToFirst()){
            while(!mCursor.isAfterLast()){
                mStatesMap.put(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME)),
                        showAllViews);
                mCursor.moveToNext();
            }
            notifyDataSetChanged();
        }
    }

    public boolean isShowAllViews(){
        return showAllViews;
    }

    private class DeleteLogTask extends AsyncTask<Long, Void, Void>{

        @Override protected Void doInBackground(Long... params) {
            mContext.getContentResolver().delete(LogsProvider.Logs.LOGS,
                    LogsColumns.SESSION_DATETIME + " = " + params[0], null);
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }

}
