package edu.ucsd.cse110.successorator.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.successorator.lib.domain.Task;

/*
This class was adapted from the FlashcardEntity class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
@Entity(tableName = "tasks")
public class TaskEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "task_name")
    public String taskName;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    @ColumnInfo(name = "complete")
    public boolean complete;

    TaskEntity(String taskName, int sortOrder, boolean complete){
        this.taskName = taskName;
        this.sortOrder = sortOrder;
        this.complete = complete;
    }

    public static TaskEntity fromTask(@NonNull Task task){
        var taskEntity = new TaskEntity(task.taskName(), task.sortOrder(), task.complete());
        taskEntity.id = task.id();
        return taskEntity;
    }

    public @NonNull Task toTask(){
        return new Task(id, taskName, sortOrder, complete);
    }
}
