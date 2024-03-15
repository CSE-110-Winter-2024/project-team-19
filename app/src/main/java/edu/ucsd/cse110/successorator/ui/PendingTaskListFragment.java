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
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentPendingTasksBinding;
import edu.ucsd.cse110.successorator.lib.domain.Context;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Task;

public class PendingTaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentPendingTasksBinding view;
    private PendingTaskListAdapter adapter;

    private LocalDate timeNow;

    //initializing Spinner variables
    Spinner viewTitleDropdown;
    ArrayAdapter<String> viewTitleAdapter;
    ArrayList<String> spinnerItems;

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

        this.timeNow = activityModel.getLocalDate().getValue();

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
        this.view = FragmentPendingTasksBinding.inflate(inflater, container, false);

        view.taskList.setAdapter(adapter);

        // When a task is long-pressed, open a menu with the option to remove it, complete it,
        // move it to today, or move it to tomorrow.

        //this allows all recurring tasks to have menus when long-pressed
        registerForContextMenu(view.taskList);

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
            var dialogFragment = PendingFormFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "RecurringForm");

        });

        /*
         * the code below was taken from ChatGPT; we used it as a reference
         * to create our spinner object
         *
         * link to the conversation: https://chat.openai.com/share/8e03a47b-1015-4363-9607-d591ca83188c
         */
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        viewTitleDropdown = view.viewTitle;
        spinnerItems = new ArrayList<String>(Arrays.asList("Pending", "Recurring", "Today, "
                + timeNow.format(myFormatObj), "Tomorrow, "
                + timeNow.plusDays(1).format(myFormatObj)));
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
                        //loadFragment(new PendingTaskListFragment());
                        break;
                    case 1:
                        loadFragment(new RecurringTaskListFragment());
                        break;
                    case 2:
                        loadFragment(new TaskListFragment());
                        break;
                    case 3:
                        loadFragment(new TomorrowTaskListFragment());
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

        // This section checks the itemId that was selected and determines which button was pressed
        // either delete, finish, move to today, or move to tommorow.
        if (itemId == R.id.pending_delete) {
            // If delete, remove the task immediately from the database.
            Task toDelete = adapter.getItem(position);
            activityModel.removeTask(toDelete);
            adapter.notifyDataSetChanged();
            return true;
        }else if (itemId == R.id.pending_finish) {
            // If finish, complete the task and add it with a ONE_TIME frequency and an active date
            // of today.
            Task toComplete = adapter.getItem(position);
            activityModel.completeTask(toComplete.withFrequency(Frequency.ONE_TIME)
                    .withActiveDate(timeNow));
            adapter.notifyDataSetChanged();
            return true;
        }else if (itemId == R.id.pending_move_today) {
            // If move to today, add the task with a ONE_TIME frequency and active date of today.
            Task toInsert = adapter.getItem(position);
            activityModel.removeTask(toInsert);
            activityModel.insertNewTask(toInsert.withFrequency(Frequency.ONE_TIME)
                    .withActiveDate(timeNow));
            adapter.notifyDataSetChanged();
            return true;
        }else if (itemId == R.id.pending_move_tomorrow){
            // If move to tomorrow, add the task with a ONE_TIME frequency and a active date of tomorrow.
            Task toInsert = adapter.getItem(position);
            activityModel.removeTask(toInsert);
            activityModel.insertNewTask(toInsert.withFrequency(Frequency.ONE_TIME)
                    .withActiveDate(timeNow.plusDays(1)));
            adapter.notifyDataSetChanged();
            return true;
        }else{
            return super.onContextItemSelected(item);
        }
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
        //viewTitleAdapter.notifyDataSetChanged();
        viewTitleAdapter.clear();
        viewTitleAdapter.addAll(spinnerItems);
        viewTitleDropdown.setAdapter(viewTitleAdapter);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}