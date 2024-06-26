/*
 * We used the following sources when writing this class (more specifics are listed in the code
 * areas where we actually used these citations):
 *
 * https://chat.openai.com/share/8e03a47b-1015-4363-9607-d591ca83188c
 * https://chat.openai.com/share/e6d2f830-6364-4ad2-9303-96f523479849
 * https://stackoverflow.com/questions/3283337/how-to-update-a-spinner-dynamically
 */

package edu.ucsd.cse110.successorator.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentRecurringTasksBinding;
import edu.ucsd.cse110.successorator.lib.domain.Context;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.MockLocalDate;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.TaskBuilder;

public class RecurringTaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentRecurringTasksBinding view;
    private RecurringTaskListAdapter adapter;

    private LocalDate timeNow;

    //initializing Spinner variables
    Spinner viewTitleDropdown;
    ArrayAdapter<String> viewTitleAdapter;
    ArrayList<String> spinnerItems;


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

        this.timeNow = activityModel.getLocalDate().getValue();

        // Initialize the Adapter (with an empty list for now)
        this.adapter = new RecurringTaskListAdapter(requireContext(), List.of(), task -> {
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
            List<Task> orderedRecurringTasks = recurringTasks;
            Collections.sort(orderedRecurringTasks, Comparator.comparing(Task::creationDate));

            //do i have to manipulate sortOrder too hmmm

            adapter.addAll(new ArrayList<>(orderedRecurringTasks)); // remember the mutable copy here!
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
        this.view = FragmentRecurringTasksBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.taskList.setAdapter(adapter);

        //no time functionality for this view yet, id like to move to main activity

        //When a task is long-pressed, open a menu with the option to remove it.
        //if they press remove at that point, it's deleted from the database.

        //this allows all recurring tasks to have menus when long-pressed
        registerForContextMenu(view.taskList);
        ImageButton hamburgerButton = view.hamburgerButton;
        hamburgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(requireContext(), hamburgerButton);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked

                        Context focusMode;

                        Bundle args = new Bundle();
                        String selectedItem = menuItem.getTitle().toString();
                        if(selectedItem.equals("Home")) {
                            focusMode = Context.HOME;
                            hamburgerButton.setBackgroundTintList(getContext().getColorStateList(R.color.context_yellow));
                        }
                        else if(selectedItem.equals("Work")) {
                            focusMode = Context.WORK;
                            hamburgerButton.setBackgroundTintList(getContext().getColorStateList(R.color.context_blue));
                        }
                        else if(selectedItem.equals("School")) {
                            focusMode = Context.SCHOOL;
                            hamburgerButton.setBackgroundTintList(getContext().getColorStateList(R.color.context_pink));
                        }
                        else if(selectedItem.equals("Errand")) {
                            focusMode = Context.ERRAND;
                            hamburgerButton.setBackgroundTintList(getContext().getColorStateList(R.color.context_green));
                        }
                        else {
                            focusMode = Context.NONE;
                            hamburgerButton.setBackgroundTintList(getContext().getColorStateList(R.color.context_transparent));
                        }


                        activityModel.getOrderedTasks().observe(tasks -> {
                            if (tasks == null) return;
                            adapter.clear();
                            List<Task> focusTasks = new ArrayList<Task>();
                            if (focusMode == Context.NONE) {
                                focusTasks.addAll(tasks);
                            }
                            for (Task i: tasks)
                            {
                                if (i.context() == focusMode) {
                                    focusTasks.add(i);
                                }
                            }
                            adapter.addAll(new ArrayList<>(focusTasks)); // remember the mutable copy here!
                            adapter.notifyDataSetChanged();
                        });
                        return true;
                    }

                });
                // Showing the popup menu
                popupMenu.show();
            }
        });

        view.addTaskButton.setOnClickListener(v -> {
            var dialogFragment = RecurringFormFragment.newInstance();
           dialogFragment.show(getParentFragmentManager(), "RecurringForm");

        });

        // Preparing dropdown
        /*
         * the code below was taken from ChatGPT; we used it as a reference
         * to create our spinner object
         *
         * link to the conversation: https://chat.openai.com/share/8e03a47b-1015-4363-9607-d591ca83188c
         */
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        viewTitleDropdown = view.viewTitle;
        spinnerItems = new ArrayList<String>(Arrays.asList("Recurring", "Today, "
                + timeNow.format(myFormatObj), "Tomorrow, "
                + timeNow.plusDays(1).format(myFormatObj), "Pending"));
        viewTitleAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        viewTitleDropdown.setAdapter(viewTitleAdapter);

        /*
         * adding cases to tell the spinner what to do when switching to Today (TaskListFragment),
         * Tomorrow (TmrwTaskListFragment), Recurring (RecurringTaskListFragment), and
         * Pending (PendingTaskListFragment)
         *
         * the code below was taken from ChatGPT; we used it as a reference
         * to let our spinner switch between different cases
         *
         * link to the conversation: https://chat.openai.com/share/e6d2f830-6364-4ad2-9303-96f523479849
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
                        loadFragment(new TomorrowTaskListFragment());
                        break;
                    case 3:
                        loadFragment(new PendingTaskListFragment());
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        view.mockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityModel.timeTravelForward();
            }
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
            //deletion logic here
            Task tobeDeleted = adapter.getItem(position);
            activityModel.removeTask(tobeDeleted);
            if(tobeDeleted.activeDate().isBefore(MockLocalDate.now().plusDays(1))){
                activityModel.insertNewTask(new TaskBuilder()
                        .withTaskName(tobeDeleted.taskName())
                        .withFrequency(Frequency.ONE_TIME)
                        .build());
            }
            adapter.notifyDataSetChanged();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /*
     * the code below was adapted from a StackOverflow post; we used it as a
     * reference to dynamically update the text in our spinner object
     *
     * link to the post: https://stackoverflow.com/questions/3283337/how-to-update-a-spinner-dynamically
     */
    private void updateDropdown(String newDate, String newTmrwDate) {
        if(viewTitleAdapter == null) return;
        //updating dates on dropdown spinner item viewTitleDropdown
        spinnerItems = new ArrayList<String>(Arrays.asList(
                "Recurring",
                "Today, " + newDate,
                "Tomorrow, " + newTmrwDate,
                "Pending"
        ));
        viewTitleAdapter.clear();
        viewTitleAdapter.addAll(spinnerItems);
        viewTitleDropdown.setAdapter(viewTitleAdapter);
    }

}
