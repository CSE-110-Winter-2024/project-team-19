package edu.ucsd.cse110.successorator.ui;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.Task;

/*
This class was adapted from the CardListFragment provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class TaskFormFragment extends DialogFragment {

    public TaskFormFragment() {

    }

    public static TaskFormFragment newInstance() {
        var fragment = new TaskFormFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task_form, container, false);

        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        var activityModel = modelProvider.get(MainViewModel.class);

        Calendar calendar = Calendar.getInstance();

        Button btnClose = view.findViewById(R.id.close_button);
        btnClose.setOnClickListener(v -> {
            dismiss(); // Close the popup when the close button is clicked
        });

        Button btnSubmit = view.findViewById(R.id.submit_button);

        btnSubmit.setOnClickListener(v -> {
            EditText taskText = view.findViewById(R.id.task_text);
            String taskTextString = taskText.getText().toString();
            activityModel.insertNewTask(new Task(null, taskTextString, 2, false));
            dismiss();
        });

        RadioButton weeklyButton = view.findViewById(R.id.weekly_button);

        String dayOfWeek = getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));
        weeklyButton.setText("Weekly on " + dayOfWeek);

        RadioButton monthlyButton = view.findViewById(R.id.monthly_button);

        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        String weekOfMonthTense = getTense(weekOfMonth);
        monthlyButton.setText("Monthly on " + weekOfMonthTense + " " + dayOfWeek);

        RadioButton yearlyButton = view.findViewById(R.id.yearly_button);

        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        yearlyButton.setText("Yearly on " + formatDate(month, day));

        return view;

    }

    private String formatDate(int month, int day) {
        return String.format("%02d/%02d", month, day);
    }

    private static String getTense(int weekOfMonth) {
        String[] days = {"", "1st", "2nd", "3rd", "4th", "5th"};
        return days[weekOfMonth];
    }

    private static String getDayOfWeekString(int dayOfWeek) {
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayOfWeek];
    }

}





