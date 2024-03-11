package edu.ucsd.cse110.successorator.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentRecurringTasksBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.databinding.ListItemTaskBinding;

public class TomorrowTaskListFragment extends Fragment {
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
    ArrayList<String> spinnerItems;

    public TomorrowTaskListFragment() {
        // Required empty public constructor
    }

    public static TomorrowTaskListFragment newInstance() {
        TomorrowTaskListFragment fragment = new TomorrowTaskListFragment();
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
                    .filter(task -> task.activeDate().isAfter(LocalDate.now()) && task.activeDate().isBefore(LocalDate.now().plusDays(2)))
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

        lastTime = LocalDateTime.now().plusDays(1);

        lastDate = LocalDate.now().plusDays(1);

        // Create dates for date dropdown
        LocalDateTime myDateObj = LocalDateTime.now().plusDays(1);
        LocalDateTime myNextDateObj = myDateObj.minusDays(1); //this is "yesterday of tomorrow" aka today
        handler = new Handler(Looper.getMainLooper());
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        String StringOfDate = myDateObj.format(myFormatObj).toString();
        String StringOfNextDate = myNextDateObj.format(myFormatObj).toString();

        // Assign values to task view by date dropdown in header
        //viewTitleDropdown is a Spinner, viewTitleAdapter is the Spinner Adapter, spinnerItems is list of strings
        viewTitleDropdown = view.viewTitle;
        spinnerItems = new ArrayList<String>(Arrays.asList( "Tomorrow (current view), " + StringOfDate, "Today, " + StringOfNextDate, "Recurring", "Pending"));
        viewTitleAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        viewTitleDropdown.setAdapter(viewTitleAdapter);

        /*
         * adding cases to tell the spinner what to do when switching to Today (TaskListFragment),
         * Tomorrow (TmrwTaskListFragment), Recurring (RecurringTaskListFragment), and
         * Pending (PendingTaskListFragment)
         */
        viewTitleDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection and switch fragments here

                switch (position) {
                    case 0:
                        // WARNING: uncommenting the below will disable the dropdown
                        //loadFragment(new RecurringTaskListFragment());
                        break;

                    case 1:
                        loadFragment(new TaskListFragment());
                        break;
                    case 2:
                        loadFragment(new RecurringTaskListFragment());
                    case 3:
                        // loadFragment(new PendingTaskListFragment());
                        break;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
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


        view.addTaskButton.setOnClickListener(v -> {
            var dialogFragment = TomorrowTaskFormFragment.newInstance();
            //var dialogFragment = TaskRecurringDatePickerFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "DatePicker");
        });

        return view.getRoot();
    }


    private void updateCurrentDate() {
        LocalDateTime timeNow = LocalDateTime.now().plusDays(1);
        LocalDateTime timeTmrw = timeNow.minusDays(1); //remember here its switched for tomorrow view
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
            String StringOfNewTmrwDate = lastTime.minusDays(1).format(myFormatObj2).toString();
            //DateDisplay.setText(StringOfNewNowDate);

            updateDropdown(StringOfNewNowDate, StringOfNewTmrwDate);

            //here we need to call some method to remove all tasks that are completed

        }
    }

    private void updateCurrentTime() {
        LocalDateTime timeNow = LocalDateTime.now();
        lastTime = lastTime.plusDays(1); //its tomorrow now
        DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        String StringOfNewNowDate = lastTime.format(myFormatObj2).toString();
        String StringOfNewTmrwDate = lastTime.minusDays(1).format(myFormatObj2).toString();
        //DateDisplay.setText(StringOfNewNowDate);

        updateDropdown(StringOfNewNowDate, StringOfNewTmrwDate);

        //here we need to call some method to remove all tasks that are completed

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void updateDropdown(String newDate, String newTmrwDate) {
        //updating dates on dropdown spinner item viewTitleDropdown
        spinnerItems = new ArrayList<String>(Arrays.asList(
                "Tomorrow (currentView), " + newDate,
                "Today, " + newTmrwDate,
                "Recurring",
                "Pending"
                    ));
        //viewTitleAdapter.notifyDataSetChanged();
        viewTitleAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        viewTitleDropdown.setAdapter(viewTitleAdapter);
    }
        }
