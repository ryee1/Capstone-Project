package com.project.richard.insightjournal.ui.timerscreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.utils.SharedPrefUtils;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.timer_fragment_container, TimerFragment.newInstance(SharedPrefUtils.getIdPref(this)))
                    .commit();
        }
    }

}
