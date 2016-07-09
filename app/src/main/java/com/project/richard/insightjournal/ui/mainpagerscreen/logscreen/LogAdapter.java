package com.project.richard.insightjournal.ui.mainpagerscreen.logscreen;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsColumns;
import com.project.richard.insightjournal.events.OnLogRvClickEvent;
import com.project.richard.insightjournal.utils.TimerUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by richard on 6/29/16.
 */
public class  LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    private static final String TAG = LogAdapter.class.getSimpleName();
    private static final int TIMESTAMP_TAG = 0;
    private Cursor mCursor;
    final private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        final private int mMaxLines = 2;

        @BindView(R.id.duration_log_recyclerview) TextView duration;
        @BindView(R.id.datetime_log_recyclerview) TextView datetime;
        @BindView(R.id.title_log_recyclerview) TextView title;
        @BindView(R.id.journal_log_recyclerview) TextView journal;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnLongClick(R.id.log_linearlayout_rv)
        public boolean onLongClick(View view){
            EventBus.getDefault().post(new OnLogRvClickEvent((long)view.getTag()));
            return true;
        }

        @OnClick(R.id.log_linearlayout_rv)
        public void toggleJournalTextView(){
            if(journal.getMaxLines() == 2) {
                journal.setMaxLines(99999);
            }
            else{
                journal.setMaxLines(mMaxLines);
            }
        }
    }

    public LogAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.duration.setText(TimerUtils.millisToDigital(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DURATION))));
        holder.datetime.setText(TimerUtils.unixTimeToDate( mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME))));
        holder.title.setText(String.format("%s", mCursor.getString(mCursor.getColumnIndex(LogsColumns.TITLE))));
        holder.journal.setText(String.format("%s", mCursor.getString(mCursor.getColumnIndex(LogsColumns.JOURNAL_ENTRY))));
        holder.itemView.setTag(mCursor.getLong(mCursor.getColumnIndex(LogsColumns.SESSION_DATETIME)));
    }


    @Override
    public int getItemCount() {
        if(null == mCursor) return 0;
        return mCursor.getCount();
    }

    public Cursor getCursor(){
        return mCursor;
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

}
