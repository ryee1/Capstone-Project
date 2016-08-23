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
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.database.PresetsColumns;
import com.project.richard.insightjournal.events.OnAddressFetchedEvent;
import com.project.richard.insightjournal.events.OnPrepTickEvent;
import com.project.richard.insightjournal.events.OnTickEvent;
import com.project.richard.insightjournal.events.OnTickFinishedEvent;
import com.project.richard.insightjournal.ui.mainpagerscreen.PagerActivity;
import com.project.richard.insightjournal.utils.PermissionsUtils;
import com.project.richard.insightjournal.utils.TimerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.google.android.gms.location.LocationServices.FusedLocationApi;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = TimerFragment.class.getSimpleName();
    private static final String BOOLEAN_TIMER_RUNNING = "boolean_timer_running";
    private static final String LONG_TIMER_DURATION = "long_timer_duration";

    public static final String PRESET_TYPE = "preset_type";
    public static final String PRESET_RECORD_TOGGLE = "preset_record_toggle";

    private String mType;
    private long mMaxDuration;
    private long mMaxPrep;
    private boolean mTimerRunning;
    private boolean mBound;
    private boolean mRecordToggleOn;
    private TimerService mTimerService;
    private Unbinder unbinder;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastKnownLocation;

    //threshold in milliseconds for session logging dialog screen to pop up
    private long mDialogThreshold = 10000;

    @BindView(R.id.timer_screen_parent_layout) RelativeLayout mTimerParentLayout;
    @BindView(R.id.circle_timer_view) CircularProgressBar mCircleTimerView;
    @BindView(R.id.digital_timer_view) TextView mDigitalTimerView;
    @BindView(R.id.btn_timer_start) Button mStartButton;

    public static TimerFragment newInstance(String type, boolean toggleOn) {
        Bundle args = new Bundle();
        TimerFragment fragment = new TimerFragment();
        args.putString(PRESET_TYPE, type);
        args.putBoolean(PRESET_RECORD_TOGGLE, toggleOn);
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
        final View view = inflater.inflate(R.layout.fragment_timer, container, false);
        unbinder = ButterKnife.bind(this, view);

        final GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(getContext(), new GestureListener());

        mRecordToggleOn = getArguments().getBoolean(PRESET_RECORD_TOGGLE);
        mType = getArguments().getString(PRESET_TYPE);
        Cursor c = getActivity().getContentResolver().query(LogsProvider.Presets.PRESETS, null,
                PresetsColumns.TYPE + " = ?", new String[]{mType}, null);
        if (c != null && c.moveToFirst()) {
            mMaxDuration = c.getLong(c.getColumnIndex(PresetsColumns.DURATION));
            mMaxPrep = c.getLong(c.getColumnIndex(PresetsColumns.PREPARATION_TIME));
            c.close();
        } else {
            Log.e(TAG, "Cannot find Preset");
        }

        if (savedInstanceState != null) {
            mTimerRunning = savedInstanceState.getBoolean(BOOLEAN_TIMER_RUNNING);
            mDigitalTimerView.setText(TimerUtils.millisToDigital(savedInstanceState.getLong(LONG_TIMER_DURATION)));
            mCircleTimerView.setProgress((float) savedInstanceState.getLong(LONG_TIMER_DURATION) / mMaxDuration * 100);
        }
        else{
            mDigitalTimerView.setText(TimerUtils.millisToDigital(mMaxDuration));
        }

        if (mRecordToggleOn && mType.equals(PresetsColumns.SITTING_MEDITAION)) {
            mTimerParentLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override public boolean onTouch(View v, MotionEvent event) {
                    mGestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override public void onConnected(@Nullable Bundle bundle) {
                        Log.e(TAG, "connected");
                        if (!PermissionsUtils.checkLocationPermissions(getContext())) {
                            Log.e(TAG, "permission needed");
                            PermissionsUtils.requestLocationPermissions(getActivity(), R.id.timer_screen_parent_layout);
                        }
                        mLastKnownLocation = FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if(mTimerService != null && mLastKnownLocation != null){
                            mTimerService.setmLastLocation(mLastKnownLocation);
                            Intent intent = new Intent(getContext(), FetchAddressIntentService.class);
                            intent.putExtra(FetchAddressIntentService.LATITUDE_EXTRA, mLastKnownLocation.getLatitude());
                            intent.putExtra(FetchAddressIntentService.LONTITUDE_EXTRA, mLastKnownLocation.getLongitude());
                            getContext().startService(intent);
                        }
                        else{
                            Log.e(TAG, "location not fetched");
                        }
                    }

                    @Override public void onConnectionSuspended(int i) {
                        Log.e(TAG, "connection suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "connection failed");
                    }
                })
                .addApi(LocationServices.API)
                .build();


        return view;
    }

    @Override public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        Intent intent = new Intent(getActivity(), TimerService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);
        mGoogleApiClient.connect();
    }

    @Override public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
            Log.e(TAG, "unbind service");
        }
        mGoogleApiClient.disconnect();

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
                mTimerService.setPrep(mMaxPrep);
            }

            mCircleTimerView.setProgress((float) mTimerService.getDuration() / mMaxDuration * 100);
            mDigitalTimerView.setText(TimerUtils.millisToDigital(mTimerService.getDuration()));
            //If service's duration doesn't match the maxduration, that means timer is already running
            if (timerRan()) {
                mTimerRunning = true;
            }
        }

        @Override public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public boolean timerRan() {
        return mTimerService.getDuration() != mMaxDuration;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    public void stopTimerOnBackpress() {
        mTimerService.stopTimer();
        mTimerRunning = false;
    }

    @Subscribe
    public void onPrepTickEvent(OnPrepTickEvent event) {
        mDigitalTimerView.setText(TimerUtils.millisToDigital(event.currentTick));
    }

    @Subscribe
    public void onTickEvent(OnTickEvent event) {
        mDigitalTimerView.setText(TimerUtils.millisToDigital(event.currentTick));
        mCircleTimerView.setProgress((float) event.currentTick / mMaxDuration * 100);
    }

    @Subscribe
    public void onTickFinishedEvent(OnTickFinishedEvent event) {
        mTimerRunning = false;
        String location = null;
        mDigitalTimerView.setText(TimerUtils.millisToDigital(event.finishedTick));
        mCircleTimerView.setProgress((float) event.finishedTick / mMaxDuration * 100);
        if (mMaxDuration - event.finishedTick < mDialogThreshold) {
            Intent intent = new Intent(getActivity(), PagerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }
        if(mTimerService.getAddress() != null){
            Log.e(TAG, mTimerService.getAddress().getLocality());
            location = mTimerService.getAddress().getLocality();
        }
        else{
            Log.e(TAG, "location is null");
        }
        StopTimerDialogFragment dialog = StopTimerDialogFragment.newInstance(
                TimerUtils.millisToMillisRemaining(mMaxDuration, event.finishedTick),
                System.currentTimeMillis(), mType, location);
        dialog.show(getActivity().getSupportFragmentManager(), StopTimerDialogFragment.class.getSimpleName());

    }

    @Subscribe
    public void onAddressFetchedEvent(OnAddressFetchedEvent event){
        mTimerService.setAddress(event.address);
    }


    @OnClick(R.id.btn_timer_start)
    public void startTimer() {
        if (!mTimerRunning) {
            mStartButton.setText(R.string.timer_start_button_onpause);
            mTimerRunning = true;
            mTimerService.startTimer(mTimerService.getDuration(), mTimerService.getPrep());
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

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connectoin failed");
    }

    //
    // code used from http://stackoverflow.com/a/4098447/3377155
    //

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private final int SWIPE_MIN_DISTANCE = 40;
        private final int SWIPE_THRESHOLD_VELOCITY = 40;

        @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Left to right
            }

            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }
            return false;
        }
    }
}
