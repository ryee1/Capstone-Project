package com.project.richard.insightjournal.ui.timerscreen;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.project.richard.insightjournal.events.OnTickEvent;
import com.project.richard.insightjournal.events.OnTickFinishedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by a11 on 6/16/16.
 */
public class TimerService extends Service {

    public static final long timerOffset = 200;

    private final IBinder mBinder = new TimerBinder();
    private final OnTickEvent onTickEvent = new OnTickEvent();
    private final OnTickFinishedEvent onTickFinishedEvent = new OnTickFinishedEvent();
    private CountDownTimer mCountDownTimer;
    private long mDuration;

    public class TimerBinder extends Binder {
        TimerService getTimerService() {
            return TimerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startTimer(long duration) {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        mCountDownTimer = new CountDownTimer(duration, 100) {
            @Override public void onTick(long millisUntilFinished) {
                mDuration = millisUntilFinished;
                onTickEvent.currentTick = mDuration;
                EventBus.getDefault().post(onTickEvent);
            }

            @Override public void onFinish() {
                mDuration = 0;
                onTickFinishedEvent.finishedTick = mDuration;
                EventBus.getDefault().post(onTickFinishedEvent);
            }
        }.start();
    }

    public void pauseTimer() {
        mCountDownTimer.cancel();
    }

    public void resetDuration() {
        mDuration = 0;
        onTickFinishedEvent.finishedTick = mDuration;
        EventBus.getDefault().post(onTickFinishedEvent);
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

    public void setDuration(long newDuration) {
        mDuration = newDuration;
    }

    public long getDuration() {
        return mDuration;
    }
}