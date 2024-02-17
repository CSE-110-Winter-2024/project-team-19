package edu.ucsd.cse110.successorator.lib.domain;

import java.util.ArrayList;
import java.util.List;

public class Tasks {
    public static List<Task> completeTaskOrder(List<Task> tasks, Task task) {
        List<Task> newTasks = new ArrayList<>(tasks.size() + 1); // Initialize with capacity
        int nextSortOrder = 1;

        for (Task t : tasks) {
            if (!t.equals(task)) {
                newTasks.add(t.withSortOrder(nextSortOrder)); // Assign the next sortOrder
                nextSortOrder++;
            }
        }
        Task compTask = task.withSortOrder(nextSortOrder);
        compTask.complete();
        newTasks.add(compTask); // Add the new task with the last sortOrder
        return newTasks;
    }

    public static void uncompleteTaskOrder(List<Task> tasks, Task task) {
        //TODO
    }

}

