package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.GoalsColumns;
import com.project.richard.insightjournal.events.OnGoalDeleteEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by richard on 7/14/16.
 */
public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder>{

    private static final String TAG = GoalsAdapter.class.getSimpleName();
    private Cursor mCursor;

    public static class GoalsViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.goal_rv_textview) TextView goalTextView;
        @BindView(R.id.goals_rv_delete_button) ImageButton goalDeleteButton;

        public GoalsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override public GoalsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_recyclerview, parent, false);
        return new GoalsViewHolder(view);
    }

    @Override public void onBindViewHolder(GoalsViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Log.e(TAG, mCursor.getString(mCursor.getColumnIndex(GoalsColumns.GOALS)));
        holder.goalTextView.setText(mCursor.getString(mCursor.getColumnIndex(GoalsColumns.GOALS)));
        holder.goalDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                EventBus.getDefault().post(new OnGoalDeleteEvent());
            }
        });
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
