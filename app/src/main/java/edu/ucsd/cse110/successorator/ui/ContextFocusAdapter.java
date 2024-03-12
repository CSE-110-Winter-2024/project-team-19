package edu.ucsd.cse110.successorator.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.databinding.ListItemNotTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Task;

public class ContextFocusAdapter extends ArrayAdapter<Task> {
    Consumer<Task> onCompleteClick;


    private SharedPreferences sharedPreferences;


    private TextView taskText;

    public ContextFocusAdapter(Context context, List<Task> tasks, Consumer<Task> onDeleteClick) {
        //the difference between this and the normal adapter is that there
        //is an onlickListener for long presses that allows you to delete

        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(tasks));
        this.onCompleteClick = onDeleteClick;
        sharedPreferences = context.getSharedPreferences("task_prefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the task for this position.
        var task = getItem(position);
        assert task != null;

        // Check if a view is being reused...
        ListItemNotTaskBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListItemNotTaskBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemNotTaskBinding.inflate(layoutInflater, parent, false);
        }


        // Populate the view with the flashcard's data.
        binding.taskContent.setText(task.taskName());

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

