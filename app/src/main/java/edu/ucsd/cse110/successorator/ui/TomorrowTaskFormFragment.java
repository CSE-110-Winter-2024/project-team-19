package edu.ucsd.cse110.successorator.ui;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.Context;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.MockLocalDate;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.TaskBuilder;
import edu.ucsd.cse110.successorator.lib.domain.Tasks;

/*
This class was adapted from the CardListFragment provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class TomorrowTaskFormFragment extends DialogFragment {

    public TomorrowTaskFormFragment() {

    }

    public static TomorrowTaskFormFragment newInstance() {
        var fragment = new TomorrowTaskFormFragment();
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
        //move calendar up one day for tomorrows time
        calendar.add(Calendar.DAY_OF_YEAR, 1);



        Button btnClose = view.findViewById(R.id.close_button);
        btnClose.setOnClickListener(v -> {
            dismiss(); // Close the popup when the close button is clicked
        });

        Button btnSubmit = view.findViewById(R.id.submit_button);

        // Set radio button text to dynamic text matching current date + frequency

        RadioButton weeklyButton = view.findViewById(R.id.weekly_button);

        String dayOfWeek = getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));
        weeklyButton.setText("Weekly on " + dayOfWeek);

        RadioButton monthlyButton = view.findViewById(R.id.monthly_button);

        LocalDate currentDate = activityModel.getLocalDate().getValue().plusDays(1); //just added one from today
        Month currentMonth = currentDate.getMonth();
        DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();
        int occurrence = getDayOccurrenceInMonth(currentDate.getYear(), currentMonth, currentDayOfWeek);
        String weekOfMonthTense = getTense(occurrence);
        monthlyButton.setText("Monthly on " + weekOfMonthTense + " " + dayOfWeek);

        RadioButton yearlyButton = view.findViewById(R.id.yearly_button);

        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        yearlyButton.setText("Yearly on " + formatDate(month, day));

        btnSubmit.setOnClickListener(v -> {
            EditText taskText = view.findViewById(R.id.task_text);
            String taskTextString = taskText.getText().toString();
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

            Frequency taskFreq = Frequency.ONE_TIME;
            // Add respective task with selected frequency to DB based on radio button selection
            RadioGroup radioGroup = view.findViewById(R.id.radio_group);
            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            if (selectedRadioButtonId != -1) {
                if (selectedRadioButtonId == R.id.onetime_button) {
                    taskFreq = Frequency.ONE_TIME;
                } else if (selectedRadioButtonId == R.id.daily_button) {
                    taskFreq = Frequency.DAILY;
                } else if (selectedRadioButtonId == R.id.weekly_button) {
                    taskFreq = Frequency.WEEKLY;
                } else if (selectedRadioButtonId == R.id.monthly_button) {
                    taskFreq = Frequency.MONTHLY;
                } else if (selectedRadioButtonId == R.id.yearly_button) {
                    taskFreq = Frequency.YEARLY;
                }
            }

            activityModel.insertNewTask(new TaskBuilder().withTaskName(taskTextString)
                    .withFrequency(taskFreq)
                    .withContext(taskContext)
                    .withActiveDate(MockLocalDate.now().plusDays(1))
                    .build());
            dismiss();
        });

        return view;

    }

    private String formatDate(int month, int day) {
        return String.format(month + "/%02d", day);
    }

    private static String getTense(int weekOfMonth) {
        String[] days = {"", "1st", "2nd", "3rd", "4th", "5th"};
        return days[weekOfMonth];
    }

    private static String getDayOfWeekString(int dayOfWeek) {
        String[] days = {"", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayOfWeek];
    }

    private static int getDayOccurrenceInMonth(int year, Month month, DayOfWeek dayOfWeek) {
        int occurrence = 1;
        LocalDate date = LocalDate.of(year, month, 1);

        while (!date.getDayOfWeek().equals(dayOfWeek)) {
            date = date.plusDays(1);
        }

        while (date.getMonth() == month) {
            if (date.getDayOfWeek().equals(dayOfWeek)) {
                return occurrence;
            }
            date = date.plusDays(7);
            occurrence++;
        }

        return -1;
    }

}





