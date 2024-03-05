package edu.ucsd.cse110.successorator.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentRecurringTasksBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Task;

public class RecurringTaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentRecurringTasksBinding view;

    private RecurringListAdapter adapter;

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
        this.adapter = new RecurringListAdapter(requireContext(), List.of(), task -> {
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
        });
        activityModel.getOrderedTasks().observe(tasks -> {
            if (tasks == null) return;
            adapter.clear();
            List<Task> recurringTasks = new ArrayList<Task>();
            for (Task i: tasks)
            {

                if (!i.frequency().equals(Frequency.ONE_TIME) && !i.frequency().equals(Frequency.PENDING)) {
                    recurringTasks.add(i);

                }
            }

            adapter.addAll(new ArrayList<>(recurringTasks)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentRecurringTasksBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.taskList.setAdapter(adapter);

        //no time functionality for this view yet, id like to move to main activity

        //When a task is long-pressed, open a menu with the option to remove it.
        //if they press remove at that point, it's deleted from the database.

        //this allows all recurring tasks to have menus when long-pressed
        registerForContextMenu(view.taskList);



        view.addTaskButton.setOnClickListener(v -> {


            var dialogFragment = RecurringFormFragment.newInstance();
           dialogFragment.show(getParentFragmentManager(), "RecurringForm");

        });

        return view.getRoot();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.task_list) {
            //menu.setHeaderTitle("Options");
            menu.add(Menu.NONE, R.id.menu_delete_recurring, Menu.NONE, "Delete");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        if (item.getItemId() == R.id.menu_delete_recurring) {
            // Log the deletion
            Log.d("when delete pressed", "Delete recurring task selected for item at position: " + position);
            //deletion logic here

            return true;
        } else {
            return super.onContextItemSelected(item);
        }

    }

}
