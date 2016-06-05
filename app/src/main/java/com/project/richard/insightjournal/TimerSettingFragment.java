package com.project.richard.insightjournal;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerSettingFragment extends Fragment {

    private static final String LOG_TAG = TimerSettingFragment.class.getName();

    @OnClick(R.id.timer_setting_start_button)
    public void start(){
        startActivity(new Intent(getActivity(), TimerActivity.class));
    }

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static TimerSettingFragment newInstance(int page) {
        Bundle args = new Bundle();
        TimerSettingFragment fragment = new TimerSettingFragment();
        args.putInt(ARG_PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_settings, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

}
