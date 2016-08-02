package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.GoalsColumns;
import com.project.richard.insightjournal.database.LogsProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by richard on 7/14/16.
 */
public class GoalsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GoalsAdapter.class.getSimpleName();

    private final Context mContext;
    private Cursor mCursor;

    public static class GoalsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goal_rv_textview) TextView goalTextView;
        @BindView(R.id.goals_rv_delete_button) ImageButton goalDeleteButton;
        @BindView(R.id.goals_rv_edit_button) ImageButton goalEditButton;
        public GoalsViewHolder(View itemView) {
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
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_main_recyclerview, parent, false);
        vh = new GoalsViewHolder(view);
        return vh;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        mCursor.moveToPosition(position);
        final GoalsViewHolder goalHolder = (GoalsViewHolder) holder;
        Log.e(TAG, mCursor.getString(mCursor.getColumnIndex(GoalsColumns.GOALS)));
        goalHolder.goalTextView.setText(mCursor.getString(mCursor.getColumnIndex(GoalsColumns.GOALS)));
        goalHolder.goalDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mCursor.moveToPosition(goalHolder.getAdapterPosition());
                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(mContext);
                confirmDialog
                        .setPositiveButton(mContext.getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                mCursor.moveToPosition(goalHolder.getAdapterPosition());
                                new DeleteGoalTask().execute(mCursor.getInt(mCursor.getColumnIndex(GoalsColumns._ID)));
                            }
                        })
                        .setNegativeButton(mContext.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        goalHolder.goalEditButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mCursor.moveToPosition(goalHolder.getAdapterPosition());
                AddGoalDialogFragment dialogFragment = AddGoalDialogFragment.newInstance(
                        mCursor.getString(mCursor.getColumnIndex(GoalsColumns.GOALS)),
                        mCursor.getInt(mCursor.getColumnIndex(GoalsColumns._ID)),
                        mCursor.getString(mCursor.getColumnIndex(GoalsColumns.TYPE))
                );
                dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                        AddGoalDialogFragment.EDIT_GOAL_FRAGMENT_TAG);
            }
        });
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
    private class DeleteGoalTask extends AsyncTask<Integer, Void, Void> {

        @Override protected Void doInBackground(Integer... params) {
            mContext.getContentResolver().delete(LogsProvider.Goals.GOALS, GoalsColumns._ID + " = "
                    + params[0], null);
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }
}
