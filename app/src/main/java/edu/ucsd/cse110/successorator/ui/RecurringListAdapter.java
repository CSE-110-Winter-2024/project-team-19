package edu.ucsd.cse110.successorator.ui;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
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
import edu.ucsd.cse110.successorator.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.successorator.lib.domain.Task;

/*
This class was adapted from the CardListAdapter class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class RecurringListAdapter extends ArrayAdapter<Task> {
    Consumer<Task> onCompleteClick;


    private SharedPreferences sharedPreferences;


    private TextView taskText;

    public RecurringListAdapter(Context context, List<Task> tasks, Consumer<Task> onDeleteClick) {
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



        binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Show the context menu for long-press action
                v.showContextMenu();
                return true;
            }
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
