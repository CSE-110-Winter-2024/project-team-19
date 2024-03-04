package edu.ucsd.cse110.successorator.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentRecurringTasksBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Task;

public class RecurringTaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentRecurringTasksBinding view;

    private TaskListAdapter adapter;

    private boolean deleteFlag = false;

    private SharedPreferences sharedPreferences;


    public RecurringTaskListFragment() {
        // Required empty public constructor
    }

    public static RecurringTaskListFragment newInstance() {
        RecurringTaskListFragment fragment = new RecurringTaskListFragment();
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
                Log.d("Debug", "Fragment called insertNewTask");
                var id = task.id();
                assert id != null;
                activityModel.removeTask(id);
                activityModel.insertNewTask(task.withComplete(false));
            } else {
                Log.d("Debug", "Fragment called completeTask");
                activityModel.completeTask(task);
            }
            adapter.notifyDataSetChanged();
        });
        activityModel.getOrderedTasks().observe(tasks -> {
            if (tasks == null) return;
            adapter.clear();
            List<Task> recurringTasks = new ArrayList<>();
            for (Task i : tasks) {
                if (!i.frequency().equals(Frequency.ONE_TIME)) {
                    recurringTasks.add(i);
                }
            }
            adapter.addAll(new ArrayList<>(recurringTasks));
            adapter.notifyDataSetChanged();
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentRecurringTasksBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.taskList.setAdapter(adapter);

        //no time functionality for this view


        view.addTaskButton.setOnClickListener(v -> {


            var dialogFragment = RecurringFormFragment.newInstance();
           dialogFragment.show(getParentFragmentManager(), "RecurringForm");

        });

        return view.getRoot();
    }


}
