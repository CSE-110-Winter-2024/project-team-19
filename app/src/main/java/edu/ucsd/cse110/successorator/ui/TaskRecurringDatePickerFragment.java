package edu.ucsd.cse110.successorator.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;

public class TaskRecurringDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private int year;
    private int month;
    private int day;

    public static TaskRecurringDatePickerFragment newInstance() {
        var fragment = new TaskRecurringDatePickerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        final Calendar c = Calendar.getInstance();
        int yearTemp = c.get(Calendar.YEAR);
        int monthTemp = c.get(Calendar.MONTH);
        int dayTemp = c.get(Calendar.DAY_OF_MONTH);
        fragment.onDateSet(null, yearTemp, monthTemp, dayTemp);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public LocalDate getPickedDate(){
        return LocalDate.of(year, month + 1, day);
    }
}
