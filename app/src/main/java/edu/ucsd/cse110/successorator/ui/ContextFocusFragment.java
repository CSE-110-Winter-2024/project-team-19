package edu.ucsd.cse110.successorator.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentContextFocusBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentRecurringTasksBinding;
import edu.ucsd.cse110.successorator.lib.domain.Context;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Task;

public class ContextFocusFragment extends Fragment {
    private MainViewModel activityModel;

    private @NonNull FragmentContextFocusBinding view;

    private ContextFocusAdapter adapter;

    private SharedPreferences sharedPreferences;


    public ContextFocusFragment() {
        // Required empty public constructor
    }

    public static ContextFocusFragment newInstance() {
        ContextFocusFragment fragment = new ContextFocusFragment();
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
        this.adapter = new ContextFocusAdapter(requireContext(), List.of(), task -> {
            if (task.complete()) {
                Log.d("Debug", "Fragment called insertNewTask");
                var id = task.id();
                assert id != null;
                activityModel.removeTask(task);
                activityModel.insertNewTask(task.withComplete(false));
            } else {
                Log.d("Debug", "Fragment called completeTask");
                activityModel.completeTask(task);
            }
            adapter.notifyDataSetChanged();
        }


        );

        Bundle args = getArguments();
        Context focusMode = args.getSerializable("focusMode", Context.class);

        activityModel.getOrderedTasks().observe(tasks -> {
            if (tasks == null) return;
            adapter.clear();
            List<Task> focusTasks = new ArrayList<Task>();
            for (Task i: tasks)
            {
                if (i.context() == focusMode) {
                    focusTasks.add(i);
                }
            }
            adapter.addAll(new ArrayList<>(focusTasks)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentContextFocusBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.taskList.setAdapter(adapter);

        //no time functionality for this view yet, id like to move to main activity

        //When a task is long-pressed, open a menu with the option to remove it.
        //if they press remove at that point, it's deleted from the database.

        //this allows all recurring tasks to have menus when long-pressed
        registerForContextMenu(view.taskList);


        Bundle args = getArguments();
        Context focusMode = args.getSerializable("focusMode", Context.class);

        TextView title = (TextView) view.contextText;
        title.setText(focusMode.toString());

        ImageButton switchButton = view.switchtoregularbutton;
        switchButton.setOnClickListener(
                v -> {
                    TaskListFragment reg = new TaskListFragment();

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, reg);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
        );


        return view.getRoot();
    }
}
