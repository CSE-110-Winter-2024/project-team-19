package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentTaskListBinding;
import edu.ucsd.cse110.successorator.ui.TaskFormFragment;
import edu.ucsd.cse110.successorator.ui.TaskListFragment;
import edu.ucsd.cse110.successorator.util.DateRolloverMock;

import android.view.View;
import android.widget.ListView;

import androidx.fragment.app.FragmentManager;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, TaskListFragment.newInstance())
                .commit();










    }
}
