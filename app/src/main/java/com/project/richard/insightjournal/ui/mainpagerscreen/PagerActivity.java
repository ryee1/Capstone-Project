package com.project.richard.insightjournal.ui.mainpagerscreen;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.MobileAds;
import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.database.PresetsColumns;
import com.project.richard.insightjournal.utils.ContentValuesUtils;
import com.project.richard.insightjournal.utils.PermissionsUtils;
import com.project.richard.insightjournal.utils.SharedPrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PagerActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewpager_main) ViewPager viewPager;
    @BindView(R.id.sliding_tabs_main) TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (!PermissionsUtils.checkLocationPermissions(this)) {
            Log.e("PagerActivity", "permission needed");
            PermissionsUtils.requestLocationPermissions(this, R.id.toolbar);
        }
        if(SharedPrefUtils.isFirstStart(this)){
            SharedPrefUtils.setFirstStart(this);
//            Intent i = new Intent(PagerActivity.this, IntroActivity.class);
//            startActivity(i);
            initializeFirstStart();
        }
        setSupportActionBar(toolbar);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), PagerActivity.this));
        tabLayout.setupWithViewPager(viewPager);


        MobileAds.initialize(this, getString(R.string.banner_ad_unit_id));
    }

    private void initializeFirstStart(){
        SharedPrefUtils.setTitlePref(this, PresetsColumns.SITTING_MEDITAION);
        getContentResolver().insert(LogsProvider.Presets.PRESETS,
                ContentValuesUtils.initialPresetContentValues());
        getContentResolver().insert(LogsProvider.Goals.GOALS,
                ContentValuesUtils.initialGoalsContentValues(this));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
