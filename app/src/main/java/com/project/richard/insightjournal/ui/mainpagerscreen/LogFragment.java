package com.project.richard.insightjournal.ui.mainpagerscreen;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.richard.insightjournal.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment {

    public static LogFragment newInstance() {
        Bundle args = new Bundle();
        LogFragment fragment = new LogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

}
