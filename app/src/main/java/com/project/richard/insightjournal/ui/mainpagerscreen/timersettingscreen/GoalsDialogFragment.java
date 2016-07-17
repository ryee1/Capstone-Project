package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.events.OnGoalsDialogConfirm;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GoalsDialogFragment extends DialogFragment {

    public static final String SHORT_TERM_FRAGMENT_TAG = "short_term_fragment_tag";
    public static final String LONG_TERM_FRAGMENT_TAG = "long_term_fragment_tag";

    private Unbinder unbinder;

    @BindView(R.id.edittext_goals_dialog) EditText editGoals;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goals_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.confirm_goals_dialog)
    public void confirm() {
        OnGoalsDialogConfirm event = new OnGoalsDialogConfirm(editGoals.getText().toString());
        EventBus.getDefault().post(event);
        getDialog().dismiss();
    }

    @OnClick(R.id.cancel_goals_dialog)
    public void cancel() {
        getDialog().cancel();
    }

}
