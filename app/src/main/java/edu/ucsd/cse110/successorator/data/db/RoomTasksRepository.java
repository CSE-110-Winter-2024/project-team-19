package edu.ucsd.cse110.successorator.data.db;

import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.TaskRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;

/*
This class was adapted from the RoomFlashcardsRepository class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class RoomTasksRepository implements TaskRepository {
    private final TasksDao tasksDao;

    public RoomTasksRepository(TasksDao tasksDao){
        this.tasksDao = tasksDao;
    }

    @Override
    public Subject<Task> find(int id){
        var entitiesLiveData = tasksDao.findAsLiveData(id);
        var taskLiveData = Transformations.map(entitiesLiveData, TaskEntity::toTask);
        return new LiveDataSubjectAdapter<>(taskLiveData);
    }

    @Override
    public Subject<List<Task>> findAll(){
        var entitiesLiveData = tasksDao.findAllAsLiveData();
        var tasksLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(TaskEntity::toTask)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(tasksLiveData);
    }

    @Override
    public void save(Task task){
        tasksDao.insertNewTask(TaskEntity.fromTask(task));
    }

    public void save(List<Task> flashcards) {
        var entities = flashcards.stream()
                .map(TaskEntity::fromTask)
                .collect(Collectors.toList());
        tasksDao.insert(entities);
    }

    @Override
    public void complete(Task task){
        tasksDao.completeTask(TaskEntity.fromTask(task));
    }

    @Override
    public void remove(int id) {
        tasksDao.delete(id);
    }
    @Override
    public void deleteCompletedTasks(){
            tasksDao.deleteCompletedTasks();
    }
}

