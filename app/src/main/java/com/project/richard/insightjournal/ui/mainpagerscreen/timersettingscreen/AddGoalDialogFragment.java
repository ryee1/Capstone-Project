package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;


import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.GoalsColumns;
import com.project.richard.insightjournal.database.LogsProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddGoalDialogFragment extends DialogFragment {

    public static final String ADD_GOAL_FRAGMENT_TAG = "add_goal_fragment_tag";
    public static final String EDIT_GOAL_FRAGMENT_TAG = "edit_goal_fragment_tag";
    public static final String GOAL_BUNDLE_TAG = "goal_fragment_tag";
    public static final String ID_BUNDLE_TAG = "id_fragment_tag";

    private Unbinder unbinder;

    @BindView(R.id.edittext_goals_dialog) EditText editGoals;

    public static AddGoalDialogFragment newInstance(String goal, int id){
        Bundle args = new Bundle();
        AddGoalDialogFragment f = new AddGoalDialogFragment();
        args.putString(GOAL_BUNDLE_TAG, goal);
        args.putInt(ID_BUNDLE_TAG, id);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goals_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        if(getTag().equals(EDIT_GOAL_FRAGMENT_TAG)){
            editGoals.setText(getArguments().getString(GOAL_BUNDLE_TAG));
        }
        return view;
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.confirm_goals_dialog)
    public void confirm() {
        if(getTag().equals(ADD_GOAL_FRAGMENT_TAG)) {
            ContentValues cv = new ContentValues();
            cv.put(GoalsColumns.GOALS, editGoals.getText().toString());
            getContext().getContentResolver().insert(LogsProvider.Goals.GOALS, cv);
            getDialog().dismiss();
        }
        else if(getTag().equals(EDIT_GOAL_FRAGMENT_TAG)){
            ContentValues cv = new ContentValues();
            cv.put(GoalsColumns.GOALS, editGoals.getText().toString());
            getContext().getContentResolver().update(LogsProvider.Goals.GOALS, cv, GoalsColumns._ID +" = ?",
                    new String[]{"" + getArguments().getInt(ID_BUNDLE_TAG)});
            getDialog().dismiss();
        }
    }

    @OnClick(R.id.cancel_goals_dialog)
    public void cancel() {
        getDialog().cancel();
    }

}
