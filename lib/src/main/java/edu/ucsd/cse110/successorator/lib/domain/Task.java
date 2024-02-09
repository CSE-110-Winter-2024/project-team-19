package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Task implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String taskName;
    private final int sortOrder;

    public Task(Integer id, String taskName, int sortOrder){
        this.id = id;
        this.taskName = taskName;
        this.sortOrder = sortOrder;
    }

    public @Nullable Integer id(){
        return id;
    }

    public @NonNull String taskName(){
        return taskName;
    }

    public int sortOrder(){
        return sortOrder;
    }

    public Task withId(Integer id){
        return new Task(id, this.taskName, this.sortOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return sortOrder == task.sortOrder && Objects.equals(id, task.id) && Objects.equals(taskName, task.taskName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, sortOrder);
    }

    public Task withSortOder(int sortOrder){
        return new Task(this.id, this.taskName, this.sortOrder);
    }

}
