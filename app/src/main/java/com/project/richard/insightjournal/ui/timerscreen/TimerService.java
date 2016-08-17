package com.project.richard.insightjournal.ui.timerscreen;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.fitness.request.OnDataPointListener;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.events.OnPrepTickEvent;
import com.project.richard.insightjournal.events.OnTickEvent;
import com.project.richard.insightjournal.events.OnTickFinishedEvent;

import org.greenrobot.eventbus.EventBus;

/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TimerService extends Service {

    private static final String TAG = TimerService.class.getSimpleName();

    private final IBinder mBinder = new TimerBinder();
    private final OnTickEvent onTickEvent = new OnTickEvent();
    private final OnPrepTickEvent onPrepTickEvent = new OnPrepTickEvent();
    private final OnTickFinishedEvent onTickFinishedEvent = new OnTickFinishedEvent();
    private CountDownTimer mCountDownTimer;
    private long mDuration;
    private long mPrep;
    private OnDataPointListener mListener;
    private Address mAddress;

    private Location mLastLocation;


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

    public void startTimer(long duration, long prep) {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        if (prep != 0) {
            mCountDownTimer = new CountDownTimer(prep, 100) {
                @Override public void onTick(long millisUntilFinished) {
                    mPrep = millisUntilFinished;
                    onPrepTickEvent.currentTick = mPrep;
                    EventBus.getDefault().post(onPrepTickEvent);

                }

                @Override public void onFinish() {
                    mPrep = 0;
                    startTimer(mDuration, mPrep);
                }
            }.start();
        } else {
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
                    stopSelf();
                }
            }.start();
        }

    }
    public void pauseTimer() {
        mCountDownTimer.cancel();
    }

    public void stopTimer() {
        onTickFinishedEvent.finishedTick = mDuration;
        EventBus.getDefault().post(onTickFinishedEvent);
        stopSelf();
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

    @Override public void onDestroy() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        Log.e(TAG, "service destroyed");
        super.onDestroy();
    }

    public void setDuration(long newDuration) {
        mDuration = newDuration;
    }

    public long getDuration() {
        return mDuration;
    }

    public long getPrep() {
        return mPrep;
    }

    public void setPrep(long mPrep) {
        this.mPrep = mPrep;
    }

    public Address getAddress() {
        return mAddress;
    }

    public void setAddress(Address mAddress) {
        this.mAddress = mAddress;
    }

    public void setmLastLocation(Location mLastLocation) {
        this.mLastLocation = mLastLocation;
        Log.e(TAG, "location lat: " + mLastLocation.getLatitude());
    }

    public void foreground(){
        startForeground(1, createNotification());
        Log.e(TAG, "foreground");
    }

    public void background(){
        stopForeground(true);
    }
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Insight Timer")
                .setContentText("??")
                .setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(this, TimerActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        return builder.build();
    }
}