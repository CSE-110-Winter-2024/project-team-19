package edu.ucsd.cse110.successorator.ui;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

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
import edu.ucsd.cse110.successorator.databinding.FragmentPendingTasksBinding;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.util.MockLocalDate;

public class PendingTaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentPendingTasksBinding view;
    private PendingTaskListAdapter adapter;


    public PendingTaskListFragment() {
        // Required empty public constructor
    }

    public static PendingTaskListFragment newInstance() {
        PendingTaskListFragment fragment = new PendingTaskListFragment();
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
        this.adapter = new PendingTaskListAdapter(requireContext(), List.of());
        activityModel.getOrderedTasks().observe(tasks -> {
            if (tasks == null) return;
            adapter.clear();
            List<Task> pendingTasks = new ArrayList<Task>();
            for (Task i: tasks)
            {
                if (i.frequency().equals(Frequency.PENDING)) {
                    pendingTasks.add(i);
                }
            }
            adapter.addAll(new ArrayList<>(pendingTasks)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentPendingTasksBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.taskList.setAdapter(adapter);

        //no time functionality for this view yet, id like to move to main activity

        //When a task is long-pressed, open a menu with the option to remove it.
        //if they press remove at that point, it's deleted from the database.

        //this allows all recurring tasks to have menus when long-pressed
        registerForContextMenu(view.taskList);

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

        view.addTaskButton.setOnClickListener(v -> {
            var dialogFragment = PendingFormFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "RecurringForm");

        });

        return view.getRoot();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.task_list) {
            //menu.setHeaderTitle("Options");
            menu.add(Menu.NONE, R.id.pending_move_today, Menu.NONE, "Move to Today");
            menu.add(Menu.NONE, R.id.pending_move_tomorrow, Menu.NONE, "Move to Tomorrow");
            menu.add(Menu.NONE, R.id.pending_finish, Menu.NONE, "Finish");
            menu.add(Menu.NONE, R.id.pending_delete, Menu.NONE, "Delete");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;

        var itemId = item.getItemId();

        if (itemId == R.id.pending_delete) {
            Task toDelete = adapter.getItem(position);
            activityModel.removeTask(toDelete);
            adapter.notifyDataSetChanged();
            return true;
        }else if (itemId == R.id.pending_finish) {
            Task toComplete = adapter.getItem(position);
            activityModel.completeTask(toComplete.withFrequency(Frequency.ONE_TIME)
                    .withActiveDate(MockLocalDate.now()));
            adapter.notifyDataSetChanged();
            return true;
        }else if (itemId == R.id.pending_move_today) {
            Task toInsert = adapter.getItem(position);
            activityModel.removeTask(toInsert);
            activityModel.insertNewTask(toInsert.withFrequency(Frequency.ONE_TIME)
                    .withActiveDate(MockLocalDate.now()));
            adapter.notifyDataSetChanged();
            return true;
        }else if (itemId == R.id.pending_move_tomorrow){
            Task toInsert = adapter.getItem(position);
            activityModel.removeTask(toInsert);
            activityModel.insertNewTask(toInsert.withFrequency(Frequency.ONE_TIME)
                    .withActiveDate(MockLocalDate.now().plusDays(1)));
            adapter.notifyDataSetChanged();
            return true;
        }else{
            return super.onContextItemSelected(item);
        }
    }
}