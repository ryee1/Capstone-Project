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


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.database.PresetsColumns;
import com.project.richard.insightjournal.events.OnTickEvent;
import com.project.richard.insightjournal.events.OnTickFinishedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    private static final String TAG = TimerFragment.class.getSimpleName();
    public static final String PRESET_TITLE = "preset_title";
    public static final String TIMER_STARTED_KEY = "timer_started_key";
    public static final String DURATION_KEY = "duration_key";

    @BindView(R.id.circle_timer_view) CircularProgressBar mCircleTimerView;
    @BindView(R.id.digital_timer_view) TextView mDigitalTimerView;
    @BindView(R.id.btn_timer_start) Button mStartButton;
    @BindView(R.id.btn_timer_stop) Button mStopButton;

    private long mMaxDuration;
    private boolean mTimerRunning;
    private boolean mBound;
    private TimerService mTimerService;

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
        Cursor c = getActivity().getContentResolver().query(LogsProvider.Presets.PRESETS, null,
                PresetsColumns.TITLE + " = ?", new String[]{getArguments().getString(PRESET_TITLE)}, null);
        if (c != null && c.moveToFirst()) {
            mMaxDuration = c.getLong(c.getColumnIndex(PresetsColumns.DURATION)) * 1000;
            c.close();
        } else {
            Log.e(TAG, "Cannot find Preset");
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(TIMER_STARTED_KEY, mTimerRunning);
        if (mTimerRunning) {
//            outState.putInt(DURATION_KEY, mCircleTimerView.start(););
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        mTimerRunning = savedInstanceState.getBoolean(TIMER_STARTED_KEY);
        if (mTimerRunning && mCircleTimerView.getProgress() != 0) {
            Log.e("df", mCircleTimerView.getProgress() + "");
            mCircleTimerView.setProgress(savedInstanceState.getInt(DURATION_KEY));
            mCircleTimerView.setProgressWithAnimation(60, 60);
        }
    }

    @Override public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        Intent intent = new Intent(getActivity(), TimerService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override public void onStart() {
        super.onStart();
    }

    @Override public void onStop() {
        super.onStop();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            mTimerService = ((TimerService.TimerBinder) service).getTimerService();
            mBound = true;
        }

        @Override public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Subscribe
    public void onTickEvent(OnTickEvent event) {
        mDigitalTimerView.setText(event.currentTick + "");
        mCircleTimerView.setProgress((float) event.currentTick / mMaxDuration * 100);
        Log.e("?", event.currentTick + " " + mMaxDuration + " " + (float) event.currentTick / mMaxDuration);
    }

    @Subscribe
    public void onTickFinishedEvent(OnTickFinishedEvent event) {
        mDigitalTimerView.setText(event.finishedTick + "");
        mCircleTimerView.setProgress(event.finishedTick);
    }

    @OnClick(R.id.btn_timer_start)
    public void startTimer() {
        if (!mTimerRunning) {
            mStartButton.setText(R.string.timer_start_button_onstart);
            mTimerRunning = true;
//            getActivity().startService(new Intent(getActivity(), TimerService.class));
            mTimerService.startTimer(mMaxDuration);
        } else {
            mStartButton.setText(R.string.timer_start_button_onpause);
            mTimerRunning = false;
            mTimerService.startTimer(mMaxDuration);
        }
    }

    @OnClick(R.id.btn_timer_stop)
    public void stopTimer() {
        mTimerService.resetDuration();
    }
}
