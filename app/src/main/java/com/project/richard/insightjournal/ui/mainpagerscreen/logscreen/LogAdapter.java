package com.project.richard.insightjournal.ui.mainpagerscreen.logscreen;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.richard.insightjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by richard on 6/29/16.
 */
public class LogAdapter extends RecyclerView.Adapter {

    private static final String LOG_TAG = LogAdapter.class.getSimpleName();
    private Cursor mCursor;
    final private Context mContext;
    final private LogAdapterOnClickHandler mClickHandler;

    public interface LogAdapterOnClickHandler{
        void onClick();
    }

    public LogAdapter(Context mContext, LogAdapterOnClickHandler mClickHandler) {
        this.mContext = mContext;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.duration_log_recyclerview) TextView duration;
        @BindView(R.id.datetime_log_recyclerview) TextView datetime;
        @BindView(R.id.title_log_recyclerview) TextView title;
        @BindView(R.id.journal_log_recyclerview) TextView journal;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override public void onClick(View v) {

        }
    }
}
