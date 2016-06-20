package com.project.richard.insightjournal.ui.timerscreen;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.project.richard.insightjournal.events.OnTickEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by a11 on 6/16/16.
 */
public class TimerService extends Service {
    private final OnTickEvent onTickEvent = new OnTickEvent();
    private CountDownTimer mCountDownTimer;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCountDownTimer = new CountDownTimer(70000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                onTickEvent.currentTick = millisUntilFinished;
                EventBus.getDefault().post(new OnTickEvent(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                stopSelf();
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }
}
