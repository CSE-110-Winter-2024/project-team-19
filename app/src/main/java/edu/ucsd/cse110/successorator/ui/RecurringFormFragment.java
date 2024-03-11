package edu.ucsd.cse110.successorator.ui;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Context;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.Tasks;

/*
This class was adapted from the CardListFragment provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class RecurringFormFragment extends DialogFragment {

    public RecurringFormFragment() {

    }

    public static RecurringFormFragment newInstance() {
        var fragment = new RecurringFormFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recurring_task_form, container, false);

        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        var activityModel = modelProvider.get(MainViewModel.class);

        var calDialog = TaskRecurringDatePickerFragment.newInstance();

        //open the calendar
        ImageButton calButton = view.findViewById(R.id.openCalendarButton);
        calButton.setOnClickListener(
                v -> {
                    calDialog.show(getParentFragmentManager(),"calendar");
                }
        );


        Button btnClose = view.findViewById(R.id.recurringCancelButton);
        btnClose.setOnClickListener(v -> {
            dismiss(); // Close the popup when the close button is clicked
        });

        Button btnSubmit = view.findViewById(R.id.recurringSubmitButton);

        //TODO - get the value of the date picker and radio buttons
        //save them as variables and log them
        RadioGroup radioGroup = view.findViewById(R.id.recurring_radio_group);
        RadioButton dailyRadio = view.findViewById(R.id.daily_button);
        RadioButton weeklyRadio = view.findViewById(R.id.weekly_button);
        RadioButton monthlyRadio = view.findViewById(R.id.monthly_button);
        RadioButton yearlyRadio = view.findViewById(R.id.yearly_button);

        //set daily button as default
        dailyRadio.setChecked(true);


        btnSubmit.setOnClickListener(v -> {
            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonId);
            String frequencyString = selectedRadioButton.getText().toString();
            EditText taskText = view.findViewById(R.id.task_text);
            String taskTextString = taskText.getText().toString();
            Task toInsert = new Task(null, taskTextString, 2, false,
                    calDialog.getPickedDate(), Tasks.convertString(frequencyString),
                    calDialog.getPickedDate().getDayOfWeek(),
                    Tasks.calculateOccurrence(calDialog.getPickedDate()), Context.NONE);
            activityModel.insertNewTask(toInsert);
            Log.d("ReccurringFormFragment", toInsert.toString());
            dismiss();
        });


        return view;

    }
}





