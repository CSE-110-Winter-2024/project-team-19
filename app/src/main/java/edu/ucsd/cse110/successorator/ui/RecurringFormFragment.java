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

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;

import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Context;

import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.TaskBuilder;
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

        var calDialog = TaskRecurringDatePickerFragment.newInstance(activityModel.getLocalDate().getValue());

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

        //save them as variables and log them
        RadioGroup radioGroup = view.findViewById(R.id.recurring_radio_group);

        btnSubmit.setOnClickListener(v -> {
            //determine context
            Context taskContext = Context.NONE;

            RadioGroup contextGroup = view.findViewById(R.id.context_group);
            int selectedContextButtonId = contextGroup.getCheckedRadioButtonId();
            if (selectedContextButtonId != -1) {
                if (selectedContextButtonId == R.id.home_context_btn) {
                    taskContext = Context.HOME;
                }
                else if (selectedContextButtonId == R.id.work_context_btn) {
                    taskContext = Context.WORK;
                }
                else if (selectedContextButtonId == R.id.school_context_btn) {
                    taskContext = Context.SCHOOL;
                }
                else if (selectedContextButtonId == R.id.errand_context_btn) {
                    taskContext = Context.ERRAND;
                }
            } else {
                taskContext = Context.NONE;
            }


            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonId);
            String frequencyString = selectedRadioButton.getText().toString();
            EditText taskText = view.findViewById(R.id.task_text);
            String taskTextString = taskText.getText().toString();
            activityModel.insertNewTask(new TaskBuilder()
                    .withTaskName(taskTextString)
                    .withFrequency(Tasks.convertString(frequencyString))
                    .build());
            dismiss();
        });


        return view;

    }
}





