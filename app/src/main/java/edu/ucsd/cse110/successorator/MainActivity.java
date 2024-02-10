package edu.ucsd.cse110.successorator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.ui.TaskFormFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var view = ActivityMainBinding.inflate(getLayoutInflater(), null, false);
        view.placeholderText.setText(R.string.hello_world);

        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        view.dateText.setText(myDateObj.format(myFormatObj));

        setContentView(view.getRoot());

        ImageButton addTaskButton = findViewById(R.id.add_task_button);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Change button color on click (for simple testing)
                //addTaskButton.setBackgroundColor(Color.RED); // You can set any color you like

                FragmentManager fragmentManager = getSupportFragmentManager();
                TaskFormFragment taskFormFragment = new TaskFormFragment();
                taskFormFragment.show(fragmentManager,"TaskFormFragment");
            }
        });

    }
}
