package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/*
This class was adapted from the Flashcard class provided in CSE 110 Lab 5.
https://docs.google.com/document/d/1hpG8UJLVru_pGrT3vCMee2vjA-8HadWwjyk5gGbUatI/edit
 */
public class Task implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String taskName;
    private final int sortOrder;
    private final boolean complete;
    private final @Nullable LocalDate activeDate;
    private final Frequency frequency;
    private final @Nullable DayOfWeek dayOfWeek;
    private final @Nullable Integer dayOccurrence;
    private final @Nullable LocalDateTime creationDate;
    private final @Nullable LocalDate expirationDate;

    public Task(Integer id, String taskName, int sortOrder, boolean complete,
                LocalDate activeDate, Frequency frequency, DayOfWeek dayOfWeek,
                Integer dayOccurrence, LocalDateTime creationDate, LocalDate expirationDate){
        this.id = id;
        this.taskName = taskName;
        this.sortOrder = sortOrder;
        this.complete = complete;
        this.activeDate = activeDate;
        this.frequency = frequency;
        this.dayOfWeek = dayOfWeek;
        this.dayOccurrence = dayOccurrence;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
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

    public boolean complete(){
        return complete;
    }

    public LocalDate activeDate(){
        return activeDate;
    }

    public Frequency frequency(){
        return frequency;
    }

    public DayOfWeek dayOfWeek(){
        return dayOfWeek;
    }

    public int dayOccurrence(){
        return dayOccurrence;
    }

    public LocalDateTime creationDate(){
        return creationDate;
    }

    public LocalDate expirationDate(){
        return expirationDate;
    }

    public Task withId(Integer id){
        return new Task(id, this.taskName, this.sortOrder, this.complete, this.activeDate,
                this.frequency, this.dayOfWeek, this.dayOccurrence, this.creationDate,
                this.expirationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return sortOrder == task.sortOrder && complete == task.complete
                && Objects.equals(dayOccurrence, task.dayOccurrence) && Objects.equals(id, task.id)
                && Objects.equals(taskName, task.taskName)
                && Objects.equals(activeDate, task.activeDate)
                && frequency == task.frequency && dayOfWeek == task.dayOfWeek
                && Objects.equals(creationDate, task.creationDate)
                && Objects.equals(expirationDate, task.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, sortOrder, complete, activeDate, frequency,
                dayOfWeek, dayOccurrence, creationDate, expirationDate);
    }

    public Task withSortOrder(int sortOrder){
        return new Task(this.id, this.taskName, sortOrder, this.complete, this.activeDate,
                this.frequency, this.dayOfWeek, this.dayOccurrence, this.creationDate,
                this.expirationDate);
    }

    public Task withComplete(boolean complete){
        return new Task(this.id, this.taskName, this.sortOrder, complete, this.activeDate,
                this.frequency, this.dayOfWeek, this.dayOccurrence, this.creationDate,
                this.expirationDate);
    }

    public Task withActiveDate(LocalDate activeDate){
        return new Task(this.id, this.taskName, this.sortOrder, this.complete, activeDate,
                this.frequency, this.dayOfWeek, this.dayOccurrence, this.creationDate,
                this.expirationDate);
    }

    public Task withFrequency(Frequency frequency){
        return new Task(this.id, this.taskName, this.sortOrder, this.complete, this.activeDate,
                frequency, this.dayOfWeek, this.dayOccurrence, this.creationDate,
                this.expirationDate);
    }

    public Task withDayOccurrence(int dayOccurrence){
        return new Task(this.id, this.taskName, this.sortOrder, this.complete, this.activeDate,
                this.frequency, this.dayOfWeek, dayOccurrence, this.creationDate,
                this.expirationDate);
    }

    public Task withExpirationDate(LocalDate expirationDate){
        return new Task(this.id, this.taskName, this.sortOrder, this.complete, this.activeDate,
                this.frequency, this.dayOfWeek, this.dayOccurrence, this.creationDate,
                expirationDate);
    }
}
