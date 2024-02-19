package edu.ucsd.cse110.successorator.ui;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.data.db.TaskEntity;
import edu.ucsd.cse110.successorator.data.db.TasksDao;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.util.DateRolloverMock;

public class TaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentTaskListBinding view;

    private TextView DateDisplay;
    private Handler handler;

    private LocalDateTime lastDate;
    private ListItemTaskBinding taskItem;
    private TaskListAdapter adapter;

    private TasksDao tasksDao;

    private TaskEntity taskEntity;

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
            activityModel.completeTask(task);
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
        handler = new Handler(Looper.getMainLooper());


        DateRolloverMock mockTime = new DateRolloverMock(myDateObj);
        lastDate = LocalDateTime.now();

        //To start the mock uncomment line below
        myDateObj = mockTime.getFakeTime();




        LocalTime rolloverTime = LocalTime.of(14, 0);
        if (myDateObj.toLocalTime().isBefore(rolloverTime)) {
            myDateObj = myDateObj.with(rolloverTime);
        } else {
            myDateObj = myDateObj.plusDays(1).with(rolloverTime);
        }
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        String StringOfDate = myDateObj.format(myFormatObj).toString();

        this.DateDisplay = this.view.dateContent;
        view.dateContent.setText(StringOfDate);



        view.addTaskButton.setOnClickListener(v -> {
            var dialogFragment = TaskFormFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "CreateCardDialogFragment");
        });


        //This is the runner that checks the time every second
        Runnable updateTimeRunnable = new Runnable() {

            @Override
            public void run() {
                updateCurrentTime(false);
                handler.postDelayed(this, 1000);
            }



        };
        handler.post(updateTimeRunnable);



        view.mockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling updateTime with mocked = true just moves date forward
                updateCurrentTime(true);}
        });



        return view.getRoot();
    }


    private void updateCurrentTime(boolean mocked) {
        LocalDateTime timeNow = LocalDateTime.now();

        //check if the timeNow is a day ahead of lastDate OR myDateObj is a day ahead
        if (timeNow.isAfter(lastDate) || mocked == true)
        {
            if (mocked == false){lastDate = timeNow;}
            else {lastDate = lastDate.plusDays(1);}


            DateTimeFormatter myFormatObj2 = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

            String StringOfDate2 = lastDate.format(myFormatObj2).toString();
            DateDisplay.setText(StringOfDate2);

            //here we need to call some method to remove all tasks that are completed

        }


}}
