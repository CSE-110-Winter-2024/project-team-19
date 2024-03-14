package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TaskBuilder {
    private Integer id;
    private String taskName;
    private int sortOrder;
    private boolean complete;
    private @Nullable LocalDate activeDate;
    private Frequency frequency;
    private @Nullable DayOfWeek dayOfWeek;
    private @Nullable Integer dayOccurrence;
    private @Nullable LocalDateTime creationDate;
    private @Nullable LocalDate expirationDate;

    public TaskBuilder(){
        id = null;
        taskName = "";
        sortOrder = 0;
        complete = false;
        activeDate = MockLocalDate.now();
        frequency = Frequency.ONE_TIME;
        dayOfWeek = MockLocalDate.now().getDayOfWeek();
        dayOccurrence = Tasks.calculateOccurrence(MockLocalDate.now());
        creationDate = LocalDateTime.of(MockLocalDate.now(), LocalTime.now());
        expirationDate = MockLocalDate.now().plusDays(1);
    }

    public TaskBuilder withTaskName(String taskName){
        this.taskName = taskName;
        return this;
    }

    public TaskBuilder withFrequency(Frequency frequency){
        this.frequency = frequency;
        if(frequency != Frequency.ONE_TIME && frequency != Frequency.PENDING)
            updateExpirationDate();
        return this;
    }

    public Task build(){
        return new Task(id, taskName, sortOrder, complete, activeDate, frequency, dayOfWeek, dayOccurrence, creationDate, expirationDate);
    }

    private void updateExpirationDate(){
        var temp = build();
        expirationDate = Tasks.nextRecurrenceDate(temp);
    }
}
