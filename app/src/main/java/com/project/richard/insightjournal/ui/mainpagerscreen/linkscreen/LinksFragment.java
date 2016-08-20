package com.project.richard.insightjournal.ui.mainpagerscreen.linkscreen;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.project.richard.insightjournal.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class LinksFragment extends Fragment {
    public static LinksFragment newInstance() {
        Bundle args = new Bundle();
        LinksFragment fragment = new LinksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LinksFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.adView) AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_resources, container, false);
        ButterKnife.bind(this, view);
        Log.e("adcheck", getString(R.string.banner_ad_unit_id));
        MobileAds.initialize(getContext()   , getString(R.string.banner_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        return view;
    }

}
