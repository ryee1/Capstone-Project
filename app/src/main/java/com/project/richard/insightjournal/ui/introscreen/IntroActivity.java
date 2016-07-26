package com.project.richard.insightjournal.ui.introscreen;

import android.os.Bundle;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.utils.ContentValuesUtils;
import com.project.richard.insightjournal.utils.SharedPrefUtils;

public class IntroActivity extends AppIntro {

    private static final String TAG = IntroActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "Intro Ran");
        addSlide(AppIntroFragment.newInstance("sadf", "blahbl", R.drawable.ic_arrow_forward_white_24px, R.color.cardview_shadow_end_color));
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.

        SharedPrefUtils.addTitlePref(this, "Default Preset");
        getContentResolver().insert(LogsProvider.Presets.PRESETS,
                ContentValuesUtils.presetContentValues("Default Preset", 5, 30));
        finish();
    }
}
