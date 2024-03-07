package edu.ucsd.cse110.successorator.lib.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

public class Tasks {

    public static List<Task> insertTask(List<Task> tasks, Task task){
        tasks = shiftSortOrders(tasks, getMaxUncompletedSortOrder(tasks) + 1,
                getMaxSortOrder(tasks), 1);
        var newSortOrder = getMaxUncompletedSortOrder(tasks) + 1;
        tasks.add(task.withSortOrder(newSortOrder));
        return tasks;
    }

    public static List<Task> removeTask(List<Task> tasks, Task task){
        tasks.remove(task);
        tasks = shiftSortOrders(tasks, task.sortOrder(), getMaxSortOrder(tasks), -1);
        return tasks;
    }

    public static List<Task> completeTask(List<Task> tasks, Task task){
        tasks.remove(task);
        tasks = shiftSortOrders(tasks, task.sortOrder(), getMaxSortOrder(tasks), -1);
        tasks.add(task.withSortOrder(getMaxSortOrder(tasks) + 1).withComplete(true));
        return tasks;
    }

    public static List<Task> uncompleteTask(List<Task> tasks, Task task){
        tasks.remove(task);
        tasks = shiftSortOrders(tasks, getMaxUncompletedSortOrder(tasks) + 1,
                task.sortOrder(), 1);
        tasks.add(task.withSortOrder(getMaxUncompletedSortOrder(tasks) + 1).withComplete(false));
        return tasks;
    }

    private static List<Task> shiftSortOrders(List<Task> tasks, int from, int to, int by){
        return tasks.stream()
                .map(task -> {
                    if(task.sortOrder() >= from && task.sortOrder() <= to){
                        return task.withSortOrder(task.sortOrder() + by);
                    }
                    return task;
                })
                .collect(Collectors.toList());
    }

    private static int getMaxUncompletedSortOrder(List<Task> tasks){
        return tasks.stream()
                .filter(task -> !task.complete())
                .map(Task::sortOrder)
                .max(Integer::compareTo)
                .orElse(0);
    }

    private static int getMaxSortOrder(List<Task> tasks){
        return tasks.stream()
                .map(Task::sortOrder)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public static Integer calculateOccurrence(LocalDate date){
        int toReturn = 1;
        Month originalMonth = date.getMonth();
        date = date.minusDays(7);
        while(date.getMonth() == originalMonth){
            toReturn++;
            date = date.minusDays(7);
        }
        return toReturn;
    }

    public static Frequency convertString(String string){
        switch (string){
            case "Daily...":
                return Frequency.DAILY;
            case "Monthly...":
                return Frequency.MONTHLY;
            case "Weekly...":
                return Frequency.WEEKLY;
            case "Yearly...":
                return Frequency.YEARLY;
            default:
                return Frequency.ONE_TIME;
        }
    }
}
