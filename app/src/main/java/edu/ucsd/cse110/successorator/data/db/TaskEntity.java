package edu.ucsd.cse110.successorator.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.DayOfWeek;
import java.time.LocalDate;

import edu.ucsd.cse110.successorator.lib.domain.Frequency;
import edu.ucsd.cse110.successorator.lib.domain.Context;
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

    @ColumnInfo(name = "active_date")
    public LocalDate activeDate;

    @ColumnInfo(name = "frequency")
    public Frequency frequency;

    @ColumnInfo(name = "day_of_week")
    public DayOfWeek dayOfWeek;

    @ColumnInfo(name = "day_occurrence")
    public Integer dayOccurrence;

    @ColumnInfo(name = "context")
    public Context context;

    TaskEntity(String taskName, int sortOrder, boolean complete,
               LocalDate activeDate, Frequency frequency, DayOfWeek dayOfWeek,
               Integer dayOccurrence, Context context){
        this.taskName = taskName;
        this.sortOrder = sortOrder;
        this.complete = complete;
        this.activeDate = activeDate;
        this.frequency = frequency;
        this.dayOfWeek = dayOfWeek;
        this.dayOccurrence = dayOccurrence;
        this.context = context;
    }

    public static TaskEntity fromTask(@NonNull Task task){
        var taskEntity = new TaskEntity(task.taskName(), task.sortOrder(), task.complete(),
                task.activeDate(), task.frequency(), task.dayOfWeek(), task.dayOccurrence(), task.context());
        taskEntity.id = task.id();
        return taskEntity;
    }

    public @NonNull Task toTask(){
        return new Task(id, taskName, sortOrder, complete, activeDate, frequency, dayOfWeek,
                dayOccurrence, context);
    }
}
