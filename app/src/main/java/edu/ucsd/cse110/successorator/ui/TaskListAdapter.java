package edu.ucsd.cse110.successorator.ui;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import edu.ucsd.cse110.successorator.databinding.ListItemTaskBinding;
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
//        Log.d("ugh", "task:" + task.taskName() + " complete: " + task.complete());
        if (isTaskCompleted) {
            binding.taskContent.setPaintFlags(binding.taskContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.taskDash.setText("+");
            binding.taskDash.setTextColor(Color.LTGRAY);
        } else {
            binding.taskContent.setPaintFlags(binding.taskContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            binding.taskDash.setText("−");
            binding.taskDash.setTextColor(Color.RED);
        }


        binding.taskDash.setOnClickListener(v -> {
            onCompleteClick.accept(task);
        });

        return binding.getRoot();
    }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var flashcard = getItem(position);
        assert flashcard != null;

        var id = flashcard.id();
        assert id != null;

        return id;
    }
}
