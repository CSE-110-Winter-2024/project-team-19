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

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.PopupMenu;
import android.widget.TextView;

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
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;

import edu.ucsd.cse110.successorator.lib.domain.Context;
import edu.ucsd.cse110.successorator.lib.domain.Task;

import edu.ucsd.cse110.successorator.lib.domain.Frequency;



/*
This class was adapted from the CardListFragment class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class TaskListFragment extends Fragment {

    private static final String PREF_TASK_DATE = "task_date";
    private static final String PREF_TASK_TIME = "task_time";
    //for when there are no tasks in the view
    private static final String DEFAULT_TEXT = "No goals for the Day.  Click the + at the upper right to enter your Most Important Thing.";
    private MainViewModel activityModel;
    private FragmentTaskListBinding view;
    private TextView defaultTextDisplay;

    //TIME VARIABLES
    private LocalDate timeNow;
    private TaskListAdapter adapter;

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

        this.timeNow = activityModel.getLocalDate().getValue();

        // Initialize the Adapter (with an empty list for now)
        this.adapter = new TaskListAdapter(requireContext(), List.of(), task -> {
            if (task.complete()) {
                activityModel.uncompleteTask(task);
            } else {
                activityModel.completeTask(task);
            }
            adapter.notifyDataSetChanged();
        });
        activityModel.getOrderedTasks().observe(tasks -> {
            if (tasks == null) return;
            adapter.clear();
            adapter.addAll(new ArrayList<>(tasks).stream()
                    .filter(task -> task.activeDate().isBefore(timeNow.plusDays(1)))
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

        ImageButton hamburgerButton = view.hamburgerButton;

        defaultTextDisplay = view.defaultText;

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
            var dialogFragment = TaskFormFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "DatePicker");
        });

        // Prepping dropdown
        /*
         * the code below was taken from ChatGPT; we used it as a reference
         * to create our spinner object
         *
         * link to the conversation: https://chat.openai.com/share/8e03a47b-1015-4363-9607-d591ca83188c
         */
        //viewTitleDropdown is a Spinner, viewTitleAdapter is the Spinner Adapter, spinnerItems is list of strings
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        viewTitleDropdown = view.viewTitle;
        spinnerItems = new ArrayList<String>(Arrays.asList( "Today, "
                + timeNow.format(myFormatObj), "Tomorrow, "
                + timeNow.plusDays(1).format(myFormatObj), "Recurring", "Pending"));
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
                        //loadFragment(new TaskListFragment());
                        break;
                    case 1:
                        TomorrowTaskListFragment tmrw = new TomorrowTaskListFragment();
                        loadFragment(tmrw);
                        break;
                    case 2:
                        loadFragment(new RecurringTaskListFragment());
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

        activityModel.getOrderedTasks().observe(tasks -> {
                if(tasks == null) return;
                updateDefaultText();
            });

        return view.getRoot();
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
                "Today, " + newDate,
                "Tomorrow, " + newTmrwDate,
                "Recurring",
                "Pending"
        ));
        viewTitleAdapter.clear();
        viewTitleAdapter.addAll(spinnerItems);
        viewTitleDropdown.setAdapter(viewTitleAdapter);
    }

    public void updateDefaultText() {
        //check if there are no tasks available. if so, set default text. otherwise, set to empty
        this.defaultTextDisplay = this.view.defaultText;

        if(activityModel.getOrderedTasks().getValue() == null) {
            defaultTextDisplay.setText(DEFAULT_TEXT);
        }
        else if(activityModel.getOrderedTasks().getValue() != null && activityModel.getOrderedTasks().getValue().size()== 0) {
            defaultTextDisplay.setText(DEFAULT_TEXT);
        }
        else {
            defaultTextDisplay.setText("");
        }
    }



}
