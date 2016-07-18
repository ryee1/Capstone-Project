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
import com.project.richard.insightjournal.utils.TimerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    private static final String TAG = TimerFragment.class.getSimpleName();
    private static final String BOOLEAN_TIMER_RUNNING = "boolean_timer_running";
    private static final String LONG_TIMER_DURATION = "long_timer_duration";

    public static final String PRESET_TITLE = "preset_title";
    public static final String TIMER_STARTED_KEY = "timer_started_key";
    public static final String DURATION_KEY = "duration_key";

    private String mTitle;
    private long mMaxDuration;
    private boolean mTimerRunning;
    private boolean mBound;
    private TimerService mTimerService;
    private Unbinder unbinder;

    @BindView(R.id.circle_timer_view) CircularProgressBar mCircleTimerView;
    @BindView(R.id.digital_timer_view) TextView mDigitalTimerView;
    @BindView(R.id.btn_timer_start) Button mStartButton;
    @BindView(R.id.btn_timer_stop) Button mStopButton;

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
        unbinder = ButterKnife.bind(this, view);

        mTitle = getArguments().getString(PRESET_TITLE);
        Cursor c = getActivity().getContentResolver().query(LogsProvider.Presets.PRESETS, null,
                PresetsColumns.TITLE + " = ?", new String[]{mTitle}, null);
        if (c != null && c.moveToFirst()) {
            mMaxDuration = c.getLong(c.getColumnIndex(PresetsColumns.DURATION));
            c.close();
        } else {
            Log.e(TAG, "Cannot find Preset");
        }

        if (savedInstanceState != null) {
            mTimerRunning = savedInstanceState.getBoolean(BOOLEAN_TIMER_RUNNING);
            mDigitalTimerView.setText(TimerUtils.millisToDigital(savedInstanceState.getLong(LONG_TIMER_DURATION)));
            mCircleTimerView.setProgress((float) savedInstanceState.getLong(LONG_TIMER_DURATION) / mMaxDuration * 100);
            Log.e(TAG, (savedInstanceState.getLong(LONG_TIMER_DURATION) / mMaxDuration * 100) + "");
        }

        return view;
    }

    @Override public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        Intent intent = new Intent(getActivity(), TimerService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);
    }

    @Override public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
            Log.e(TAG, "unbind service");
        }

        // Only stop the service if the timer hasn't been started yet.
        if (mTimerService.getDuration() == mMaxDuration) {
            getActivity().stopService(new Intent(getActivity(), TimerService.class));
        } else {
            mTimerService.foreground();
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BOOLEAN_TIMER_RUNNING, mTimerRunning);
        outState.putLong(LONG_TIMER_DURATION, mTimerService.getDuration());
        super.onSaveInstanceState(outState);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            mTimerService = ((TimerService.TimerBinder) service).getTimerService();
            mBound = true;
            mTimerService.background();

            //If service first run, duration == 0. Set duration to mMaxDuration
            if (mTimerService.getDuration() == 0) {
                mTimerService.setDuration(mMaxDuration);
            }

            //If service's duration doesn't match the maxduration, that means timer is already running
            if (timerRan()) {
                mTimerRunning = true;
            }
        }

        @Override public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public boolean timerRan(){
        return mTimerService.getDuration() != mMaxDuration;
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    public void stopTimerOnBackpress(){
        mTimerService.stopTimer();
        mTimerRunning = false;
    }
    @Subscribe
    public void onTickEvent(OnTickEvent event) {
        mDigitalTimerView.setText(TimerUtils.millisToDigital(event.currentTick));
        mCircleTimerView.setProgress((float) event.currentTick / mMaxDuration * 100);
    }

    @Subscribe
    public void onTickFinishedEvent(OnTickFinishedEvent event) {
        mTimerRunning = false;
        mDigitalTimerView.setText(TimerUtils.millisToDigital(event.finishedTick));
        mCircleTimerView.setProgress((float)event.finishedTick / mMaxDuration * 100);
        StopTimerDialogFragment dialog = StopTimerDialogFragment.newInstance(
                TimerUtils.millisToMillisRemaining(mMaxDuration, event.finishedTick),
                System.currentTimeMillis() / 1000L, mTitle);
        Log.e(TAG, mMaxDuration + " " + event.finishedTick);
        dialog.show(getActivity().getSupportFragmentManager(), StopTimerDialogFragment.class.getSimpleName());
    }

    //TODO implement proper back navigation when timer is running
//    @Subscribe
//    public void onTimerBackPressed(OnTimerBackPressed event) {
//        if (mTimerRunning) {
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }
//        else{
//
//        }
//    }

    @OnClick(R.id.btn_timer_start)
    public void startTimer() {
        if (!mTimerRunning) {
            mStartButton.setText(R.string.timer_start_button_onpause);
            mTimerRunning = true;
            mTimerService.startTimer(mTimerService.getDuration());
        } else {
            mStartButton.setText(R.string.timer_start_button_onstart);
            mTimerRunning = false;
            mTimerService.pauseTimer();
        }
    }

    @OnClick(R.id.btn_timer_stop)
    public void stopTimer() {
        mTimerService.stopTimer();
        mTimerRunning = false;
    }
}
