package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleTaskRepository implements TaskRepository{
    private final InMemoryDataSource dataSource;

    public SimpleTaskRepository(InMemoryDataSource dataSource){
        this.dataSource = dataSource;
    }

    @Override
    public Subject<Task> find(int id) {
        return dataSource.getTaskSubject(id);
    }

    @Override
    public Subject<List<Task>> findAll() {
        return dataSource.getAllTasksSubject();
    }

    @Override
    public void save(Task task) {
        dataSource.shiftSortOrders(dataSource.getMinCompletedSortOrder(), dataSource.getMaxSortOrder(), 1);
        if(dataSource.getMinCompletedSortOrder() > 0){
            dataSource.putTask(task.withSortOrder(dataSource.getMinCompletedSortOrder() - 1));
        }else{
            dataSource.putTask(task.withSortOrder(0));
        }
    }

    @Override
    public void save(List<Task> tasks) {
        dataSource.putTasks(tasks);
    }

    @Override
    public void complete(Task task) {
        dataSource.completeTask(task.id());
    }

    @Override
    public void remove(int id) {
        dataSource.removeTask(id);
    }

    public void deleteCompletedTasks(){};
}
