package edu.ucsd.cse110.successorator;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.TaskRepository;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.ui.TaskFormFragment;

public class MainViewModel extends ViewModel {
    private final TaskRepository taskRepository;
    private final MutableSubject<List<Task>> orderedTasks;


    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getTaskRepository());
                    }
            );

    public MainViewModel(TaskRepository taskRepository){
        this.taskRepository = taskRepository;

        this.orderedTasks = new SimpleSubject<>();

        taskRepository.findAll().observe(tasks -> {
            if (tasks == null) return; // not ready yet, ignore

            var newOrderedTasks = tasks.stream()
                    .sorted(Comparator.comparingInt(Task::sortOrder))
                    .collect(Collectors.toList());

            orderedTasks.setValue(newOrderedTasks);
        });


    }

    public void insertNewTask(Task task){
        taskRepository.insert(task);
    }

    public Subject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }
}
