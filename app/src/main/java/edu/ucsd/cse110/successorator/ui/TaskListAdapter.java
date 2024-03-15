package edu.ucsd.cse110.successorator.ui;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.MockLocalDate;
import edu.ucsd.cse110.successorator.lib.domain.Task;

/*
This class was adapted from the CardListAdapter class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class TaskListAdapter extends ArrayAdapter<Task> {
    Consumer<Task> onCompleteClick;

    private SharedPreferences sharedPreferences;


    private TextView taskText;

    public TaskListAdapter(Context context, List<Task> flashcards, Consumer<Task> onDeleteClick) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(flashcards));
        this.onCompleteClick = onDeleteClick;
        sharedPreferences = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE);
    }

    public boolean isComplete(Task task) {
        return sharedPreferences.getBoolean("task_" + task.id(), false);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the task for this position.
        var task = getItem(position);
        assert task != null;

        // Check if a view is being reused...
        ListItemTaskBinding binding;
        FragmentTaskListBinding listBinding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListItemTaskBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemTaskBinding.inflate(layoutInflater, parent, false);
        }


        // Populate the view with the flashcard's data.
        binding.taskContent.setText(task.taskName());

        final boolean isTaskCompleted = task.complete();

        if (isTaskCompleted) {
            binding.taskContent.setPaintFlags(binding.taskContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.taskDash.setText("+");
            binding.taskDash.setTextColor(Color.LTGRAY);
        } else {
            binding.taskContent.setPaintFlags(binding.taskContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            binding.taskDash.setText("−");
            binding.taskDash.setTextColor(Color.RED);
        }

        edu.ucsd.cse110.successorator.lib.domain.Context taskContext = task.context();
        if (taskContext == edu.ucsd.cse110.successorator.lib.domain.Context.HOME) {
            binding.taskContext.setText("H");
            binding.taskContext.setBackgroundTintList(getContext().getColorStateList(R.color.context_yellow));
        }
        else if (taskContext == edu.ucsd.cse110.successorator.lib.domain.Context.WORK) {
            binding.taskContext.setText("W");
            binding.taskContext.setBackgroundTintList(getContext().getColorStateList(R.color.context_blue));
        }
        else if (taskContext == edu.ucsd.cse110.successorator.lib.domain.Context.SCHOOL) {
            binding.taskContext.setText("S");
            binding.taskContext.setBackgroundTintList(getContext().getColorStateList(R.color.context_pink));
        }
        else if (taskContext == edu.ucsd.cse110.successorator.lib.domain.Context.ERRAND) {
            binding.taskContext.setText("E");
            binding.taskContext.setBackgroundTintList(getContext().getColorStateList(R.color.context_green));
        } else {
            binding.taskContext.setText("");
            binding.taskContext.setBackgroundTintList(getContext().getColorStateList(R.color.context_transparent));
        }


        binding.taskDash.setOnClickListener(v -> {
            onCompleteClick.accept(task);
        });

        return binding.getRoot();
    }
}
