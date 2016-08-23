package com.project.richard.insightjournal.ui.mainpagerscreen.linkscreen;


import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

    @BindView(R.id.catherine_shaila_hyperlink) TextView shailaHyperlink;
    @BindView(R.id.adView) AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_resources, container, false);
        ButterKnife.bind(this, view);

        shailaHyperlink.setText(Html.fromHtml(getContext().getString(R.string.shaila_catherine_hyperlink)));
        shailaHyperlink.setMovementMethod(LinkMovementMethod.getInstance());

        String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(android_id)
                .build();
        mAdView.loadAd(adRequest);

        return view;
    }

}
