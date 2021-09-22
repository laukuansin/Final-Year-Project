package com.example.a303com_laukuansin.utilities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import com.example.a303com_laukuansin.R;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimePickerDialogFragment extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener _onTimeSet;
    private int hour,minute;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hour = args.getInt("hour");
        minute = args.getInt("minute");
    }

    public TimePickerDialogFragment() {
    }

    public void setCallBack(TimePickerDialog.OnTimeSetListener onTime ) {
        _onTimeSet = onTime;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), R.style.TimePickerTheme,_onTimeSet,hour,minute,false);
    }
}
