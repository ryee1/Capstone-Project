package com.project.richard.insightjournal.ui.mainpagerscreen.logscreen;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.ui.DividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = LogFragment.class.getSimpleName();
    private static final int LOG_LOADER = 0;

    private LogAdapter mLogAdapter;
    private Unbinder unbinder;
    @BindView(R.id.log_recyclerview) RecyclerView recyclerView;

    public static LogFragment newInstance() {
        Bundle args = new Bundle();
        LogFragment fragment = new LogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LogFragment() {
        // Required empty public constructor
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOG_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        unbinder = ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        mLogAdapter = new LogAdapter(getContext());

        recyclerView.setAdapter(mLogAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_log, menu);
        MenuItem toggleLog = menu.findItem(R.id.action_toggle_log);
        if(mLogAdapter.isShowAllViews()){
            toggleLog.setIcon(R.drawable.ic_visibility_off_white_18dp);
        }
        else{
            toggleLog.setIcon(R.drawable.ic_visibility_black_18dp);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_toggle_log:
                mLogAdapter.toggleStatesMap();
                if(mLogAdapter.isShowAllViews()){
                    item.setIcon(R.drawable.ic_visibility_off_white_18dp);
                }
                else{
                    item.setIcon(R.drawable.ic_visibility_black_18dp);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onPause() {
        super.onPause();
    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                LogsProvider.Logs.LOGS,
                null, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLogAdapter.swapCursor(data);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        mLogAdapter.swapCursor(null);
    }
}
