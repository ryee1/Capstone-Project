//The MIT License (MIT)
//
//        Copyright (c) 2015 Maxwell Forest
//
//        Permission is hereby granted, free of charge, to any person obtaining a copy
//        of this software and associated documentation files (the "Software"), to deal
//        in the Software without restriction, including without limitation the rights
//        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//        copies of the Software, and to permit persons to whom the Software is
//        furnished to do so, subject to the following conditions:
//
//        The above copyright notice and this permission notice shall be included in all
//        copies or substantial portions of the Software.
//
//        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//        SOFTWARE.

package com.project.richard.insightjournal.ui.timerscreen;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.events.OnTickEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    public static final String PRESET_TITLE = "preset_title";
    public static final String TIMER_STARTED_KEY = "timer_started_key";
    public static final String DURATION_KEY = "duration_key";

    @BindView(R.id.circle_timer_view) CircularProgressBar mCircleTimerView;
    @BindView(R.id.digital_timer_view) TextView mDigitalTimerView;

    private boolean mTimerStarted;

    public static TimerFragment newInstance(String title) {
        Bundle args = new Bundle();
        TimerFragment fragment = new TimerFragment();
        args.putString(PRESET_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(TIMER_STARTED_KEY, mTimerStarted);
        if(mTimerStarted){
//            outState.putInt(DURATION_KEY, mCircleTimerView.start(););
        }
    }

    @Override
    public void onViewStateRestored( Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState == null){
            return;
        }
        mTimerStarted = savedInstanceState.getBoolean(TIMER_STARTED_KEY);
        if(mTimerStarted && mCircleTimerView.getProgress() != 0){
            Log.e("df", mCircleTimerView.getProgress()+"");
            mCircleTimerView.setProgress(savedInstanceState.getInt(DURATION_KEY));
            mCircleTimerView.setProgressWithAnimation(60, 60);
        }
    }

    @Override public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe public void onTickEvent(OnTickEvent event){
        mDigitalTimerView.setText(event.currentTick + "");
    }
    @OnClick(R.id.btn_timer_start)
    public void startTimer(){
        if(mTimerStarted) {
            mCircleTimerView.setProgressWithAnimation(100, 7000);
            mTimerStarted = true;
            getActivity().startService(new Intent(getActivity(), TimerService.class));
        }
        else{

        }
    }
}
