package com.project.richard.insightjournal.ui.timerscreen;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.events.OnTickEvent;
import com.project.richard.insightjournal.events.OnTickFinishedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by a11 on 6/16/16.
 */
public class TimerService extends Service {

    private static final String TAG = TimerService.class.getSimpleName();

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
                stopSelf();
            }
        }.start();
    }

    public void pauseTimer() {
        mCountDownTimer.cancel();
    }

    @Override public void onDestroy() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        Log.e(TAG, "service destroyed");
        super.onDestroy();
    }

    public void stopTimer() {
        onTickFinishedEvent.finishedTick = mDuration;
        EventBus.getDefault().post(onTickFinishedEvent);
        stopSelf();
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

    public void setDuration(long newDuration) {
        mDuration = newDuration;
    }

    public long getDuration() {
        return mDuration;
    }

    public void foreground(){
        startForeground(1, createNotification());
        Log.e(TAG, "foreground");
    }

    public void background(){
        stopForeground(true);
    }
    public static final long timerOffset = 200;

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