package edu.ucsd.cse110.successorator.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Looper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import java.util.Arrays;
import java.util.List;
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
    private MainViewModel activityModel;
    private FragmentTaskListBinding view;


    private TextView DateDisplay;
    private Handler handler;

    private LocalDateTime lastTime;

    private LocalDate lastDate;

    private LocalTime rolloverTime;
    private ListItemTaskBinding taskItem;

    private TaskListAdapter adapter;

    private boolean deleteFlag = false;

    private SharedPreferences sharedPreferences;

    //initializing Spinner variables
    Spinner viewTitleDropdown;
    ArrayAdapter<String> viewTitleAdapter;
    String[] spinnerItems;

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
            adapter.addAll(new ArrayList<>(tasks)); // remember the mutable copy here!
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
        LocalDateTime myNextDateObj = myDateObj.plusDays(1);
        handler = new Handler(Looper.getMainLooper());


        DateRolloverMock mockTime = new DateRolloverMock(myDateObj);
        lastTime = LocalDateTime.now();
        lastDate = LocalDate.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        String StringOfDate = myDateObj.format(myFormatObj).toString();
        String StringOfNextDate = myNextDateObj.format(myFormatObj).toString();

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

        // Prepping dropdown
        // TODO: Improve this note
//        Spinner spinnerViewTitles = view.viewTitle;
//        ArrayAdapter<CharSequence> adapterViewTitles = ArrayAdapter.createFromResource(this.getActivity(), R.array.view_titles, android.R.layout.simple_spinner_item);
//        adapterViewTitles.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        spinnerViewTitles.setAdapter(adapterViewTitles); // LINE WITH ISSUE; INVOKING ON NULL
        // TODO: Do implementation below; will help with str substitution
        //viewTitleDropdown is a Spinner, viewTitleAdapter is the Spinner Adapter, spinnerItems is list of strings
        viewTitleDropdown = view.viewTitle;
        spinnerItems = new String[]{"Today, " + StringOfDate, "Tomorrow, " + StringOfNextDate, "Recurring", "Pending"};
        viewTitleAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        viewTitleDropdown.setAdapter(viewTitleAdapter);


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



        return view.getRoot();
    }

    //dynamically updating spinner time using this StackOverflow post: https://stackoverflow.com/questions/3283337/how-to-update-a-spinner-dynamically
    private void updateCurrentDate() {
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeTmrw = timeNow.plusDays(1);
        LocalDate dateNow = LocalDate.now();
        LocalTime tNow = LocalTime.now();
        if (dateNow.isAfter(lastDate) && tNow.isAfter(LocalTime.of(2, 0))) {
            lastDate = dateNow;
            activityModel.deleteCompletedTasks();
        } else if (timeNow.isAfter(lastTime) )
        {
            lastTime = timeNow;
            DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

            //declaring strings to put into the spinner dropdown
            String StringOfNewNowDate = lastTime.format(myFormatObj2).toString();
            String StringOfNewTmrwDate = lastTime.format(myFormatObj2).toString();
            DateDisplay.setText(StringOfNewNowDate);

            //here we need to call some method to remove all tasks that are completed

        }
    }

    private void updateCurrentTime() {
        LocalDateTime timeNow = LocalDateTime.now();
        lastTime = lastTime.plusDays(1);
        DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        String StringOfNewNowDate = lastTime.format(myFormatObj2).toString();
        String StringOfNewTmrwDate = lastTime.plusDays(1).format(myFormatObj2).toString();
        DateDisplay.setText(StringOfNewNowDate);

        //updating dates on dropdown spinner item viewTitleDropdown
        spinnerItems = new String[]{
                "Today, " + StringOfNewNowDate,
                "Tomorrow, " + StringOfNewTmrwDate,
                "Recurring",
                "Pending"
        };
        //viewTitleAdapter.notifyDataSetChanged();
        viewTitleAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        viewTitleDropdown.setAdapter(viewTitleAdapter);

        //here we need to call some method to remove all tasks that are completed

    }




    }
