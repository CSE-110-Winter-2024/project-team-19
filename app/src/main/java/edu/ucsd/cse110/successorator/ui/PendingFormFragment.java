package edu.ucsd.cse110.successorator.ui;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.util.MockLocalDate;

/*
This class was adapted from the CardListFragment provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class PendingFormFragment extends DialogFragment {

    public PendingFormFragment() {

    }

    public static PendingFormFragment newInstance() {
        var fragment = new PendingFormFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_pending_task_form, container, false);

        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        var activityModel = modelProvider.get(MainViewModel.class);


        Button btnClose = view.findViewById(R.id.pendingCancelButton);
        btnClose.setOnClickListener(v -> {
            dismiss(); // Close the popup when the close button is clicked
        });

        Button btnSubmit = view.findViewById(R.id.pendingSubmitButton);


        btnSubmit.setOnClickListener(v -> {
            EditText taskText = view.findViewById(R.id.task_text);
            String taskTextString = taskText.getText().toString();
            Task toInsert = new Task(null, taskTextString, 2, false,
                    MockLocalDate.now(), Frequency.PENDING,
                    MockLocalDate.now().getDayOfWeek(), 1);
            activityModel.insertNewTask(toInsert);
            dismiss();
        });


        return view;

    }
}





