package com.project.richard.insightjournal.ui.mainscreen;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.richard.insightjournal.R;


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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resources, container, false);
    }

}
