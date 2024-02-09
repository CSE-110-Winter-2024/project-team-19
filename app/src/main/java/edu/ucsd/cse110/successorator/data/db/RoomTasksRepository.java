package edu.ucsd.cse110.successorator.data.db;

import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Task;
import edu.ucsd.cse110.successorator.lib.domain.TaskRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;

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
        tasksDao.insert(TaskEntity.fromTask(task));
    }

    @Override
    public void append(Task task){
        tasksDao.append(TaskEntity.fromTask(task));
    }

    @Override
    public void prepend(Task task){
        tasksDao.prepend(TaskEntity.fromTask(task));
    }

    @Override
    public void remove(int id){
        tasksDao.delete(id);
    }
}

