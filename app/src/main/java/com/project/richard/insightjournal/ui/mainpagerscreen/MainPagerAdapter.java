package com.project.richard.insightjournal.ui.mainpagerscreen;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.project.richard.insightjournal.ui.mainpagerscreen.linksscreen.LinksFragment;
import com.project.richard.insightjournal.ui.mainpagerscreen.logsscreen.LogFragment;
import com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen.TimerSettingFragment;

/**
 * Created by a11 on 6/3/16.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private static final int TIMER_SETTING_TAB = 0;
    private static final int LOG_TAB = 1;
    private static final int LINKS_TAB = 2;

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"Timer", "Log", "Links"};
    private Context context;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;
        switch(position){
            case TIMER_SETTING_TAB:
                f = TimerSettingFragment.newInstance(position + 1);
                break;
            case LOG_TAB:
                f = LogFragment.newInstance();
                break;
            case LINKS_TAB:
                f = LinksFragment.newInstance();
                break;
            default:
                return null;
        }
        return f;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
