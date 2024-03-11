package edu.ucsd.cse110.successorator.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Looper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.os.Handler;
import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.successorator.util.DateRolloverMock;


/*
This class was adapted from the CardListFragment class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class TaskListFragment extends Fragment {

    private static final String PREF_TASK_DATE = "task_date";
    private static final String PREF_TASK_TIME = "task_time";
    //for when there are no tasks in the view
    private static final String DEFAULT_TEXT = "No goals for the Day.  Click the + at the upper right to enter your Most Important Thing.";
    private MainViewModel activityModel;
    private FragmentTaskListBinding view;


    private TextView DateDisplay;
    //display for the default text when there are no tasks in the view
    private TextView DefaultTextDisplay;
    private Handler handler;

    private LocalDateTime lastTime;

    private LocalDate lastDate;

    private LocalTime rolloverTime;
    private ListItemTaskBinding taskItem;

    private TaskListAdapter adapter;

    private boolean deleteFlag = false;

    private SharedPreferences sharedPreferences;


    public TaskListFragment() {
        // Required empty public constructor
    }

    public static TaskListFragment newInstance() {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        // Initialize the Adapter (with an empty list for now)
        this.adapter = new TaskListAdapter(requireContext(), List.of(), task -> {
            if (task.complete()) {
                Log.d("Debug", "Fragment called uncompleteTask");
                activityModel.uncompleteTask(task);
            } else {
                Log.d("Debug", "Fragment called completeTask");
                activityModel.completeTask(task);
            }
            adapter.notifyDataSetChanged();
        });
        activityModel.getOrderedTasks().observe(tasks -> {
            if (tasks == null) return;
            adapter.clear();
            adapter.addAll(new ArrayList<>(tasks).stream()
                                .filter(task -> task.activeDate().isBefore(LocalDate.now().plusDays(1)))
                                .collect(Collectors.toList())); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentTaskListBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.taskList.setAdapter(adapter);

        //Time functionality + mock
        LocalDateTime myDateObj = LocalDateTime.now();
        handler = new Handler(Looper.getMainLooper());

        //DateRolloverMock mockTime = new DateRolloverMock(myDateObj);
        lastTime = LocalDateTime.now();
        lastDate = LocalDate.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        String StringOfDate = myDateObj.format(myFormatObj).toString();

        this.DateDisplay = this.view.dateContent;
        view.dateContent.setText(StringOfDate);


        //this is the button responsible for switching to the recurring task fragment
        ImageButton switchButton = view.switchtorecurringbutton;
        switchButton.setOnClickListener(
                v -> {
                    RecurringTaskListFragment recur = new RecurringTaskListFragment();

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, recur);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
        );

        view.addTaskButton.setOnClickListener(v -> {
            var dialogFragment = TaskFormFragment.newInstance();
            //var dialogFragment = TaskRecurringDatePickerFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "DatePicker");
        });


        //This is the runner that checks the time every second
        Runnable updateTimeRunnable = new Runnable() {

            @Override
            public void run() {
                updateCurrentDate();
                handler.postDelayed(this, 1000);
            }



        };
        handler.post(updateTimeRunnable);

        view.mockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling updateTime with mocked = true just moves date forward
                updateCurrentTime();
                //Call deleteCompletedTasks from the taskDao
                activityModel.deleteCompletedTasks();

            }
        });

        activityModel.getOrderedTasks().observe(tasks -> {
            if(tasks == null) return;
            updateDefaultText();
        });

        return view.getRoot();
    }


    private void updateCurrentDate() {
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDate dateNow = LocalDate.now();
        LocalTime tNow = LocalTime.now();
        if (dateNow.isAfter(lastDate) && tNow.isAfter(LocalTime.of(2, 0))) {
            lastDate = dateNow;
            activityModel.deleteCompletedTasks();
        } else if (timeNow.isAfter(lastTime) )
        {
            lastTime = timeNow;
            DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

            String StringOfDate2 = lastTime.format(myFormatObj2).toString();
            DateDisplay.setText(StringOfDate2);

            //here we need to call some method to remove all tasks that are completed

        }
    }

    private void updateCurrentTime() {
        LocalDateTime timeNow = LocalDateTime.now();
        lastTime = lastTime.plusDays(1);
            DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

            String StringOfDate2 = lastTime.format(myFormatObj2).toString();
            DateDisplay.setText(StringOfDate2);

            //here we need to call some method to remove all tasks that are completed

    }

    public void updateDefaultText() {
        //check if there are no tasks available. if so, set default text. otherwise, set to empty
        this.DefaultTextDisplay = this.view.defaultText;

        if(activityModel.getOrderedTasks().getValue() == null) {
            DefaultTextDisplay.setText(DEFAULT_TEXT);
        }
        else if(activityModel.getOrderedTasks().getValue() != null && activityModel.getOrderedTasks().getValue().size()== 0) {
            DefaultTextDisplay.setText(DEFAULT_TEXT);
        }
        else {
            DefaultTextDisplay.setText("");
        }
    }


}
