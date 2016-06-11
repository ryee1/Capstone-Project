package com.project.richard.insightjournal;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerDialogFragment extends DialogFragment{

    @BindView(R.id.spinner_second) Spinner secSpinner;
    @BindView(R.id.spinner_minute) Spinner minSpinner;
    @BindView(R.id.spinner_hour) Spinner hourSpinner;

    @OnClick(R.id.confirm_button_time_picker)
    public void confirm(){
        Log.e("a", "" + secSpinner.getSelectedItemPosition());
    }
 @Override public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
     View view = inflater.inflate(R.layout.fragment_time_picker_dialog, container, false);
     ButterKnife.bind(this, view);

     ArrayAdapter<String> secAdapter = new ArrayAdapter<String>(getContext(),
             R.layout.support_simple_spinner_dropdown_item, TimerUtils.createSecondsArrayList(getContext()));
     ArrayAdapter<String> minAdapter = new ArrayAdapter<String>(getContext(),
             R.layout.support_simple_spinner_dropdown_item, TimerUtils.createMinutesArrayList(getContext()));
     ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(getContext(),
             R.layout.support_simple_spinner_dropdown_item, TimerUtils.createHoursArrayList(getContext()));
     secSpinner.setAdapter(secAdapter);
     minSpinner.setAdapter(minAdapter);
     hourSpinner.setAdapter(hourAdapter);
     return view;
    }
}
