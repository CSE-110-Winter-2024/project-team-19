package edu.ucsd.cse110.successorator.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Looper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


    private TextView DateDisplay;
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
                    .filter(task -> task.activeDate().isBefore(MockLocalDate.now().plusDays(1)))
                    .filter(task -> task.frequency() != Frequency.PENDING)
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
        LocalDate myDateObj = MockLocalDate.now();
        handler = new Handler(Looper.getMainLooper());


//        DateRolloverMock mockTime = new DateRolloverMock(myDateObj);
        lastTime = LocalDateTime.now();
        lastDate = MockLocalDate.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        String StringOfDate = myDateObj.format(myFormatObj);

        this.DateDisplay = this.view.dateContent;
        view.dateContent.setText(StringOfDate);


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
                MockLocalDate.advanceDate();
                updateCurrentDate();
                //Call deleteCompletedTasks from the taskDao
//                activityModel.deleteCompletedTasks();
            }
        });
        return view.getRoot();
    }


    private void updateCurrentDate() {
        if(LocalDate.now().isAfter(MockLocalDate.now())){
            MockLocalDate.setDate(LocalDate.now());
        }
        LocalDate dateNow = MockLocalDate.now();
        LocalTime tNow = LocalTime.now();
        if (dateNow.isAfter(lastDate) && tNow.isAfter(LocalTime.of(2, 0))) {
            lastDate = dateNow;
            DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
            String StringOfDate2 = dateNow.format(myFormatObj2);
            DateDisplay.setText(StringOfDate2);
            activityModel.deleteCompletedTasks();
        }
    }
}
