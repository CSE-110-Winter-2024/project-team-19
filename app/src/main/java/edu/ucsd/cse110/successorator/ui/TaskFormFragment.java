package edu.ucsd.cse110.successorator.ui;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.Task;

public class TaskFormFragment extends DialogFragment {

    private MainViewModel activityModel;
    private FragmentTaskListBinding view;

    public TaskFormFragment() {

    }

    public static TaskFormFragment newInstance() {
        var fragment = new TaskFormFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task_form, container, false);

        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        var activityModel = modelProvider.get(MainViewModel.class);

        Button btnClose = view.findViewById(R.id.close_button);
        btnClose.setOnClickListener(v -> {
            dismiss(); // Close the popup when the close button is clicked
        });

        Button btnSubmit = view.findViewById(R.id.submit_button);

        btnSubmit.setOnClickListener(v -> {
            EditText taskText = view.findViewById(R.id.task_text);
            String taskTextString = taskText.getText().toString();
            activityModel.insertNewTask(new Task(null, taskTextString, -1));
            dismiss();
        });

        return view;

    }

}





