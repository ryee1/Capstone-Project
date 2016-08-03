package com.project.richard.insightjournal.ui.timerscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.utils.SharedPrefUtils;

public class TimerActivity extends AppCompatActivity {

    private static final String TAG = TimerActivity.class.getSimpleName();
    public static final String TOGGLE_ON_EXTRA_TAG = "toggle_on_extra_tag";

    public static Intent newIntent(Context context, boolean toggleOn){
        Bundle b = new Bundle();
        Intent intent = new Intent(context, TimerActivity.class);
        intent.putExtra(TOGGLE_ON_EXTRA_TAG, toggleOn);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.timer_fragment_container, TimerFragment.newInstance(
                            SharedPrefUtils.getTypePref(this), getIntent().getBooleanExtra(TOGGLE_ON_EXTRA_TAG, false)))
                    .commit();
        }
    }

    //TODO implement proper back navigation when timer is running
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)  {
//        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
//                && keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
//            onBackPressed();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//
    @Override
    public void onBackPressed() {
        TimerFragment f = (TimerFragment)getSupportFragmentManager().findFragmentById(R.id.timer_fragment_container);
        Log.e(TAG, "timerfirstrunonbackpress: " + f.timerRan());
        if (f != null && f.timerRan()){
            f.stopTimerOnBackpress();
        }
        else{
            super.onBackPressed();
        }
    }
}
