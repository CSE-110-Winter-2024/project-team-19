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

    public static TaskRecurringDatePickerFragment newInstance(LocalDate date) {
        var fragment = new TaskRecurringDatePickerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.onDateSet(null, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it.

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePickerDialog;
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
