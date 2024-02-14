package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.ui.TaskFormFragment;

import android.view.View;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding view;
    String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_task_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.fragment_task_list, mobileArray);

        ListView listView = (ListView) findViewById(R.id.task_list);
        listView.setAdapter(adapter);
        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        // TODO: Uncomment below (need it to test that main works)
        //view.placeholderText.setText(R.string.hello_world);

        LocalDateTime myDateObj = LocalDateTime.now();
        LocalTime rolloverTime = LocalTime.of(14, 0);
        if (myDateObj.toLocalTime().isBefore(rolloverTime)) {
            myDateObj = myDateObj.with(rolloverTime);
        } else {
            myDateObj = myDateObj.plusDays(1).with(rolloverTime);
        }
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        // TODO: Uncomment below
        //view.dateText.setText(myDateObj.format(myFormatObj));

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