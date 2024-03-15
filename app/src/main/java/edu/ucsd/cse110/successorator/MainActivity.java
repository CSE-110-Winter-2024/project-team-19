package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.lib.domain.MockLocalDate;
import edu.ucsd.cse110.successorator.ui.TaskListFragment;

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
