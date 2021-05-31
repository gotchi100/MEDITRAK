package com.mobdeve.meditrak;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.mobdeve.meditrak.data.Schedule;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimePickerFragment extends DialogFragment
                            implements TimePickerDialog.OnTimeSetListener {

    private int hour, minute;
    private Schedule sched_to_create;
    private AtomicBoolean timeWasSet;
    private TextView tvTimeVal;

    public TimePickerFragment(Schedule sched_to_create, TextView tvTimeVal, AtomicBoolean timeWasSet) {
        this.sched_to_create = sched_to_create;
        this.tvTimeVal = tvTimeVal;
        this.timeWasSet = timeWasSet;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog td = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        td.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return td;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        this.hour = hourOfDay;
        this.minute = minute;

        sched_to_create.setTime(this.getTime());
        this.tvTimeVal.setText(sched_to_create.getTimeDisplay());
        this.timeWasSet.set(true);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getTime() {
        return this.hour + ":" + this.minute;
    }
}