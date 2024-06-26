package edu.ucsd.cse110.successorator;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.MockLocalDate;
import edu.ucsd.cse110.successorator.lib.domain.Context;
import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.TaskRepository;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

/*
This class was adapted from the MainViewModel class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class MainViewModel extends ViewModel {
    private final TaskRepository taskRepository;
    private final MutableSubject<List<Task>> orderedTasks;
    private final MutableSubject<LocalDate> dateSubject;

    private Handler handler;

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

        this.handler = new Handler(Looper.getMainLooper());

        Runnable updateTimeRunnable = new Runnable() {

            @Override
            public void run() {
                if(LocalDate.now().isAfter(MockLocalDate.now())){
                    updateDate(LocalDate.now());
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateTimeRunnable);

        taskRepository.findAll().observe(tasks -> {
            if (tasks == null) return; // not ready yet, ignore

            List<Task>homeTasks = tasks.stream()
                    .filter(task -> task.context() == Context.HOME).sorted(Comparator.comparingInt(Task::sortOrder))
                    .collect(Collectors.toList());
            List<Task>workTasks = tasks.stream()
                    .filter(task -> task.context() == Context.WORK).sorted(Comparator.comparingInt(Task::sortOrder))
                    .collect(Collectors.toList());
            List<Task>schoolTasks = tasks.stream()
                    .filter(task -> task.context() == Context.SCHOOL).sorted(Comparator.comparingInt(Task::sortOrder))
                    .collect(Collectors.toList());
            List<Task>errandTasks = tasks.stream()
                    .filter(task -> task.context() == Context.ERRAND).sorted(Comparator.comparingInt(Task::sortOrder))
                    .collect(Collectors.toList());


//            var newOrderedTasks = tasks.stream()
//                    .sorted(Comparator.comparingInt(Task::sortOrder))
//                    .collect(Collectors.toList());
            List<Task> newOrderedTasks= new ArrayList<>();
            newOrderedTasks.addAll(homeTasks);
            newOrderedTasks.addAll(workTasks);
            newOrderedTasks.addAll(schoolTasks);
            newOrderedTasks.addAll(errandTasks);

            orderedTasks.setValue(newOrderedTasks);
        });

        this.dateSubject = new SimpleSubject<>();
        dateSubject.setValue(MockLocalDate.now());

        dateSubject.observe(date ->{
            updateTasks();
        });
    }

    public void insertNewTask(Task task){

        taskRepository.save(task);
    }

    public void completeTask(Task task){
        taskRepository.complete(task);
    }

    public void uncompleteTask(Task task){
        taskRepository.uncomplete(task);
    }

    public void removeTask(Task task){
        taskRepository.remove(task);
    }

    public void updateTasks(){
        taskRepository.updateTasks();
    }

    public Subject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }

    public Subject<LocalDate> getLocalDate() {
        return dateSubject;
    }

    public void timeTravelForward(){
        MockLocalDate.advanceDate();
        updateDate(MockLocalDate.now());
    }

    public void updateDate(LocalDate date){
        dateSubject.setValue(date);
    }
}
