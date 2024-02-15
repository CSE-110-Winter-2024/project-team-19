package edu.ucsd.cse110.successorator.ui;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import edu.ucsd.cse110.successorator.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Task;

public class TaskListAdapter extends ArrayAdapter<Task> {
    Consumer<Task> onDeleteClick;

    private ArrayList<Task> completedTasks = new ArrayList<Task>();

    public TaskListAdapter(Context context, List<Task> flashcards, Consumer<Task> onDeleteClick) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(flashcards));
        this.onDeleteClick = onDeleteClick;
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

        binding.taskDash.setOnClickListener(v -> {
            binding.taskDash.setText("☑");
            var id = task.id();
            assert id != null;
            onDeleteClick.accept(task);
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
