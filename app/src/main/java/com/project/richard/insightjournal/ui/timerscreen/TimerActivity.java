package com.project.richard.insightjournal.ui.timerscreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.utils.SharedPrefUtils;

public class TimerActivity extends AppCompatActivity {

    private static final String TAG = TimerActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.timer_fragment_container, TimerFragment.newInstance(SharedPrefUtils.getTitlePref(this)))
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
