package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskBuilder {
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

    public TaskBuilder(){
        id = null;
        taskName = "";
        sortOrder = 0;
        complete = false;
        activeDate = null;
        frequency = Frequency.ONE_TIME;
        dayOfWeek = null;
        dayOccurrence = null;
        creationDate = null;
        expirationDate = null;
    }
}
