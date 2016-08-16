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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.events.OnPrepTickEvent;
import com.project.richard.insightjournal.events.OnTickEvent;
import com.project.richard.insightjournal.events.OnTickFinishedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

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
    private GoogleApiClient mGoogleApiClient;
    private OnDataPointListener mListener;


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
                    unregisterFitnessDataListener();
                    stopSelf();
                }
            }.start();
        }
        mGoogleApiClient.connect();
        findFitnessDataSources();

    }
    public void pauseTimer() {
        mCountDownTimer.cancel();
    }

    public void stopTimer() {
        onTickFinishedEvent.finishedTick = mDuration;
        EventBus.getDefault().post(onTickFinishedEvent);
        unregisterFitnessDataListener();
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


    public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        this.mGoogleApiClient = mGoogleApiClient;
    }
    private void findFitnessDataSources() {
        // [START find_data_sources]
        // Note: Fitness.SensorsApi.findDataSources() requires the ACCESS_FINE_LOCATION permission.

        if(mListener != null){
            Log.e(TAG, "mlistener not null");
        }
        else{
            Log.e(TAG, "mlistener is null");
        }
        if(mGoogleApiClient == null){
            Log.e(TAG, "client null");
        }
        else{
            Log.e(TAG, "client not null");
        }

        Fitness.SensorsApi.findDataSources(mGoogleApiClient, new DataSourcesRequest.Builder()
                // At least one datatype must be specified.
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                // Can specify whether data type is raw or derived.
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            //Let's register a listener to receive Activity data!
                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)
                                    && mListener == null) {
                                Log.i(TAG, "Data source for LOCATION_SAMPLE found!  Registering.");
                                registerFitnessDataListener(dataSource,
                                        DataType.TYPE_STEP_COUNT_DELTA);
                            }
                        }
                    }
                });
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        // [START register_data_listener]
        Log.e(TAG, "test123: " + dataSource.toString() + "\n" + dataType.toString());
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
                    Log.i(TAG, "Detected DataPoint value: " + val);
                }
                Log.e(TAG, "ondatapoint ran");
            }
        };
        Fitness.SensorsApi.add(
                mGoogleApiClient,
                new SensorRequest.Builder()
                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA) // Can't be omitted.
                        .setSamplingRate(3, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener registered!");
                        } else {
                            Log.i(TAG, "Listener not registered.");
                        }
                    }
                });
    }
    private void unregisterFitnessDataListener() {
        if (mListener == null) {
            return;
        }

        Fitness.SensorsApi.remove(
                mGoogleApiClient,
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener was removed!");
                        } else {
                            Log.i(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
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