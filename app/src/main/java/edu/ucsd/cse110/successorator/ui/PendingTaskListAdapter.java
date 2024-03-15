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

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.ListItemNotTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Task;

public class PendingTaskListAdapter extends ArrayAdapter<Task> {
    public PendingTaskListAdapter(Context context, List<Task> tasks) {
        //the difference between this and the normal adapter is that there
        //is an onlickListener for long presses that allows you to delete

        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(tasks));
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

        binding.getRoot().setOnLongClickListener(v -> {
            // Show the context menu for long-press action
            v.showContextMenu();
            return true;
        });

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
        return binding.getRoot();
    }
}
