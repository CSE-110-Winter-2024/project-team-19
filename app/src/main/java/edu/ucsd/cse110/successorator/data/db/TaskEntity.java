package edu.ucsd.cse110.successorator.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.successorator.lib.domain.Task;

@Entity(tableName = "tasks")
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "task_name")
    public String taskName;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    TaskEntity(String taskName, int sortOrder){
        this.taskName = taskName;
        this.sortOrder = sortOrder;
    }

    public static TaskEntity fromTask(@NonNull Task task){
        var taskEntity = new TaskEntity(task.taskName(), task.sortOrder());
        taskEntity.id = task.id();
        return taskEntity;
    }

    public @NonNull Task toTask(){
        return new Task(id, taskName, sortOrder);
    }
}
