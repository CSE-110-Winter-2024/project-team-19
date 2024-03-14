package edu.ucsd.cse110.successorator.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Looper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
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
import java.util.stream.Collectors;

import android.os.Handler;
import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.util.MockLocalDate;


/*
This class was adapted from the CardListFragment class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class TaskListFragment extends Fragment {

    private static final String PREF_TASK_DATE = "task_date";
    private static final String PREF_TASK_TIME = "task_time";
    private MainViewModel activityModel;
    private FragmentTaskListBinding view;

    //TIME VARIABLES
    private LocalDate timeNow;


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
                    .filter(task -> task.activeDate().isBefore(MockLocalDate.now().plusDays(1)))
                    .filter(task -> task.frequency() != Frequency.PENDING)
                    .collect(Collectors.toList())); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
        activityModel.getLocalDate().observe(date ->{
            timeNow = date;
            DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
            String StringOfNewNowDate = timeNow.format(myFormatObj2).toString();
            String StringOfNewTmrwDate = timeNow.plusDays(1).format(myFormatObj2).toString();
            updateDropdown(StringOfNewNowDate, StringOfNewTmrwDate);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentTaskListBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.taskList.setAdapter(adapter);

        //Time functionality + mock
//        LocalDate myDateObj = MockLocalDate.now();
//        LocalDate myNextDateObj = myDateObj.plusDays(1);
        handler = new Handler(Looper.getMainLooper());
//
//
//        //DateRolloverMock mockTime = new DateRolloverMock(myDateObj);
//        lastTime = LocalDateTime.now();
//        lastDate = MockLocalDate.now();
//        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
//
//        String StringOfDate = myDateObj.format(myFormatObj);
//        String StringOfNextDate = myNextDateObj.format(myFormatObj);

        //this.DateDisplay = this.view.dateContent;
        //view.dateContent.setText(StringOfDate);


        //this is the button responsible for switching to the recurring task fragment
        ImageButton switchButton = view.switchtorecurringbutton;
        switchButton.setOnClickListener(
                v -> {
//                    RecurringTaskListFragment recur = new RecurringTaskListFragment();
                    PendingTaskListFragment recur = new PendingTaskListFragment();

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
        //viewTitleDropdown is a Spinner, viewTitleAdapter is the Spinner Adapter, spinnerItems is list of strings
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        viewTitleDropdown = view.viewTitle;
        spinnerItems = new ArrayList<String>(Arrays.asList( "Today, " + timeNow.format(myFormatObj), "Tomorrow, " + timeNow.plusDays(1).format(myFormatObj), "Recurring", "Pending"));
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
                        //loadFragment(new TaskListFragment());
                        Log.d("spin", "spun to 0");
                        break;

                    case 1:
                        TomorrowTaskListFragment tmrw = new TomorrowTaskListFragment();
                        loadFragment(tmrw);
                        Log.d("spin", "spun to 1");
                        break;
                    case 2:
                        loadFragment(new RecurringTaskListFragment());
                        Log.d("spin", "spun to 2");
                        break;
                    case 3:
                        loadFragment(new PendingTaskListFragment());
                        Log.d("spin", "spun to 3");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

//        This is the runner that checks the time every second
        Runnable updateTimeRunnable = new Runnable() {

            @Override
            public void run() {
//                updateCurrentDate();
                handler.postDelayed(this, 1000);
            }



        };
        handler.post(updateTimeRunnable);

        view.mockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling updateTime with mocked = true just moves date forward
//                MockLocalDate.advanceDate();
//                updateCurrentDate();
                //Call deleteCompletedTasks from the taskDao
//                activityModel.deleteCompletedTasks();
                activityModel.timeTravelForward();
            }
        });



        return view.getRoot();
    }

    // TODO: keeping updateDropdown in this function crashes the app. need fix.
    //dynamically updating spinner time using this StackOverflow post: https://stackoverflow.com/questions/3283337/how-to-update-a-spinner-dynamically
//    private void updateCurrentDate() {
////        if(LocalDate.now().isAfter(timeNow)){
////            MockLocalDate.setDate(LocalDate.now());
////        }
//        LocalDate dateNow = MockLocalDate.now();
//        LocalTime tNow = LocalTime.now();
//        if (dateNow.isAfter(lastDate) && tNow.isAfter(LocalTime.of(2, 0))) {
//            lastDate = dateNow;
//            DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
//            activityModel.deleteCompletedTasks();
//
//            //declaring strings to put into the spinner dropdown
//            //String StringOfNewNowDate = lastTime.format(myFormatObj2).toString();
//            //String StringOfNewTmrwDate = lastTime.plusDays(1).format(myFormatObj2).toString();
//            String StringOfNewNowDate = lastDate.format(myFormatObj2).toString();
//            String StringOfNewTmrwDate = lastDate.plusDays(1).format(myFormatObj2).toString();
//
//            //DateDisplay.setText(StringOfNewNowDate);
//
////            updateDropdown(StringOfNewNowDate, StringOfNewTmrwDate);
//
//
//
//        }
//    }

    private void updateCurrentTime() {
        LocalDateTime timeNow = LocalDateTime.now();
        lastTime = lastTime.plusDays(1);
        DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        String StringOfNewNowDate = lastTime.format(myFormatObj2).toString();
        String StringOfNewTmrwDate = lastTime.plusDays(1).format(myFormatObj2).toString();


        updateDropdown(StringOfNewNowDate, StringOfNewTmrwDate);



    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void updateDropdown(String newDate, String newTmrwDate) {
        if(viewTitleAdapter == null) return;
        //updating dates on dropdown spinner item viewTitleDropdown
        spinnerItems = new ArrayList<String>(Arrays.asList(
                "Today, " + newDate,
                "Tomorrow, " + newTmrwDate,
                "Recurring",
                "Pending"
        ));
        //viewTitleAdapter.notifyDataSetChanged();
        Log.d("not made title adapter","not made title adapter");
        //viewTitleAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        viewTitleAdapter.clear();
        Log.d("made title adapter2","made title adapter");
        viewTitleAdapter.addAll(spinnerItems);
        Log.d("made title adapter","made title adapter");
        viewTitleDropdown.setAdapter(viewTitleAdapter);
    }


    private void createDropdown(){

    }
}
