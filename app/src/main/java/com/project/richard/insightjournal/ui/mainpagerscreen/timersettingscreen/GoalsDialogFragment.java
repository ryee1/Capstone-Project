package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.richard.insightjournal.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GoalsDialogFragment extends DialogFragment {

    public static final String SHORT_TERM_FRAGMENT_TAG = "short_term_fragment_tag";
    public static final String LONG_TERM_FRAGMENT_TAG = "long_term_fragment_tag";

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_goals_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

}
